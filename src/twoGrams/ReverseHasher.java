package twoGrams;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ReverseHasher {
	
	private HashMap<String,ArrayList<String>> reverseHash;
	private Set<String> places;
	
	public ReverseHasher(){
		this.reverseHash=new HashMap<String,ArrayList<String>>();
		this.places=new HashSet<String>();
	}
	
	public boolean contains(String macId){
		return this.reverseHash.containsKey(macId);
	}
	
	public Set<String> getAllPlaces(){
		return this.places;
	}
	
	public ArrayList<String> getPlacesfor(String macId){
		return this.reverseHash.get(macId);
	}
	
	public HashMap<String,ArrayList<String>> getCompleteHash(){
		return this.reverseHash;
	}
	
	public void hashFile(String path){
		File file = new File(path);
		JsonHandler jHandler = new JsonHandler(file);
		for(String place:jHandler.getPlaces()){
			this.places.add(place);
			ArrayList<String> macAddresses=jHandler.getMacAddressForPlace(place);
			for(String macId:macAddresses){
				if(this.reverseHash.containsKey(macId)){
					this.reverseHash.get(macId).add(place);
				}else{
					ArrayList<String> newList = new ArrayList<String>();
					newList.add(place);
					this.reverseHash.put(macId,newList);
				}
			}
		}
	}
	
	public static HashMap<String,ArrayList<String>> getReverseHash(String path){
		File file = new File(path);
		HashMap<String,ArrayList<String>> tempReverseHash = new HashMap<String,ArrayList<String>>();
		JsonHandler jHandler = new JsonHandler(file);
		for(String place:jHandler.getPlaces()){
			
			ArrayList<String> macAddresses=jHandler.getMacAddressForPlace(place);
			for(String macId:macAddresses){
				if(tempReverseHash.containsKey(macId)){
					tempReverseHash.get(macId).add(place);
				}else{
					ArrayList<String> newList = new ArrayList<String>();
					newList.add(place);
					tempReverseHash.put(macId,newList);
				}
			}
		}
		return tempReverseHash;
		
	}

}
