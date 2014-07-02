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

package cat.tv3.eng.rec.recomana.vidre.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Datasets {
	private final static String GUTENBERG_RECOMMENDATION_FILE = "gutenbergRecommendationResources.txt";
	private final static String GUTENBERG_CLUSTERING_FILE = "gutenbergClusteringResources.txt";


	private static List<TestTextInstance> TEST_TEXT_SAMPLE;
	
	public static List<TestTextInstance> getRecommendationSamples() {	
		if (TEST_TEXT_SAMPLE == null) {
			try {
				loadRecommendationSample();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return TEST_TEXT_SAMPLE;
	}
	
	public static List<TestTextInstance> getClusteringSamples() {	
		if (TEST_TEXT_SAMPLE == null) {
			try {
				loadClusteringSample();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return TEST_TEXT_SAMPLE;
	}


	protected static void loadRecommendationSample() throws IOException {
		TEST_TEXT_SAMPLE = new ArrayList<TestTextInstance>();
		
		InputStream is = Datasets.class.getClassLoader().getResourceAsStream(GUTENBERG_RECOMMENDATION_FILE);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					String[] values = line.split("&");					
					TEST_TEXT_SAMPLE.add(new TestTextInstance(Integer.parseInt(values[0]),values[1],values[2]));
				} catch (Exception ex) {
					System.err.println("Skipped twitter sample because it can't be parsed : " + line);
				}
			}			
		} finally {
			is.close();
			br.close();
		}
	}
	
	protected static void loadClusteringSample() throws IOException {
		TEST_TEXT_SAMPLE = new ArrayList<TestTextInstance>();
		
		InputStream is = Datasets.class.getClassLoader().getResourceAsStream(GUTENBERG_CLUSTERING_FILE);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					String[] values = line.split("&");						
					TEST_TEXT_SAMPLE.add(new TestTextInstance(Integer.parseInt(values[0]),values[1],values[2]));
				} catch (Exception ex) {
					System.err.println("Skipped twitter sample because it can't be parsed : " + line);
				}
			}
			
		} finally {
			is.close();
			br.close();
		}
	}

}
