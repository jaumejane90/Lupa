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

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class GenerateAllVisualization {
	public static void main(String[] args) throws ParseException, IOException {		
		ClustersToJson clusters = new ClustersToJson();
		GenerateHistograms histograms = new GenerateHistograms();
		RecommendationToJson recommendations = new RecommendationToJson();
		TextsResumeToJson texts_resume = new TextsResumeToJson();
		
		createDir("data_toVisualize");
		
		clusters.main(args);
		
		createDir("data_toVisualize/data_tsv");
		histograms.main(args);
		
		createDir("data_toVisualize/data_recommendation");
		recommendations.main(args);
		
		createDir("data_toVisualize/data_resume");
		texts_resume.main(args);	    
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
		     }        
		     if(result) {    
		       System.out.println("DIR created");  
		     }
	  	}
	}
}
