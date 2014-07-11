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

package cat.tv3.eng.rec.recomana.lupa;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import cat.calidos.storm.freeling.socket.FreelingBolt;
import cat.tv3.eng.rec.recomana.lupa.clustering.DispatcherClusterBolt;
import cat.tv3.eng.rec.recomana.lupa.clustering.SearchClusterNodeBolt;
import cat.tv3.eng.rec.recomana.lupa.engine.CalcProbBolt;
import cat.tv3.eng.rec.recomana.lupa.engine.CompareTextBolt;
import cat.tv3.eng.rec.recomana.lupa.io.TextRedisSpout;

public class StormLupaClusteredTopology {
	
	public static void main(String[] args) {
		/*
				Eclipse :
					program arguments:
						@IP redis host
						redis port
						@IP redis freeling
						freeling port	
						maximum size of cluster
					VM arguments
							-Dfile.encoding=UTF-8 
		*/
		 if (args.length < 5 ) {
	            throw new RuntimeException("Invalid number of arguments(redis_host,redis_port,freeling_host,freeling_port,maximum_size_of_cluster,[deploy_remote_name])");   	
	     }
	     
		 String redis_host = args[0];
	     int redis_port = Integer.parseInt(args[1]);
	     String freeling_host = args[2];
	     int freeling_port = Integer.parseInt(args[3]);
	     int max_size_of_clusters = Integer.parseInt(args[4]); 
	
	     TopologyBuilder b = new TopologyBuilder();
	     b.setSpout("TextRedisSpout", new TextRedisSpout(redis_host, redis_port)); 
	     b.setBolt("FreelingBolt", new FreelingBolt(freeling_host,freeling_port)).shuffleGrouping("TextRedisSpout"); 
	     //b.setBolt("FreelingBolt", new FreelingBoltSimulator()).shuffleGrouping("TextRedisSpout"); 
	     b.setBolt("CalcProBolt",new CalcProbBolt(redis_host,redis_port)).shuffleGrouping("FreelingBolt");
	     b.setBolt("SearchClusterNodeBolt", new SearchClusterNodeBolt(redis_host,redis_port,max_size_of_clusters)).shuffleGrouping("CalcProBolt");
	     b.setBolt("DispatcherClusterBolt", new DispatcherClusterBolt(redis_host,redis_port)).shuffleGrouping("SearchClusterNodeBolt"); 
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
	    		 Utils.sleep(10000000);	
	    	 } finally {
	    		 try {
	    			 cluster.shutdown();
	    		 } catch (Exception e) {}			
	    	 }
	     }
	}		
}
