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


import java.util.HashSet;
import java.util.Set;


public class KLDdistance {
	private VidreItem VidreItem;
	private VidreItem VidreItem_toCompare;
	private double thresholdFactor = 10.0;	
	
	private Double epsilon = null;
	
	
	public  KLDdistance(){
		
	}
	
	public KLDdistance(VidreItem vocabulary1, VidreItem vocabulary2){
		this.VidreItem= vocabulary1;
		this.VidreItem_toCompare= vocabulary2;
	}
	
	private Set<String> createGlobalVocabulary() {
		Set<String> vocabulary = new HashSet<String>();
		vocabulary.addAll(VidreItem.wordSet());
		vocabulary.addAll(VidreItem_toCompare.wordSet());
		
		return vocabulary;
	}
		
	public Double distance() {
		double distance;
		double beta_voc1 = this.caculateBeta(VidreItem);		
		double beta_voc2 = this.caculateBeta(VidreItem_toCompare); 	
		
		distance = this.distance(beta_voc1,beta_voc2);	

		return distance;

	}
	
	private double distance(double beta_1, double beta_2){
		Double distance = 0.0;

		Set<String> vocabulary = this.createGlobalVocabulary();	
		Double tpc;
		Double tpd;
		for (String word : vocabulary) {
			tpc = this.wordProbabilityInDocument(word, this.VidreItem, beta_1);
			tpd = this.wordProbabilityInDocument(word, this.VidreItem_toCompare, beta_2);
			distance += (tpc - tpd) * Math.log(tpc / tpd);
		}
		return distance;
		
	}	
	
	protected Double wordProbabilityInDocument(String word, VidreItem documentVocabulary, double beta) {
		Double probability = documentVocabulary.frequency(word);		
		if (probability == 0 || probability.equals(Double.NaN)) {
			probability = this.getEpsilon();			
		} else {			
			probability *= beta;
		}

		return probability;
	}
	
	protected double caculateBeta(VidreItem documentVocabulary) {
		Double beta = 1.0;
		Double epsilon = this.getEpsilon();

		Set<String> globalVocabulary = this.createGlobalVocabulary();

		for (String word : globalVocabulary) {
			if (!documentVocabulary.contains(word)) {
				beta -= epsilon;
			}
		}

		return beta;
	}
	
	protected double getEpsilon() {
		if (this.epsilon == null) {
			this.epsilon = this.estimateEpsilon();
		}
		return this.epsilon;
	}

	protected double estimateEpsilon() {
		Double maxSize = 0.0;
		Double cand1 = VidreItem.totalCount();
		Double cand2 = VidreItem_toCompare.totalCount();
		if(cand1>cand2){
			maxSize=cand1;					
		}
		else maxSize=cand2;		
		return 1 / (this.thresholdFactor * maxSize.doubleValue());
	}

	

	
}
