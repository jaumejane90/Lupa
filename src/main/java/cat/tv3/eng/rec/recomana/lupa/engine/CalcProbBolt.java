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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cat.calidos.storm.freeling.FlAnalyzedSentence.FlAnalyzedSentence;
import cat.tv3.eng.rec.recomana.lupa.test.Datasets;
import cat.tv3.eng.rec.recomana.lupa.test.TestTextInstance;
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

public class CalcProbBolt extends BaseRichBolt {
	private final static String STOP_WORDS_EN = "StopWordsEn.txt";
	private OutputCollector _collector;
	final String host;
	final int port;
	JedisPool pool;
	
	public CalcProbBolt(String host, int port) {
		this.host = host;
		this.port = port;		
	}
	

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		 _collector = collector; 
		 JedisPoolConfig poolConfig = new JedisPoolConfig();
	     poolConfig.setMaxActive(1);
	     poolConfig.setMaxIdle(1);
	     pool = new JedisPool(new JedisPoolConfig(),host,port);
	     stopWordsToRedis();
		
	}

	@Override
	public void execute(Tuple input) {	
		FlAnalyzedSentence freeling_text = (FlAnalyzedSentence)input.getValueByField("FlAnalyzedSentence");
		String id_text = freeling_text.getOriginalTokens().get(freeling_text.getOriginalTokens().size()-1);		
		List<String> list_words = freeling_text.getListStringMorfologicText();
	    list_words.remove(list_words.size()-1);
		LupaItem distr_prob_text = new LupaItem(list_words);		
				
		Jedis jedis = pool.getResource();
		try {	
			jedis.del(id_text);				
		} finally {
			pool.returnResource(jedis);
		}      
		
		Map<String,String> prob_redis = new TreeMap<String,String>();
		String key;
		Double value;
		Double total=0.0;
		jedis = pool.getResource();	
		try {	
			for(Map.Entry<String, Double> entry : distr_prob_text.getWordCounts().entrySet()){
				key = entry.getKey();
				value= entry.getValue();
				if(!key.matches("(?!#)\\p{Punct}") && !jedis.hexists("StopWordsEn", key)) {
					prob_redis.put(key, value.toString());	
					total+=value;
				}
			}
			distr_prob_text.setSize(total);
			jedis.hmset("distr_text-id-"+id_text, prob_redis);					
		} finally {
			pool.returnResource(jedis);
		}      		
        
		_collector.emit(new Values(id_text,distr_prob_text));		
		
	}
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		  declarer.declare(new Fields("id_text","distr_text")); 
		
	}
	
	public void stopWordsToRedis(){
		InputStream is = Datasets.class.getClassLoader().getResourceAsStream(STOP_WORDS_EN);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					Jedis jedis = pool.getResource();
					try {	
						jedis.hset("StopWordsEn", line, line);
					} finally {
						pool.returnResource(jedis);
					}      
				
				} catch (Exception ex) {
					System.err.println("Skipped twitter sample because it can't be parsed : " + line);
				}
			}			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				is.close();
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
}
