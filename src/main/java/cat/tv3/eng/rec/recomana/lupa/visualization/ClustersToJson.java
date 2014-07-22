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

package cat.tv3.eng.rec.recomana.lupa.visualization;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import redis.clients.jedis.Jedis;

public class ClustersToJson {
	public static void main(String[] args) throws IOException {	
		
		String host = args[0];
	    int port = Integer.parseInt(args[1]);
		Jedis jedis = new Jedis(host, port,20000);
		
		// Cluster to binary tree visualitzation
		Map<String,String> attr_cluster = jedis.hgetAll("ClusterBinaryTree-Arrel");				
	    String cluster_name = attr_cluster.get("cluster_ids_name");  
	    
	    JSONObject cluster;
	    if(!cluster_name.equals("cluster_splited")) {
	    	 cluster = new JSONObject();	    	
	 	     cluster.put("name", "arrel");	 	     	 	    
	    }
	    else {
	    	String id_left_centroid = attr_cluster.get("id_left_centroid");			
			String id_right_centroid = attr_cluster.get("id_right_centroid");			
	    	
			String hash_left = attr_cluster.get("hash_left");		
			String hash_right = attr_cluster.get("hash_right");		
			
	    	cluster = new JSONObject();
	        cluster.put("name", "arrel");
	  		cluster.put("children", hashToJSONArrayRepresentationBinaryTree(id_left_centroid,hash_left,jedis,id_right_centroid,hash_right));
	  			  	  
	    }	
		
	    jedis.disconnect();
			
		Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data_toVisualize/cluster.json"), "UTF-8"));
		try {
		    out.write(cluster.toJSONString());
		} finally {
		    out.close();
		}
		
		
	    
			
		
	}
	
	
	
	public static JSONArray hashToJSONArrayRepresentationBinaryTree(String id ,String hash,Jedis jedis,String id2 ,String hash2 ){
		 JSONArray result = new JSONArray();
		 JSONObject info;
		 Map<String,String> attr_cluster = jedis.hgetAll(hash);				
		 String cluster_name = attr_cluster.get("cluster_ids_name");		
		if(!cluster_name.equals("cluster_splited")) {
			 info = new JSONObject();
			 info.put("name", "Centroid " + id);	
			 
			 String[] instance_group_keys = jedis.keys("Instances_centroid_ID_"+id).toArray(new String[0]);			
			 info.put("children",fullestoArray(instance_group_keys,jedis));
	    }
	    else {
	    	String id_left_centroid = attr_cluster.get("id_left_centroid");			
			String id_right_centroid = attr_cluster.get("id_right_centroid");			
	    	
			String hash_left = attr_cluster.get("hash_left");		
			String hash_right = attr_cluster.get("hash_right");
			
			info = new JSONObject();
			info.put("name", "Centroid " + id);
			info.put("children", hashToJSONArrayRepresentationBinaryTree(id_left_centroid,hash_left,jedis,id_right_centroid,hash_right));
			
			
	    }
		result.add(info);		
		
		
		attr_cluster = jedis.hgetAll(hash2);				
		cluster_name = attr_cluster.get("cluster_ids_name");
		    
		if(!cluster_name.equals("cluster_splited")) {
			 info = new JSONObject();
			 info.put("name", "Centroid " + id2);
			 
			 String[] instance_group_keys = jedis.keys("Instances_centroid_ID_"+id2).toArray(new String[0]);			
			 info.put("children",fullestoArray(instance_group_keys,jedis));
	    }
	    else {
	    	String id_left_centroid = attr_cluster.get("id_left_centroid");			
			String id_right_centroid = attr_cluster.get("id_right_centroid");			
	    	
			String hash_left = attr_cluster.get("hash_left");		
			String hash_right = attr_cluster.get("hash_right");
					
			
			info = new JSONObject();
			info.put("name", "Centroid " + id2);
			info.put("children", hashToJSONArrayRepresentationBinaryTree(id_left_centroid,hash_left,jedis,id_right_centroid,hash_right));
			
	    }
		result.add(info);		
		return result;
	}	
	
	
	public static JSONArray fullestoArray(String[] instance_group_keys,Jedis jedis){
		JSONArray result = new JSONArray(); 
		JSONObject info = new JSONObject(); 	
		for (int i = 0 ; i < instance_group_keys.length; ++i) {				
			String[] instancesOfGroup = jedis.smembers(instance_group_keys[i]).toArray(new String[0]);		
			for(int j = 0 ; j<instancesOfGroup.length; ++j) {				
				info = new JSONObject(); 
				info.put("name", instancesOfGroup[j]);				
				result.add(info);  
			}				
		}		
		return result;
	}

	public static double truncate(double value, int places) {
		    double multiplier = Math.pow(10, places);
		    return Math.floor(multiplier * value) / multiplier;
	}
}
