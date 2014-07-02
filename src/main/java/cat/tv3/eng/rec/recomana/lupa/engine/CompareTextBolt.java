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

package cat.tv3.eng.rec.recomana.lupa.engine;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;


public class CompareTextBolt extends BaseRichBolt {
	private OutputCollector _collector;
	final String host;
	final int port;
	JedisPool pool;
	private int NUMBER_OF_RECOMENDATIONS = 4;
	
	public CompareTextBolt() {
		this.host = "localhost";
		this.port = 6379;
	}
	
	public CompareTextBolt(String host, int port) {
		this.host = host;
		this.port = port;		
	}	
	
	public CompareTextBolt(String host, int port, int number_recommendations) {
		this.host = host;
		this.port = port;	
		this.NUMBER_OF_RECOMENDATIONS = number_recommendations;
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
		String id_compare = input.getStringByField("id_compare");
		if(!id_text.equals(id_compare)) {			
			TreeMap<String, Double> distr_prob = new TreeMap<String,Double>();
			String Key;
			Double Value;
			Double total=0.0;
			Jedis jedis = pool.getResource();
			try {
				Map<String,String> distr_prob_map = jedis.hgetAll("distr_text-id-"+id_compare);
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
			
			KLDdistance calculador = new KLDdistance(distr_text,to_compare);
			Double distancia = calculador.distance();
			
			Long actual_number_recomendations;
			
			Set<redis.clients.jedis.Tuple> worst_recommendation;
			jedis = pool.getResource();
			try {
				
				actual_number_recomendations = jedis.zcard("recommendations_"+id_text);
				if(actual_number_recomendations <= NUMBER_OF_RECOMENDATIONS){
					jedis.zadd("recommendations_"+id_text, distancia, id_compare);				
				}
				else {
					worst_recommendation = jedis.zrangeWithScores("recommendations_"+id_text, NUMBER_OF_RECOMENDATIONS , NUMBER_OF_RECOMENDATIONS );
					Iterator<redis.clients.jedis.Tuple> it = worst_recommendation.iterator(); 
					if (it.hasNext()){   
						    redis.clients.jedis.Tuple t = it.next();
				            if(t.getScore() > distancia){
				            	jedis.zrem("recommendations_"+id_text, t.getElement());
				            	jedis.zadd("recommendations_"+id_text, distancia, id_compare);
				            }		                
				     }
				}
				
				
				actual_number_recomendations = jedis.zcard("recommendations_"+id_compare);
				if(actual_number_recomendations <= NUMBER_OF_RECOMENDATIONS){
					jedis.zadd("recommendations_"+id_compare, distancia, id_text);				
				}
				else {
					worst_recommendation = jedis.zrangeWithScores("recommendations_"+id_compare, NUMBER_OF_RECOMENDATIONS , NUMBER_OF_RECOMENDATIONS );
					Iterator<redis.clients.jedis.Tuple> it = worst_recommendation.iterator(); 
					if (it.hasNext()){   
						    redis.clients.jedis.Tuple t = it.next();
				            if(t.getScore() > distancia){
				            	jedis.zrem("recommendations_"+id_compare, t.getElement());
				            	jedis.zadd("recommendations_"+id_compare, distancia, id_text);
				            }		                
				     }
				}				
					
			} finally {
				pool.returnResource(jedis);
			}   
		}	
		
	}
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		  declarer.declare(new Fields("id_text","id_compare","distance")); 
		
	}
	
}
