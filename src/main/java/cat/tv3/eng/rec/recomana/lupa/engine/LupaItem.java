/**
Copyright 2013-2015 Pierre Merienne
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


package cat.tv3.eng.rec.recomana.lupa.engine;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class LupaItem implements Iterable<String>, Serializable {
	
	private TreeMap<String, Double> wordCounts = new TreeMap<String, Double>();	
	private Double size = 0.0;

	public LupaItem() {
	}

	public LupaItem(List<String> words) {
		this.addAll(words);
	}

	public TreeMap<String, Double> getWordCounts() {
		return wordCounts;
	}

	public void setWordCounts(TreeMap<String, Double> wordCounts) {
		this.wordCounts = wordCounts;
	}

	public Double getSize() {
		return size;
	}

	public void setSize(Double size) {
		this.size = size;
	}

	public void add(String word) {
		Double actualCount = this.wordCounts.get(word);
		if (actualCount == null) {
			actualCount = 1.0;
		} else {
			actualCount++;
		}
		this.wordCounts.put(word, actualCount);
		this.size++;
	}

	public void addAll(List<String> words) {
		for (String word : words) {
			this.add(word);
		}
	}	

	public Double count(String word) {
		Double actualCount = this.wordCounts.get(word);
		if (actualCount == null) {
			actualCount = 0.0;
		}
		return actualCount;
	}
	
	public void remove(String key){
		this.wordCounts.remove(key);
	}

	public Double frequency(String word) {
		return this.count(word).doubleValue() / this.size.doubleValue();
	}

	public Boolean contains(String word) {
		return this.wordCounts.containsKey(word);
	}

	public Integer wordCount() {
		return this.wordCounts.size();
	}

	public Double totalCount() {
		return this.size;
	}

	@Override
	public Iterator<String> iterator() {
		return this.wordCounts.keySet().iterator();
	}

	public Set<String> wordSet() {
		return this.wordCounts.keySet();
	}

	@Override
	public String toString() {
		return "Vocabulary [size=" + size + ", wordCounts=" + wordCounts + "]";
	}
}
