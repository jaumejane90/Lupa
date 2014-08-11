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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import redis.clients.jedis.Jedis;

public class TextsResumeToJson {
	public static void main(String[] args) throws IOException {			
		String host = args[0];
	    int port = Integer.parseInt(args[1]);
		Jedis jedis = new Jedis(host, port,20000);	
		
		
		String[] text_keys = jedis.keys("hash_id_*").toArray(new String[0]);	
		
		for (int i = 0 ; i < text_keys.length; ++i) {	
			JSONArray resume = new JSONArray();
			String[] split_resume_name =  text_keys[i].split("_");
			String id = split_resume_name[split_resume_name.length-1];
			
			JSONObject text = new JSONObject();
			text.put("id",id);			
			text.put("tittle", jedis.hget(text_keys[i], "tittle"));
			text.put("text", jedis.hget(text_keys[i], "text"));
			
			resume.add(text);
		               	 
			saveResults(resume,id);			
		}	
		jedis.disconnect();		
	}
	
	private static void saveResults(JSONArray recomendations, String id){		
		Writer out;
		try {			
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data_toVisualize/data_resume/resume_"+id+".json"), "UTF-8"));
						
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
