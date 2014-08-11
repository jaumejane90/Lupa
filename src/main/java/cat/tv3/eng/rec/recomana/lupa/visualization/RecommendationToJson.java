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
package cat.tv3.eng.rec.recomana.lupa.visualization;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

public class RecommendationToJson {
		public static void main(String[] args) throws IOException {		
			final int TOTAL_WORDS = 20;
			String host = args[0];
		    int port = Integer.parseInt(args[1]);
			Jedis jedis = new Jedis(host, port,20000);
				
			String[] recommendation_keys = jedis.keys("recommendations_*").toArray(new String[0]);	
				
			for (int i = 0 ; i < recommendation_keys.length; ++i) {	
				JSONArray recommendations = new JSONArray();
				String[] split_reco_name =  recommendation_keys[i].split("_");
				String id = split_reco_name[split_reco_name.length-1];
				
				Set<Tuple> recos = jedis.zrangeWithScores(recommendation_keys[i], 0, -1);
				Iterator<Tuple> it = recos.iterator(); 
				
			    while(it.hasNext()){
			    	Tuple t = it.next(); 	
			    	
			    	JSONObject new_reco = new JSONObject();
			    	new_reco.put("id",t.getElement());
			    	new_reco.put("distance", (double)Math.round(t.getScore() * 10000)/10000);
			    	
			    	recommendations.add(new_reco);
			    }		              
			  	saveResults(recommendations,id);			
			}	
			jedis.disconnect();
		}	
						
		private static void saveResults(JSONArray recomendations, String id){			
			Writer out;
			try {				
				out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data_toVisualize/data_recommendation/recommendation_"+id+".json"), "UTF-8"));					
				try {
					out.write(recomendations.toJSONString());
					out.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}   
			
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}		
		}		
}
