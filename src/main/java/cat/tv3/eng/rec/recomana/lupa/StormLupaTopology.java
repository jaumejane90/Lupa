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

import cat.tv3.eng.rec.recomana.lupa.engine.*;
import cat.tv3.eng.rec.recomana.lupa.io.TextRedisSpout;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import cat.calidos.storm.freeling.socket.FreelingBolt;

public class StormLupaTopology {
	
	public static void main(String[] args) {
		/*
			Eclipse :
				program arguments:
					    @IP redis host
						redis port
						@IP redis freeling
						freeling port	
						language(en,ca,es)				
				VM arguments
						-Dfile.encoding=UTF-8 
		*/
		if (args.length < 5 ) {
			throw new RuntimeException("Invalid number of arguments(redis_host,redis_port,freeling_host,freeling_port,lang,[deploy_remote_name])");   	
	    }
	     
		String redis_host = args[0];
		int redis_port = Integer.parseInt(args[1]);
		String freeling_host = args[2];
		int freeling_port = Integer.parseInt(args[3]);
		  String language = args[4];
		
 
		TopologyBuilder b = new TopologyBuilder();
		b.setSpout("TextRedisSpout", new TextRedisSpout(redis_host, redis_port)); 
		b.setBolt("FreelingBolt", new FreelingBolt(freeling_host,freeling_port)).shuffleGrouping("TextRedisSpout"); 
		//b.setBolt("FreelingBolt", new FreelingBoltSimulator()).shuffleGrouping("TextRedisSpout"); 
		b.setBolt("CalcProBolt",new CalcProbBolt(redis_host,redis_port,language)).shuffleGrouping("FreelingBolt");
		b.setBolt("DispatcherBolt", new DispatcherBolt(redis_host,redis_port)).shuffleGrouping("CalcProBolt"); 
		b.setBolt("CompareTextBolt", new CompareTextBolt(redis_host,redis_port)).shuffleGrouping("DispatcherBolt");
		   
		if(args!=null && args.length > 5) { 
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
		else {  //Local
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
