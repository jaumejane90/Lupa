/**
Copyright 2014 Jaume Jané 

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package cat.tv3.eng.rec.recomana.vidre.clustering;

import java.util.Map;
import java.util.TreeMap;

import cat.tv3.eng.rec.recomana.vidre.engine.KLDdistance;
import cat.tv3.eng.rec.recomana.vidre.engine.VidreItem;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class SearchClusterNodeBolt  extends BaseRichBolt {
	private OutputCollector _collector;
	final String host;
	final int port;
	JedisPool pool;	
	private int MAX_SIZE_OF_CLUSTERS;
	
	public SearchClusterNodeBolt(String host, int port, int max_size_of_clusters) {
		this.host = host;
		this.port = port;	
		this.MAX_SIZE_OF_CLUSTERS = max_size_of_clusters;
		
	}	
	
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		 _collector = collector; 
	     JedisPoolConfig poolConfig = new JedisPoolConfig();
	     poolConfig.setMaxActive(1);
	     poolConfig.setMaxIdle(1);
	     pool = new JedisPool(new JedisPoolConfig(),host,port);
		
	}

	@Override
	public void execute(Tuple input) {		
		String id_text = input.getStringByField("id_text");
		VidreItem distr_text = (VidreItem)input.getValueByField("distr_text");		
					
		Jedis jedis = pool.getResource();			
		
		try {
			
			Map<String,String> attr_cluster = jedis.hgetAll("ClusterBinaryTree-Arrel");				
		    String cluster_name = attr_cluster.get("cluster_ids_name");			
		    if(cluster_name == null) {    	
		    	jedis.hset("ClusterBinaryTree-Arrel", "cluster_ids_name", "Instances_arrel");
		    	jedis.sadd("Instances_arrel",id_text);
		    }
		    else if(cluster_name.equals("cluster_splited")){ 
		    	String id_left_centroid = attr_cluster.get("id_left_centroid");			
				String id_right_centroid = attr_cluster.get("id_right_centroid");		
		    	
				Double dist_left = compareKLD(distr_text,id_left_centroid);
				Double dist_right = compareKLD(distr_text,id_right_centroid);
				
				if(dist_left < dist_right) {
					String hash_left = attr_cluster.get("hash_left");	
					search_cluster_binarytree(id_text,distr_text,hash_left,2);
				}
				else {
				    String hash_right = attr_cluster.get("hash_right");		
					search_cluster_binarytree(id_text,distr_text,hash_right,2);
				}	    	
		    }
		    else { 
		    	   jedis.sadd(cluster_name,id_text);
		    	   String[] ids_noticia_cluster = jedis.smembers(cluster_name).toArray(new String[0]);	  			  
				    
				   if(ids_noticia_cluster.length < MAX_SIZE_OF_CLUSTERS) {
					   _collector.emit(new Values(id_text,distr_text,cluster_name));
				   }
				   else { 
					   VidreClusterSet dataset = clusterList_toDataset(cluster_name);
					   VidreKMeans kmeans = new VidreKMeans(2,100,new VidreItemDistance());						
					   VidreClusterSet[] clusters = kmeans.cluster(dataset);
					   
				       Map<String,String> new_attr_cluster = new TreeMap<String,String>();	
					   VidreClusterItem[] centroids = kmeans.getCentroids();
					   jedis.del(attr_cluster.get("cluster_ids_name"));					  
					
					   new_attr_cluster.put("cluster_ids_name", "cluster_splited" );
					   new_attr_cluster.put("id_left_centroid",centroids[0].getID());
					   new_attr_cluster.put("hash_left","ClusterCentroid-ID_"+centroids[0].getID()+"_level_1");
					   new_attr_cluster.put("id_right_centroid",centroids[1].getID());
					   new_attr_cluster.put("hash_right","ClusterCentroid-ID_"+centroids[1].getID()+"_level_1");				  
					   jedis.hmset("ClusterBinaryTree-Arrel", new_attr_cluster);					   
					
					   jedis.hset("ClusterCentroid-ID_"+centroids[0].getID()+"_level_1", "cluster_ids_name", "Instances_centroid_ID_"+centroids[0].getID());
				       String[] list_left_ids = clusters[0].listOfIdInstances();
				       for(int i = 0 ; i<list_left_ids.length; ++i) {
				    	   jedis.sadd("Instances_centroid_ID_"+centroids[0].getID(), list_left_ids[i]);
				       }
					  				   
				       jedis.hset("ClusterCentroid-ID_"+centroids[1].getID()+"_level_1", "cluster_ids_name", "Instances_centroid_ID_"+centroids[1].getID());
				       String[] list_right_ids = clusters[1].listOfIdInstances();
				       for(int i = 0 ; i<list_right_ids.length; ++i) {
				    	   jedis.sadd("Instances_centroid_ID_"+centroids[1].getID(), list_right_ids[i]);
				       }
					  
				     			       
					   if(clusters[0].containsIdVidreInstance(id_text)) {
						   _collector.emit(new Values(id_text,distr_text,"Instances_centroid_ID_"+centroids[0].getID()));
					   }
					   else {
						   _collector.emit(new Values(id_text,distr_text,"Instances_centroid_ID_"+centroids[1].getID()));						   
					   }					   
				   }			
		    }					
		} finally {
			pool.returnResource(jedis);
		}   	
		
	}
	
	public VidreClusterSet clusterList_toDataset(String cluster_name){		
		
		VidreClusterSet dataset = new VidreClusterSet();
		String[] ids_noticia_cluster = new String[0];
		
		Jedis jedis = pool.getResource();		
		try{			
			 ids_noticia_cluster = jedis.smembers(cluster_name).toArray(new String[0]);
			 VidreClusterItem[] instances = new VidreClusterItem[ids_noticia_cluster.length];		
			 for(int i = 0; i<ids_noticia_cluster.length; ++i){
				 instances[i] = new VidreClusterItem(ids_noticia_cluster[i]);
				 Map<String,String> distr_prob_map = jedis.hgetAll("distr_text-id-"+ids_noticia_cluster[i]);				
				 instances[i].addTreeMapStrings(distr_prob_map);
				 dataset.addInstance(instances[i]);
			 }
		} finally {
			pool.returnResource(jedis);
		} 	 
		return dataset;
	}
	
	public void search_cluster_binarytree(String id_text, VidreItem distr_text,String hash_name,Integer h){
		Jedis jedis = pool.getResource();			
		
		try {
			
			Map<String,String> attr_cluster = jedis.hgetAll(hash_name);				
		    String cluster_name = attr_cluster.get("cluster_ids_name");	
		    if(cluster_name.equals("cluster_splited")){ 
		    	String id_left_centroid = attr_cluster.get("id_left_centroid");	
				String id_right_centroid = attr_cluster.get("id_right_centroid");
		    	
				Double dist_left = compareKLD(distr_text,id_left_centroid);
				Double dist_right = compareKLD(distr_text,id_right_centroid);
				
				if(dist_left < dist_right) {
					String hash_left = attr_cluster.get("hash_left");		
					search_cluster_binarytree(id_text,distr_text,hash_left,h+1);
				}
				else {
				    String hash_right = attr_cluster.get("hash_right");		
					search_cluster_binarytree(id_text,distr_text,hash_right,h+1);
				}	    	
		    }
		    else { 
		    	   jedis.sadd(cluster_name,id_text);
		    	   String[] ids_noticia_cluster = jedis.smembers(cluster_name).toArray(new String[0]);				  
				    
				   if(ids_noticia_cluster.length < MAX_SIZE_OF_CLUSTERS) {
					   _collector.emit(new Values(id_text,distr_text,cluster_name));
				   }
				   else { 
					   VidreClusterSet dataset = clusterList_toDataset(cluster_name);
					   VidreKMeans kmeans = new VidreKMeans(2,100,new VidreItemDistance());						
					   VidreClusterSet[] clusters = kmeans.cluster(dataset);
					   
					   Map<String,String> new_attr_cluster = new TreeMap<String,String>();	
					   VidreClusterItem[] centroids = kmeans.getCentroids();
					   jedis.del(attr_cluster.get("cluster_ids_name"));
					
					   new_attr_cluster.put("cluster_ids_name", "cluster_splited" );
					   new_attr_cluster.put("id_left_centroid",centroids[0].getID());
					   new_attr_cluster.put("hash_left","ClusterCentroid-ID_"+centroids[0].getID()+"_level_"+h);
					   new_attr_cluster.put("id_right_centroid",centroids[1].getID());
					   new_attr_cluster.put("hash_right","ClusterCentroid-ID_"+centroids[1].getID()+"_level_"+h);				  
					   jedis.hmset(hash_name, new_attr_cluster);					   
					
					   jedis.hset("ClusterCentroid-ID_"+centroids[0].getID()+"_level_"+h, "cluster_ids_name", "Instances_centroid_ID_"+centroids[0].getID());
				       String[] list_left_ids = clusters[0].listOfIdInstances();
				       for(int i = 0 ; i<list_left_ids.length; ++i) {
				    	   jedis.sadd("Instances_centroid_ID_"+centroids[0].getID(), list_left_ids[i]);
				       }
					  				   
				       jedis.hset("ClusterCentroid-ID_"+centroids[1].getID()+"_level_"+h, "cluster_ids_name", "Instances_centroid_ID_"+centroids[1].getID());
				       String[] list_right_ids = clusters[1].listOfIdInstances();
				       for(int i = 0 ; i<list_right_ids.length; ++i) {
				    	   jedis.sadd("Instances_centroid_ID_"+centroids[1].getID(), list_right_ids[i]);
				       }
					  				     			       
					   if(clusters[0].containsIdVidreInstance(id_text)) {
						   _collector.emit(new Values(id_text,distr_text,"Instances_centroid_ID_"+centroids[0].getID()));
					   }
					   else {
						   _collector.emit(new Values(id_text,distr_text,"Instances_centroid_ID_"+centroids[1].getID()));
						   
					   }
					   
				   }			
		    }					
		} finally {
			pool.returnResource(jedis);
		}       
		
	}
	
	public Double compareKLD(VidreItem vidreitem, String id_text){		
		TreeMap<String, Double> distr_prob = new TreeMap<String,Double>();
		String Key;
		Double Value;
		Double total=0.0;
		Jedis jedis = pool.getResource();
		try {
			Map<String,String> distr_prob_map = jedis.hgetAll("distr_text-id-"+id_text);
			for(Map.Entry<String, String> entry : distr_prob_map.entrySet()){					
					Key = entry.getKey();
					Value= Double.parseDouble(entry.getValue());			
					distr_prob.put(Key, Value);
					total+=Value;				
			}			
				
		} finally {
			pool.returnResource(jedis);
		}       		
		
		VidreItem to_compare = new VidreItem();
		to_compare.setWordCounts(distr_prob);
		to_compare.setSize(total);		
		
		KLDdistance calculador = new KLDdistance(vidreitem,to_compare);
		return calculador.distance();
		
	}
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		  declarer.declare(new Fields("id_text","distr_text","cluster_name")); 
		
	}

}
