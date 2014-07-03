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

import java.util.Random;


public class LupaKMeans  {
    
    private int clusters = -1;   
    private int maximumOfIterations = -1;   
    private Random rg;  
    private LupaItemDistance distance_function;   
    private LupaClusterItem[] centroids;

      
    public LupaKMeans(int clusters, int iterations, LupaItemDistance d) {
        this.clusters = clusters;
        this.maximumOfIterations = iterations;
        this.distance_function = d;
        rg = new Random(System.currentTimeMillis());
    }  
    

    public LupaClusterItem[] getCentroids() {
		return centroids;
	}

	public void setCentroids(LupaClusterItem[] centroids) {
		this.centroids = centroids;
	}

	
    public LupaClusterSet[] cluster(LupaClusterSet dataset) {
        if (dataset.size() == 0) {
            throw new RuntimeException("VidreDataset empty");   	
        }
        if (clusters <= 1) {
        	clusters = 2;    
        }      
       
        this.centroids = randomCentroids(dataset);
                
        int itCount = 0;
        boolean stillGeneratingNewClusters = true;
        boolean thereAreRandomClustersLeft = true;
        while (thereAreRandomClustersLeft || (itCount < this.maximumOfIterations && stillGeneratingNewClusters)) {
        	itCount++;          
           
            int[] assigs = assignInstasncestoCentroids(dataset,centroids);
            
            int[] sizeCluster = new int[this.clusters];              
            LupaClusterItem[] newMathematicsCentroids = findMatethmaticCentroids(dataset, sizeCluster, assigs);
    
            stillGeneratingNewClusters = false;
            thereAreRandomClustersLeft = false;
            for (int i = 0; i < this.clusters; i++) {
                if (sizeCluster[i] > 0) {                  
                    Integer index = dataset.findProximity(newMathematicsCentroids[i]);                	
                    if (distance_function.distance(dataset.getInstance(index), centroids[i]) > 0.0001) {
                        stillGeneratingNewClusters = true;
                        centroids[i] = dataset.getInstance(index);
                    }                  	
                } else {                    
                    thereAreRandomClustersLeft = true;
                    this.centroids[i] = dataset.getInstance(rg.nextInt(dataset.size())); 
                }
             }
        }
      
        return clustertoDataset(dataset, centroids);
    }
    
    private int[] assignInstasncestoCentroids(LupaClusterSet dataset, LupaClusterItem[] centroids ){    	
    	 int[] assigs = new int[dataset.size()];         
         for (int i = 0; i < dataset.size(); i++) {
             int bestClusterIndex = 0;
             double minDistance = distance_function.distance(centroids[0], dataset.getInstance(i));
             for (int j = 1; j < centroids.length; j++) {
                 double dist = distance_function.distance(centroids[j], dataset.getInstance(i));
                 if (dist < minDistance) {
                     minDistance = dist;
                     bestClusterIndex = j;
                 }
             }
             assigs[i] = bestClusterIndex;
          } 
          return assigs;
    }
    
    private LupaClusterItem[] randomCentroids(LupaClusterSet dataset){
    	  Integer rand_centroid_gen;
          LupaClusterItem[] centroids = new LupaClusterItem[clusters];       
          for (int i = 0; i < clusters; i++) {
          	rand_centroid_gen = rg.nextInt(dataset.size());           	
          	centroids[i] = dataset.getInstance(rand_centroid_gen);        	
          }
    	return centroids;
    }
    
    private LupaClusterItem[] findMatethmaticCentroids(LupaClusterSet dataset, int[] sizeCluster, int[] assigs){
    	 LupaClusterItem[] newMathematicsCentroids = new LupaClusterItem[this.clusters];
         for(int i =0 ; i < this.clusters; i++) {
        	 newMathematicsCentroids[i] = new LupaClusterItem();
         }
         for (int i = 0; i < dataset.size(); i++) {
             LupaClusterItem in = dataset.getInstance(i);
             newMathematicsCentroids[assigs[i]].addTreeMap(in.getText_freq());               
             sizeCluster[assigs[i]]++;
         }
         for(int i = 0 ; i < this.clusters ; i++ ) {
        	 newMathematicsCentroids[i].divide(sizeCluster[i]); 
         }
         return newMathematicsCentroids;
    }
    
    private LupaClusterSet[] clustertoDataset(LupaClusterSet dataset, LupaClusterItem[] final_centroids){
    	
    	 LupaClusterSet[] result = new LupaClusterSet[clusters];
         for (int i = 0; i < clusters; i++)  result[i] = new LupaClusterSet();
         for (int i = 0; i < dataset.size(); i++) {
             int bestCluster = 0;
             double minDistance = distance_function.distance(final_centroids[0], dataset.getInstance(i));
             for (int j = 0; j < clusters; j++) {
                 double dist = distance_function.distance(final_centroids[j], dataset.getInstance(i));
                 if (dist < minDistance) {
                     minDistance = dist;
                     bestCluster = j;
                 }
             }
             result[bestCluster].addInstance(dataset.getInstance(i));

         }
         return result;
    } 

}
