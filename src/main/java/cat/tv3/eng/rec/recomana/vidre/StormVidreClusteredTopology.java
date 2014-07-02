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

package cat.tv3.eng.rec.recomana.vidre;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import cat.calidos.storm.freeling.socket.FreelingBolt;
import cat.tv3.eng.rec.recomana.vidre.clustering.DispatcherClusterBolt;
import cat.tv3.eng.rec.recomana.vidre.clustering.SearchClusterNodeBolt;
import cat.tv3.eng.rec.recomana.vidre.engine.CalcProbBolt;
import cat.tv3.eng.rec.recomana.vidre.engine.CompareTextBolt;
import cat.tv3.eng.rec.recomana.vidre.io.TextRedisSpout;

public class StormVidreClusteredTopology {
	
	public static void main(String[] args) {
		
		 if (args.length < 5 ) {
	            throw new RuntimeException("Invalid number of arguments(redis_host,redis_port,freeling_host,freeling_port,maximum_size_of_cluster,[deploy_remote_name])");   	
	     }
	     
		 String redis_host = args[0];
	     int redis_port = Integer.parseInt(args[1]);
	     String freeling_host = args[2];
	     int freeling_port = Integer.parseInt(args[3]);
	     int max_size_of_clusters = Integer.parseInt(args[4]);
	    
	     
	   
	
	     TopologyBuilder b = new TopologyBuilder();
	     // Read from Redis text and send to topology    
	     b.setSpout("TextRedisSpout", new TextRedisSpout(redis_host, redis_port)); 
	     // Envia ID , FreelingObject
	      b.setBolt("FreelingBolt", new FreelingBolt(freeling_host,freeling_port)).shuffleGrouping("TextRedisSpout"); //Envia text Freeling(ID LAST TOKEN)
	     //b.setBolt("FreelingBolt", new FreelingBoltSimulator()).shuffleGrouping("NoticiesRedisSpout"); 
	     
	    // Calcular funcio de probabilitats i enviar -> ID,V(prob(string,integer)). Tamb� guardem en un hash a redis les probabilitats('distr_paraules-id-ID(Key,Value)')
	     b.setBolt("CalcProBolt",new CalcProbBolt(redis_host,redis_port)).shuffleGrouping("FreelingBolt");
	     
	  	 //CHECK BINARY TREE -> SEARCH CLUSTER TO COMPARE AND ADD INTO CLUSTER (if cluster >> TAM) SPLIT WITH K MEANS
	    //Search Cluster // In <- ID voabulary) Out -> ID vocabulary NameClusterListIDS
	     b.setBolt("SearchClusterNodeBolt", new SearchClusterNodeBolt(redis_host,redis_port,max_size_of_clusters)).shuffleGrouping("CalcProBolt");
		    
	   	     
	     b.setBolt("DispatcherClusterBolt", new DispatcherClusterBolt(redis_host,redis_port)).shuffleGrouping("SearchClusterNodeBolt"); // Envia id,Freeling(noticia) + {ids} una per cada tupla to compare
	     
	     //Per cada id + prob + id(carregues prob) -> Calcules DISTANCIA i guardes a HASH ID -> 5 RECOMANACIONS SI es < sobrescius sino no es guarda
	     b.setBolt("CompareTextBolt", new CompareTextBolt(redis_host,redis_port)).shuffleGrouping("DispatcherClusterBolt");
	    
		if(args!=null && args.length > 5) {   //Storm Remot
			Config conf = new Config();
	        conf.setDebug(true);
	        conf.setNumWorkers(3); 
	        try {
				StormSubmitter.submitTopology(args[5], conf, b.createTopology());
			} catch (AlreadyAliveException e) {				
				e.printStackTrace();
			} catch (InvalidTopologyException e) {				
				e.printStackTrace();
			}       
	        
		}
		else {   //Storm Local
			LocalCluster cluster = new LocalCluster();
			try {	
				cluster.submitTopology("test", new Config(), b.createTopology());
				Utils.sleep(1000000);	
			} finally {
				try {
				cluster.shutdown();
				} catch (Exception e) {}			
			}
		}
	}
		
}
