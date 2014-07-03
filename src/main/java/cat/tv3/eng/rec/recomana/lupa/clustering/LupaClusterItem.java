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


import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


public class LupaClusterItem implements Map<String,Double>, Serializable {
	
	private static final long serialVersionUID = 1L;
	private TreeMap<String, Double> text_freq = new TreeMap<String, Double>();	
	private String ID;
	private Double count;
	
	public LupaClusterItem() {	      
	      this.count = 0.0;
	}
	
	public LupaClusterItem(String id) {
		this.ID = id;
		this.count = 0.0;
	      
	}

	public LupaClusterItem(TreeMap<String,Double> text_freq, String id, Double count) {
       this.text_freq = text_freq;
       this.ID = id;
       this.count = count;
      
    }	
	
	public TreeMap<String, Double> getText_freq() {
		return text_freq;
	}

	
	public void add(String word) {
		Double actualCount = this.text_freq.get(word);
		if (actualCount == null) {
			actualCount = 1.0;
		} else {
			actualCount++;
		}
		this.text_freq.put(word, actualCount);
		this.count++;
	}

	public void addAll(List<String> words) {
		for (String word : words) {
			this.add(word);
		}
	}
	
	public void add(String word,Double number) {
		Double actualCount = this.text_freq.get(word);
		if (actualCount == null) {
			actualCount = number;
		} else {
			actualCount+=number;
		}
		this.text_freq.put(word, actualCount);
		this.count+=number;
	}

	
	public void addTreeMap(TreeMap<String,Double> words) {
		String key;
		Double value;
		for(Map.Entry<String, Double> entry : words.entrySet()){					
			key = entry.getKey();
			value= entry.getValue();			
			this.add(key,value);					
		}			
	}
	
	public void addTreeMapStrings(Map<String,String> words) {
		String key;
		Double value;
		for(Map.Entry<String, String> entry : words.entrySet()){					
			key = entry.getKey();
			value= Double.parseDouble(entry.getValue());			
			this.add(key,value);					
		}			
	}
	
	public void divide(Integer x){
		String key;
		Double value,new_value;
		for(Map.Entry<String, Double> entry : text_freq.entrySet()){					
			key = entry.getKey();
			value= entry.getValue();
			if(x == 0) new_value = value;
			else new_value = value / x;
			if(new_value == 0 ) new_value = 1.0;
			this.put(key,new_value);				
		}	
		Double old_count = this.count;
		this.count = old_count / x;
		//this.recount();
	}
	
	public void setText_freq(TreeMap<String, Double> text_freq) {
		this.text_freq = text_freq;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}
	
	public void recount(){		
		Double value;
		Double count = 0.0;
		for(Map.Entry<String, Double> entry : text_freq.entrySet()){					
			value= entry.getValue();
			count += value;		
		}			
		this.count = count;
		
	}

	public Double getCount() {
		return count;
	}

	public void setCount(Double count) {
		this.count = count;
	}

	@Override
	public void clear() {
		text_freq.clear();
		
	}

	@Override
	public boolean containsKey(Object key) {
		return text_freq.containsKey(key);

	}

	@Override
	public boolean containsValue(Object value) {
		return text_freq.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<String, Double>> entrySet() {
		return text_freq.entrySet();
	}

	@Override
	public Double get(Object key) {
		if (text_freq.containsKey(key))
            return text_freq.get(key);
        else
            return null;
	}

	@Override
	public boolean isEmpty() {
		 return text_freq.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		TreeSet<String> set = new TreeSet<String>();
        set.addAll(text_freq.keySet());
        return set;
	}

	@Override
	public Double put(String key, Double value) {
		 return text_freq.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Double> m) {
		text_freq.putAll(m);
		
	}

	@Override
	public Double remove(Object key) {
		return text_freq.remove(key);
	}

	@Override
	public int size() {
		 return text_freq.size();
	}

	@Override
	public Collection<Double> values() {
		return text_freq.values();
	}
	
	public String toString() {
	        return "{" + text_freq.toString() + ";" + this.ID + "}";
	}
	
}
