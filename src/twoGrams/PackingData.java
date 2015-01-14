package twoGrams;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class PackingData implements Serializable {
	
	private HashMap<String,ArrayList<String>> Hash;
	
	public PackingData(){
		Hash = new HashMap<String,ArrayList<String>>();
	}
	
	public void addToHash(String key, String value){
		ArrayList<String> val = this.Hash.get(key);
		if(val!=null){
			val.add(value);
		}else{
			val=new ArrayList<String>();
			val.add(value);
		}
	}
	
	public HashMap<String,ArrayList<String>> getHash(){
		return this.Hash;
	}

}
