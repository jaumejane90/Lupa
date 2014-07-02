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

package cat.tv3.eng.rec.recomana.lupa.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Test;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Tuple;
import cat.calidos.storm.freeling.socket.FreelingBolt;
import cat.tv3.eng.rec.recomana.lupa.engine.CalcProbBolt;
import cat.tv3.eng.rec.recomana.lupa.engine.CompareTextBolt;
import cat.tv3.eng.rec.recomana.lupa.engine.DispatcherBolt;
import cat.tv3.eng.rec.recomana.lupa.io.TextRedisSpout;



public class VidreRecommendationITest {
	public static JedisPool pool;
	
	@Test
	public void testInTopology() throws InterruptedException {		
		 /*TEST FROM ECLIPSE -> RUN CONFIGURATION(Arguments -> VM Arguments) :
		 	-Dfile.encoding=UTF-8
			-Dredis_host=172.21.110.182
			-Dredis_port=6379
			-Dfreeling_host=172.21.110.182
			-Dfreeling_port=5050
		*/
		
		 /* TEST FROM MVN TERMINAL
		  * mvn package -Dredis_freeling_host=172.21.110.182 -Dredis_port=6379 -Dfreeling_port=5050
		  */
		
		 /*
		  *  Defaults  redis_freeling_host=localhost redis_port=6379 freeling_port=5050
		  * 
		  */
		
		 String redis_host = System.getProperty("redis_host");		
         int redis_port = Integer.parseInt(System.getProperty("redis_port"));   
         String freeling_host = System.getProperty("freeling_host");		
         int freeling_port = Integer.parseInt(System.getProperty("freeling_port"));
	     
	     JedisPoolConfig poolConfig = new JedisPoolConfig();
	     poolConfig.setMaxActive(1);
	     poolConfig.setMaxIdle(1);
	     pool = new JedisPool(new JedisPoolConfig(),redis_host,redis_port);
		
	        
		 
	    TopologyBuilder b = new TopologyBuilder();
	    b.setSpout("TextRedisSpout", new TextRedisSpout(redis_host, redis_port)); 
		b.setBolt("FreelingBolt", new FreelingBolt(freeling_host,freeling_port)).shuffleGrouping("TextRedisSpout"); 
		b.setBolt("CalcProBolt",new CalcProbBolt(redis_host,redis_port)).shuffleGrouping("FreelingBolt");
		b.setBolt("DispatcherBolt", new DispatcherBolt(redis_host,redis_port)).shuffleGrouping("CalcProBolt"); 
		b.setBolt("CompareTextBolt", new CompareTextBolt(redis_host,redis_port,8)).shuffleGrouping("DispatcherBolt");
	       
		LocalCluster cluster = new LocalCluster();
		
		
		try {
			// Build topology
			cluster.submitTopology("test", new Config(), b.createTopology());			
			
			List<TestTextInstance> dataset  = Datasets.getRecommendationSamples();
			for (TestTextInstance instance : dataset) {
				insertTextToRedis(instance);
			} 		
			
			
			boolean topologyFinished = false;
			Jedis jedis;
			while(!topologyFinished){
				Thread.sleep(1000);
				jedis = pool.getResource();
				try {	
					Long recommendations11 = jedis.zcard("recommendations_11");	
					Long recommendations22 = jedis.zcard("recommendations_22");
					Long recommendations33 = jedis.zcard("recommendations_33");
					if(recommendations11 == 8 && recommendations22 == 8 && recommendations33 == 8) {
						topologyFinished = true;
					}								 		
				} finally {
					pool.returnResource(jedis);
				} 
			}
			
			
			Integer[] expected_reco_11 = new Integer[]{13,12,31,33,32,23,22,21};
			Integer[] expected_reco_22 = new Integer[]{21,23,33,12,32,31,13,11};
			Integer[] expected_reco_33 = new Integer[]{32,31,12,13,11,23,22,21};
			
			testRecommendation(11 , expected_reco_11);	
			testRecommendation(22, expected_reco_22);	
			testRecommendation(33, expected_reco_33);
			
			
		}finally {
			/*try {				
				cluster.shutdown();				
			} catch (Exception e) {}*/	
		}	
	}


	private static void insertTextToRedis(TestTextInstance instance){
		Jedis jedis = pool.getResource();
		try {				
			Integer id = instance.getId();
			String tittle = instance.getTittle();
			String text = instance.getText();
			
			jedis.zadd("text_id_list", id, id.toString());
			Map<String,String> text_attr = new TreeMap<String,String>();
			text_attr.put("id", id.toString());
			text_attr.put("tittle", tittle);
			text_attr.put("text", text);
			jedis.hmset(id.toString(), text_attr);				
			jedis.rpush("text_queue",id.toString());
			
			
						 		
		} finally {
			pool.returnResource(jedis);
		} 
	}
	
	protected static void testRecommendation(Integer id, Integer[] expected_recommendations) {			
		Jedis jedis = pool.getResource();
		try {			
			Set<Tuple> results = jedis.zrangeWithScores("recommendations_" + id.toString(), 0 , -1);		
			java.util.Iterator<Tuple> it = results.iterator(); 			
			boolean miss = false;
			int i = 0;
			while (it.hasNext() && !miss){ 				
			    redis.clients.jedis.Tuple t = it.next();			   
			    if(Integer.parseInt(t.getElement()) != expected_recommendations[i]) {			    	
			    	miss = true;
			    }			    
			    ++i;
		     }
			if(!miss) {
				assertEquals(1, 1, 0.01);	
			}
			else{
				assertEquals(id, 0, 0.01);	
			}
						
		}finally {
			pool.returnResource(jedis);
		}		
	}
}
