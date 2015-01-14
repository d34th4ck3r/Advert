package twoGrams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PositionCounter {
	
	private ReverseHasher reverseHasher;
	
	public PositionCounter(){
		this.reverseHasher=new ReverseHasher();
	}
	
	public void addFile(String filepath){
		this.reverseHasher.hashFile(filepath);
	}
	
	public ArrayList<String> getMacIdInPlaces(String Place1,String Place2){
		ArrayList<String> macIds= new ArrayList<String>();
		HashMap<String,ArrayList<String>> hashMap=this.reverseHasher.getCompleteHash();
		for(Map.Entry<String, ArrayList<String>> entry : hashMap.entrySet()){
			String macId=entry.getKey();
			ArrayList<String> Places = entry.getValue();
			if(Places.contains(Place1) && Places.contains(Place2)){
				macIds.add(macId);
			}
		}
		return macIds;
	}
	
	public HashMap<String,Integer> getCountForAllPair(){
		HashMap<String,Integer> allPair=new HashMap<String,Integer>();
		HashMap<String,ArrayList<String>> hashMap=this.reverseHasher.getCompleteHash();
		for(Map.Entry<String, ArrayList<String>> entry : hashMap.entrySet()){
			String macId=entry.getKey();
			ArrayList<String> Places = entry.getValue();
			String[] placesArr=new String[Places.size()];
			placesArr=Places.toArray(placesArr);
			for(int i=0;i<placesArr.length;i++){
				for(int j=i+1;j<i+11 && j<placesArr.length;j++){
					if(placesArr[i].equalsIgnoreCase(placesArr[j]))
						continue;
					String key=placesArr[i]+"->"+placesArr[j];
//					if(key.equalsIgnoreCase("SHIBUYA->SHINJUKU"))
//					System.err.println(key);
					Integer val=allPair.get(key);
					if(val!=null){
						allPair.put(key, val+1);
					}else{
						allPair.put(key, 1);
					}
				}
			}
		}
		return allPair;
	}	
}
