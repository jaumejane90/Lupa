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

package cat.tv3.eng.rec.recomana.vidre.io;

import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class TextRedisSpout extends BaseRichSpout {
	 
	private static final long serialVersionUID = 1L;
	SpoutOutputCollector _collector;    
	final String host;
	final int port;
	JedisPool pool;
		
	public TextRedisSpout(String host, int port) {
		this.host = host;
		this.port = port;		
	}
		
		
	 @Override
	 public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		 _collector = collector; 
	     JedisPoolConfig poolConfig = new JedisPoolConfig();
	     poolConfig.setMaxActive(1);
	     poolConfig.setMaxIdle(1);
	     pool = new JedisPool(new JedisPoolConfig(),host,port);
	 }
	    
	 @Override
	 public void close() {
		 pool.destroy();
	 }	

    @Override
    public void nextTuple() {     	
    	String id,text;    
    	Jedis jedis = pool.getResource();
		try {
			List<String> pop_text = jedis.blpop(0,"text_queue"); // timeout = 0 -> block indefinitely 
			id = pop_text.get(1); 			
			text = jedis.hget(id,"text");				
		} finally {
			pool.returnResource(jedis);
		}       		
        _collector.emit(new Values(text+" "+id));
    }        

    @Override
    public void ack(Object id) {
    }

    @Override
    public void fail(Object id) {
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {      
    	declarer.declare(new Fields("text+id")); 
	}
}
