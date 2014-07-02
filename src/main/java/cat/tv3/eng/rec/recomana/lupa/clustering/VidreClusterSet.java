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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VidreClusterSet {
	   private List<VidreClusterItem> dataset = new ArrayList<VidreClusterItem>();
	 
	   public VidreClusterSet() {	
		   
	   }
	   
	   public VidreClusterSet(Collection<VidreClusterItem> coll) {
	       dataset.addAll(coll);
	   }    
	    
	   public int size(){
	    	return dataset.size();	    	
	   }
	    
	   public VidreClusterItem getInstance(int index) {
	       return dataset.get(index);
	   }
	    
	   public Integer findProximity(VidreClusterItem centroid){
	    	VidreItemDistance dm = new VidreItemDistance();
	    	int tmpCentroid = 0;
	    	double minDistance = dm.distance(centroid, this.getInstance(0));
	    	for(int i = 1 ; i < dataset.size() ; ++i) {
	    		double dist = dm.distance(centroid, this.getInstance(i));
	    		if (dist < minDistance) {
	                minDistance = dist;
	                tmpCentroid = i;
	            }
	    	}
	    	
	    	return tmpCentroid;     	
	    }
	    
	    public void addInstance(VidreClusterItem instance){
	    	dataset.add(instance);
	    }	    
	  
	    
	    public boolean containsIdVidreInstance(String id){
	    	for(int i = 0 ; i < dataset.size(); ++i) {
	    		if(dataset.get(i).getID().equals(id)) return true;
	    	}
	    	return false;
	    }
	    
	    public String[] listOfIdInstances(){
	    	String[] list = new String[dataset.size()];
	    	for(int i = 0 ; i < dataset.size(); ++i) {
	    		list[i] = dataset.get(i).getID();
	    	}
	    	return list;
	    	
	    }
	   
}
