package cat.tv3.eng.rec.recomana.lupa.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import cat.calidos.storm.freeling.socket.FreelingBolt;
import cat.tv3.eng.rec.recomana.lupa.clustering.DispatcherClusterBolt;
import cat.tv3.eng.rec.recomana.lupa.clustering.SearchClusterNodeBolt;
import cat.tv3.eng.rec.recomana.lupa.engine.CalcProbBolt;
import cat.tv3.eng.rec.recomana.lupa.engine.CompareTextBolt;
import cat.tv3.eng.rec.recomana.lupa.io.TextRedisSpout;

public class LupaFullBooksITest {
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
	     b.setBolt("SearchClusterNodeBolt", new SearchClusterNodeBolt(redis_host,redis_port,30)).shuffleGrouping("CalcProBolt");
		 b.setBolt("DispatcherClusterBolt", new DispatcherClusterBolt(redis_host,redis_port)).shuffleGrouping("SearchClusterNodeBolt"); 
	     b.setBolt("CompareTextBolt", new CompareTextBolt(redis_host,redis_port)).shuffleGrouping("DispatcherClusterBolt");
	         
		LocalCluster cluster = new LocalCluster();
		
		
		try {
			// Build topology
			cluster.submitTopology("test", new Config(), b.createTopology());			
			
			List<TestTextInstance> dataset  = Datasets.getFullBooksSamples();
			//Book 1 -> 56 parts , Book 2 -> 49 parts , Book 3 -> 33 parts
			for(int i=0 ; i<10; ++i) {				
				insertTextToRedis(dataset.get(i));
			}
			for(int i=0 ; i<10; ++i) {
				//System.out.println(dataset.get(56+i).getId());
				insertTextToRedis(dataset.get(56+i));
			}
			for(int i=0 ; i<10; ++i) {
				//System.out.println(dataset.get(56+49+i).getId());
				insertTextToRedis(dataset.get(56+49+i));
			}
			
			for (int i = 10 ; i < dataset.size(); ++i) {	
				if((i>=10 && i < 56) || (i >= 66 && i < 56+49) || (i >= 56+49)) {
					//System.out.println(dataset.get(i).getId());
					insertTextToRedis(dataset.get(i));
				}
			} 		
			
			//INSERTAR ORDRE
			/*for (TestTextInstance instance : dataset) {
				insertTextToRedis(instance);
			}*/
			
			
			Thread.sleep(300000);
						
			
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
	
	protected static void testClustering(Integer id, List<String> expected_clustering) {		
		Jedis jedis = pool.getResource();
		try {		
			String[] valuesOfCentroid = jedis.smembers("Instances_centroid_ID_"+id.toString()).toArray(new String[0]);		
			
			boolean miss = false;
			int i = 0;
			while (i<valuesOfCentroid.length && !miss){ 	
				if(!expected_clustering.contains(valuesOfCentroid[i])){
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
