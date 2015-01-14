package twoGrams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.json.JSONObject;

public class JsonHandler {
	
	private JSONObject jsonObject;
	
	public JsonHandler(File file){
		
		Scanner scanner=null;
		try {
			scanner=new Scanner(file, "UTF-8" ).useDelimiter("\\A");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String data=scanner.next();
		this.jsonObject=new JSONObject(data);
		
	}
	
	public ArrayList<String> getMacAddressForPlace(String place){
		ArrayList<String> macAddresses = new ArrayList<String>();
		JSONObject value =   (JSONObject) this.jsonObject.get(place);
		if(value==null){
			System.err.println("Place: " + place + " not found");
			return macAddresses;
		}
		macAddresses.addAll(value.keySet());
		return macAddresses;
	}
	
	public ArrayList<String> getPlaces(){
		ArrayList<String> places=new ArrayList<String>();
		places.addAll(this.jsonObject.keySet());
		return places;
		 
	}
	
	public HashMap<String,String> parseInput(String data){
		
		return null;
	}
}

