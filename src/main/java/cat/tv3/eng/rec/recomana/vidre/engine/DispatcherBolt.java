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

package cat.tv3.eng.rec.recomana.vidre.engine;

import java.util.Map;
import java.util.Set;

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


public class DispatcherBolt  extends BaseRichBolt {
	
	private OutputCollector _collector;
	final String host;
	final int port;
	JedisPool pool;
	
	public DispatcherBolt(String host, int port) {
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
		
	}

	@Override
	public void execute(Tuple input) {		
		String id_text = input.getStringByField("id_text");
		VidreItem distr_text = (VidreItem)input.getValueByField("distr_text");
		
		Jedis jedis = pool.getResource();
		try {					
			Set<String> set_IDs_toCompare = jedis.smembers("IDs_text_toCompare"); 
			String[] ids_toCompare = set_IDs_toCompare.toArray(new String[0]);			
			for(int i = 0 ; i < ids_toCompare.length; ++i){			
				_collector.emit(new Values(id_text,distr_text,ids_toCompare[i]));
			}
			jedis.sadd("IDs_text_toCompare", id_text);				
		} finally {
			pool.returnResource(jedis);
		}  	
	}
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		  declarer.declare(new Fields("id_text","distr_text","id_compare"));		
	}
}
