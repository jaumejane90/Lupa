package cat.tv3.eng.rec.recomana.lupa.visualization;

import java.io.BufferedWriter;
import java.io.File;
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

public class TextsResumeToJson {
	public static void main(String[] args) throws IOException {			
		String host = args[0];
	    int port = Integer.parseInt(args[1]);
		Jedis jedis = new Jedis(host, port);	
		
		
		String[] text_keys = jedis.keys("hash_id_*").toArray(new String[0]);	
		
		for (int i = 0 ; i < text_keys.length; ++i) {	
			JSONArray resume = new JSONArray();
			String[] split_resume_name =  text_keys[i].split("_");
			String id = split_resume_name[split_resume_name.length-1];
			//System.out.println(id);
			
			 JSONObject text = new JSONObject();
			 text.put("id",id);			
			 text.put("tittle", jedis.hget(text_keys[i], "tittle"));
			 text.put("text", jedis.hget(text_keys[i], "text"));
			 		
			 resume.add(text);
		               
		     //save recommendation
			saveResults(resume,id);			
		}		
		
	}
	
	
	
	private static void saveResults(JSONArray recomendations, String id){
		
		Writer out;
		try {
			
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data_toVisualize/data_resume/resume_"+id+".json"), "UTF-8"));
						
			try {
				out.write(recomendations.toJSONString());
				out.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
}
