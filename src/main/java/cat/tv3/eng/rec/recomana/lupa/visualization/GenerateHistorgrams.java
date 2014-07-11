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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;



import redis.clients.jedis.Jedis;

public class GenerateHistorgrams {
	public static void main(String[] args) throws IOException {		
		final int TOTAL_WORDS = 20;
		String host = args[0];
	    int port = Integer.parseInt(args[1]);
		Jedis jedis = new Jedis(host, port);
		
		createDir("data_tsv");
		
		String[] distr_prob_keys = jedis.keys("distr_text-id-*").toArray(new String[0]);	
		
		for (int i = 0 ; i < distr_prob_keys.length; ++i) {	
			String[] split_distr_name =  distr_prob_keys[i].split("-");
			String id = split_distr_name[split_distr_name.length-1];
			//System.out.println(id);
			Map<String,String> word_freq = jedis.hgetAll(distr_prob_keys[i]);
			TreeMap<String,Double> top_words = new TreeMap<String,Double>();			
			Double total = 0.0;
			for(Map.Entry<String, String> entry : word_freq.entrySet()){
				String key = entry.getKey();
			    Double value = Double.parseDouble(entry.getValue());
			  
			    total+= value;
			    //System.out.printf("Key : %s and Value: %s %n", entry.getKey(), entry.getValue());
			   // System.out.println("key -> " + key + " value -> " + value);
			    if(top_words.size()<TOTAL_WORDS) {
			    	top_words.put(key,value);		    	
			    }
			    else {			    	
			    	String result[] = minimumValue(top_words);
			    	//System.out.println("MINIMUM -> " + result[0] + "key_min -> " + result[1]);
			    	if(value > Double.parseDouble(result[0])) {
			    		top_words.remove(result[1]);
			    		top_words.put(key, value);
			    	}
			    }
			    
			    
			}
			
			//sort map
			Map<String,Double> sorted_top_words = sortByValue(top_words);
			
			//save map
			saveResults(sorted_top_words,Integer.parseInt(id),total);
			
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
	
	private static String[] minimumValue(Map<String,Double> map) {
		String key_minimum = "Error";
		Double minimum = 0.0;
		int i = 0;
		for(Map.Entry<String, Double> entry : map.entrySet()){
			String key = entry.getKey();
		    Double value = entry.getValue();
		    if(i==0) {
		    	  key_minimum = key;
		    	  minimum = value;
		    	  ++i;
		    }
		    else {
		    	if(value < minimum) {
		    		 key_minimum = key;
			    	 minimum = value;
		    	}
		    }	    
		}
		
		return new String[]{minimum.toString(),key_minimum};
	}
	
	private static void saveResults(Map<String,Double> map, Integer id,Double total){
		Writer out;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data_tsv/data_"+id.toString()+".tsv"), "UTF-8"));
						
			try {
				//System.out.println("total -> " + total);
				out.write("word" + "\t" +  "frequency" + "\n");
				for(Map.Entry<String, Double> entry : map.entrySet()){
					//System.out.println(entry.getKey() + " -> " + entry.getValue());
					out.write(entry.getKey() + "\t" +  (double)Math.round(entry.getValue()/total * 10000)/10000 + "\n");
				}
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
	
	public static Map<String, Double> sortByValue(Map<String, Double> map) {
        List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {

            public int compare(Map.Entry<String, Double> m1, Map.Entry<String, Double> m2) {
                return (m2.getValue()).compareTo(m1.getValue());
            }
        });

        Map<String, Double> result = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
	
	 
}
