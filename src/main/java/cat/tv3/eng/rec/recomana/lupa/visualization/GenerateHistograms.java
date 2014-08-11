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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import redis.clients.jedis.Jedis;

public class GenerateHistograms {
	public static void main(String[] args) throws IOException {		
		final int TOTAL_WORDS = 20;
		String host = args[0];
	    int port = Integer.parseInt(args[1]);
		Jedis jedis = new Jedis(host, port,20000);
		
		String[] distr_prob_keys = jedis.keys("distr_text-id-*").toArray(new String[0]);	
		
		for (int i = 0 ; i < distr_prob_keys.length; ++i) {	
			String[] split_distr_name =  distr_prob_keys[i].split("-");
			String id = split_distr_name[split_distr_name.length-1];
			Map<String,String> word_freq = jedis.hgetAll(distr_prob_keys[i]);
			TreeMap<String,Double> top_words = new TreeMap<String,Double>();			
			Double total = 0.0;
			for(Map.Entry<String, String> entry : word_freq.entrySet()){
				String key = entry.getKey();
			    Double value = Double.parseDouble(entry.getValue());			  
			    total+= value;
			    if(top_words.size()<TOTAL_WORDS) {
			    	top_words.put(key,value);		    	
			    }
			    else {			    	
			    	String result[] = minimumValue(top_words);
			    	if(value > Double.parseDouble(result[0])) {
			    		top_words.remove(result[1]);
			    		top_words.put(key, value);
			    	}
			    }			    			    
			}
			
			Map<String,Double> sorted_top_words = sortByValue(top_words);			
			
			saveResults(sorted_top_words,Integer.parseInt(id),total);			
		}
		jedis.disconnect();
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
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data_toVisualize/data_tsv/data_"+id.toString()+".tsv"), "UTF-8"));
						
			try {				
				out.write("word" + "\t" +  "frequency" + "\n");
				for(Map.Entry<String, Double> entry : map.entrySet()){					
					out.write(entry.getKey() + "\t" +  (double)Math.round(entry.getValue()/total * 10000)/10000 + "\n");
				}
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
