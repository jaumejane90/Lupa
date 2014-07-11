package cat.tv3.eng.rec.recomana.lupa.visualization;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

public class recommendationToJson {
		public static void main(String[] args) throws IOException {		
			final int TOTAL_WORDS = 20;
			String host = args[0];
		    int port = Integer.parseInt(args[1]);
			Jedis jedis = new Jedis(host, port);
			
			createDir("data_recommendation");
			
			String[] recommendation_keys = jedis.keys("recommendations_*").toArray(new String[0]);	
				
			for (int i = 0 ; i < recommendation_keys.length; ++i) {	
				JSONArray recommendations = new JSONArray();
				String[] split_reco_name =  recommendation_keys[i].split("_");
				String id = split_reco_name[split_reco_name.length-1];
				//System.out.println(id);
				
				Set<Tuple> recos = jedis.zrangeWithScores(recommendation_keys[i], 0, -1);
				Iterator<Tuple> it = recos.iterator(); 
				
			    while(it.hasNext()){
			    	Tuple t = it.next(); 	
			    	
			    	JSONObject new_reco = new JSONObject();
			    	new_reco.put("id",t.getElement());
			    	new_reco.put("distance", t.getScore());
			    	
			    	recommendations.add(new_reco);
			    }		              
			     //save recommendation
				saveResults(recommendations,id);			
			}		
		}	
		
		private static void createDir(String dir){
			File theDir = new File(dir);
			if (!theDir.exists()) {
			    System.out.println("creating directory: " + dir);
			    boolean result = false;

			    try{
			        theDir.mkdir();
			        result = true;
			     } catch(SecurityException se){
			        //handle it
			     }        
			     if(result) {    
			       System.out.println("DIR created");  
			     }
			  }
		}
		
		
		private static void saveResults(JSONArray recomendations, String id){
			
			Writer out;
			try {
				
				out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data_recommendation/recommendation_"+id+".json"), "UTF-8"));
							
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
