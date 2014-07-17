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

package cat.tv3.eng.rec.recomana.lupa.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class Datasets {
	private final static String GUTENBERG_RECOMMENDATION_FILE = "gutenbergRecommendationResources.txt";
	private final static String GUTENBERG_CLUSTERING_FILE = "gutenbergClusteringResources.txt";
	private final static String GUTENBERG_FULL_BOOKS_FILE = "gutenberg6FullBooksResources.txt";


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
	
	public static List<TestTextInstance> getFullBooksSamples() {	
		if (TEST_TEXT_SAMPLE == null) {
			try {
				loadFullBooksSample();
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
					System.err.println("Skipped sample because it can't be parsed : " + line);
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
					System.err.println("Skipped sample because it can't be parsed : " + line);
				}
			}
			
		} finally {
			is.close();
			br.close();
		}
	}
	
	protected static void loadFullBooksSample() throws IOException {
		
		TEST_TEXT_SAMPLE = new ArrayList<TestTextInstance>();
		
		InputStream is = Datasets.class.getClassLoader().getResourceAsStream(GUTENBERG_FULL_BOOKS_FILE);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
						
		try {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					
					String[] values = line.split("&");
					//System.out.println("Length -> " + values[2].length());
					List<String> parts = new ArrayList<String>();
					int size = 10000;
					int length = values[2].length();
					for(int i = 0 ; i < length; i+=size) {
						 //System.out.println("i  " + i);
						 int nextdot = 0 ;
						 boolean nextdotfound = false;
						 for(int j = (i+size) ; j < length && !nextdotfound; j++) {
							 nextdot ++;
							 if(values[2].charAt(j)=='.') {
								// System.out.println("FOUND -> " + line.charAt(j));
								 nextdotfound=true;
							 }							 
						 }
						
						 parts.add(values[2].substring(i, Math.min(length, i + size + nextdot)));
						 i+=nextdot;
					}					
				
					// Show output
					 
					//System.out.println("Number of parts -> " + parts.size());
					 /*
					for(int i = 0 ; i< parts.size(); i++) {
						System.out.println(parts.get(i));
					}
					System.out.println("Number of parts -> " + parts.size());
					*/
					Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("parts_"+values[1] +".txt"), "UTF-8"));
					try {						
						out.write(parts.size()+"\n");
						for(int i = 0 ; i< parts.size(); i++) {
							Integer id = 100 * Integer.parseInt(values[0]) + i;
							out.write(id.toString()+"\n");
							out.write(parts.get(i)+"\n");						
						}
					  
					} finally {
					    out.close();
					}  
					
					for(int i = 0 ; i< parts.size(); ++i) {
						Integer id = 100 * Integer.parseInt(values[0]) + i;
						//System.out.println("ID -> " + id);
						TEST_TEXT_SAMPLE.add(new TestTextInstance(id,values[1]+" part "+id,parts.get(i)));
					}
					
					  
					 
					
					
					
					} catch (Exception ex) {
					System.err.println("Skipped sample because it can't be parsed : " + line);
				}
			}
			
		} finally {
			is.close();
			br.close();
		}
	}


}
