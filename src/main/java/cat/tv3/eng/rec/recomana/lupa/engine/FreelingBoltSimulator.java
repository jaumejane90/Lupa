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

package cat.tv3.eng.rec.recomana.lupa.engine;

import java.util.ArrayList;
import java.util.Map;

import cat.calidos.storm.freeling.FlAnalyzedSentence.FlAnalyzedSentence;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class FreelingBoltSimulator extends BaseRichBolt{
	private OutputCollector _collector;
	
	public FreelingBoltSimulator() {	
		
	}	

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		_collector = collector; 		
	}

	@Override
	public void execute(Tuple input) {
		//String id_noticia = input.getStringByField("id_noticia");
		String text = input.getStringByField("text+id");
		FlAnalyzedSentence freeling_noticia = (FlAnalyzedSentence) new FlAnalyzedSentence(); 
		
		ArrayList<ArrayList<String>> morfologicTokens = new ArrayList<ArrayList<String>>();	
		String[] tokens = text.trim().replaceAll("\\p{Punct}+", "").toLowerCase().split("\\s+");
		//String[] tokens = text.split(" ");
		ArrayList<String> line_MorfologicTokens = new ArrayList<String>();
		ArrayList<String> originalTokens = new ArrayList<String>();
		for(int j=0; j<tokens.length; ++j){		
			line_MorfologicTokens = new ArrayList<String>();
			line_MorfologicTokens.add(tokens[j]);
			originalTokens.add(tokens[j]);
			morfologicTokens.add(line_MorfologicTokens);
		}
		freeling_noticia.setOriginalTokens(originalTokens);		
		freeling_noticia.setMorfologicTokens(morfologicTokens);	
		
		_collector.emit(new Values(freeling_noticia));	
	}
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		  declarer.declare(new Fields("FlAnalyzedSentence")); 		
	}	
}
