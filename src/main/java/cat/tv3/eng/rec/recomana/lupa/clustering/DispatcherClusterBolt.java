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

package cat.tv3.eng.rec.recomana.lupa.clustering;

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
import cat.tv3.eng.rec.recomana.lupa.engine.LupaItem;

public class DispatcherClusterBolt  extends BaseRichBolt {
	private OutputCollector _collector;
	final String host;
	final int port;
	JedisPool pool;
	
	public DispatcherClusterBolt(String host, int port) {
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
	     pool = new JedisPool(new JedisPoolConfig(),host,port,20000);
		
	}

	@Override
	public void execute(Tuple input) {	
		String id_text = input.getStringByField("id_text");
		LupaItem distr_text = (LupaItem)input.getValueByField("distr_text");
		String cluster_name = input.getStringByField("cluster_name");
		
		Jedis jedis = pool.getResource();
		try {					
			Set<String> set_IDs_toCompare = jedis.smembers(cluster_name); 
			String[] ids_toCompare = set_IDs_toCompare.toArray(new String[0]);			
			for(int i = 0 ; i < ids_toCompare.length; ++i){			
				_collector.emit(new Values(id_text,distr_text,ids_toCompare[i]));
			}				
		} finally {
			pool.returnResource(jedis);
		}   
		
	}
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		  declarer.declare(new Fields("id_text","distr_text","id_compare")); 
		
	}
}
