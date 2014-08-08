package cat.tv3.eng.rec.recomana.lupa.visualization;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;


public class GenerateAllVisualization {
	public static void main(String[] args) throws ParseException, IOException {		
		ClustersToJson csj = new ClustersToJson();
		GenerateHistorgrams gh = new GenerateHistorgrams();
		RecommendationToJson rj = new RecommendationToJson();
		TextsResumeToJson tr = new TextsResumeToJson();
		
		createDir("data_toVisualize");
		
		//csj.main(args);
		
		createDir("data_toVisualize/data_tsv");
		gh.main(args);
		
		createDir("data_toVisualize/data_recommendation");
		rj.main(args);
		
		createDir("data_toVisualize/data_resume");
		tr.main(args);
		
	
	    
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
}
