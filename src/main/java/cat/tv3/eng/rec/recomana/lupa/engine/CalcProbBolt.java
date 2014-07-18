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
	private final static String STOP_WORDS_CA = "StopWordsCa.txt";
	private final static String STOP_WORDS_ES = "StopWordsEs.txt";
	private final static Integer SIZE_OF_REPRESENTATION_TEXT = 1000;
	private OutputCollector _collector;
	String lang;
	final String host;
	final int port;
	JedisPool pool;
	
	/*public CalcProbBolt(String host, int port) {
		this.host = host;
		this.port = port;		
	}*/
	
	public CalcProbBolt(String host, int port,String language) {
		this.host = host;
		this.port = port;
		if(language!=null) {
			this.lang = language;
		}
		else {
			this.lang="en";
		}
		
	}
	

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		 _collector = collector; 
		 JedisPoolConfig poolConfig = new JedisPoolConfig();
	     poolConfig.setMaxActive(32);
	     poolConfig.setMaxIdle(32);
	     pool = new JedisPool(new JedisPoolConfig(),host,port,20000);
	     stopWordsToRedis();
		
	}

	@Override
	public void execute(Tuple input) {	
		FlAnalyzedSentence freeling_text = (FlAnalyzedSentence)input.getValueByField("FlAnalyzedSentence");
		String id_text = freeling_text.getOriginalTokens().get(freeling_text.getOriginalTokens().size()-1);		
		List<String> list_words = freeling_text.getListStringMorfologicText();
	    list_words.remove(list_words.size()-1);
		LupaItem distr_prob_text = new LupaItem(list_words);	
		LupaItem filtred_text = new LupaItem();	
		
		Map<String,String> prob_redis = new TreeMap<String,String>();
		TreeMap<String, Double> distr_prob_text_filtred = new TreeMap<String, Double>();	
		String key;
		Double value;
		Double total=0.0;
				
		Jedis jedis = pool.getResource();
		try {	
			String tittle = jedis.hget(id_text, "tittle");
			String text = jedis.hget(id_text, "text");				
			String new_text = text.substring(0, Math.min(text.length(), SIZE_OF_REPRESENTATION_TEXT));
			jedis.del(id_text);
			Map<String,String> id_representation = new TreeMap<String,String>();
			id_representation.put("id", id_text);
			id_representation.put("tittle", tittle);
			id_representation.put("text", new_text);			
			jedis.hmset("hash_id_" + id_text, id_representation);
			
			for(Map.Entry<String, Double> entry : distr_prob_text.getWordCounts().entrySet()){
				key = entry.getKey();
				value= entry.getValue();
				
				if(!key.matches("(?!#)\\p{Punct}") && !jedis.hexists("StopWords", key) && key.length()>1) {
					prob_redis.put(key, value.toString());	
					total+=value;
					distr_prob_text_filtred.put(key, value);
				}
				
			}
			
			filtred_text.setWordCounts(distr_prob_text_filtred);
			filtred_text.setSize(total);
			System.out.println("CALC PROB BOLT : id -> " + id_text + " prob_redis_size -> " + prob_redis.size());
			jedis.hmset("distr_text-id-"+id_text, prob_redis);			
			
		} finally {
			pool.returnResource(jedis);
		}      
				
				
        
		_collector.emit(new Values(id_text,filtred_text));		
		
	}
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		  declarer.declare(new Fields("id_text","distr_text")); 
		
	}
	
	public void stopWordsToRedis(){
		InputStream is;		
		if(lang.equals("ca")){
			is = Datasets.class.getClassLoader().getResourceAsStream(STOP_WORDS_CA);
		}
		else if(lang.equals("es")){
			is = Datasets.class.getClassLoader().getResourceAsStream(STOP_WORDS_ES);
		}
		else {
			is = Datasets.class.getClassLoader().getResourceAsStream(STOP_WORDS_EN);
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		Jedis jedis = pool.getResource();
		try {
			String line;
			jedis.del("StopWords");			
			while ((line = br.readLine()) != null) {
				jedis.hset("StopWords", line, line);				     
				
			}
					
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();	 
		
	    }finally {
			pool.returnResource(jedis);
		} 
	}
	
}
