package twoGrams;

import java.util.Calendar;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;

public class Neo4jOperations  {
	
	
	
	public static Node getHMacFromValue(GraphDatabaseService graphDB,String value){
		try(ResourceIterator<Node> HMacs=graphDB.findNodesByLabelAndProperty(DynamicLabel.label("HMac"), "value", value).iterator()){
			if(HMacs.hasNext())
				return HMacs.next();
			return null;
		}
	}
	
	public static Node getZipcodeFromValue(GraphDatabaseService graphDB,String value){
		try(ResourceIterator<Node> Zipcodes=graphDB.findNodesByLabelAndProperty(DynamicLabel.label("Zipcode"), "value", value).iterator()){
			if(Zipcodes.hasNext())
				return Zipcodes.next();
			return null;
		}
	}
	
	public static Node createHMac(GraphDatabaseService graphDB,String value){
		Node HMac=graphDB.createNode(DynamicLabel.label("HMac"));
		HMac.setProperty("value", value);
		HMac.setProperty("time", Calendar.getInstance().getTimeInMillis());
		return HMac;
	}
	
	public static Node createZipcode(GraphDatabaseService graphDB,String value){
		Node Zipcode=graphDB.createNode(DynamicLabel.label("Zipcode"));
		Zipcode.setProperty("value", value);
		Zipcode.setProperty("time", (long)Calendar.getInstance().getTimeInMillis());
		return Zipcode;
	}
	
	public static Relationship getRelationshipBetween(Node n1, Node n2) { // RelationshipType type, Direction direction
	    for (Relationship rel : n1.getRelationships()) { // n1.getRelationships(type,direction)
	       if (rel.getOtherNode(n1).equals(n2)) return rel;
	    }
	    return null;
	}
	
	public static Relationship travelTo(Node HMac,Node Zipcode){
		Relationship travelledTo=HMac.createRelationshipTo(Zipcode, RelTypes.TravelledTo);
		travelledTo.setProperty("time", Calendar.getInstance().getTimeInMillis());
		return travelledTo;
		
	}
	
	public static Relationship updateToCurrentTime(Node HMac,Node Zipcode){
		Relationship travelledTo = getRelationshipBetween(HMac, Zipcode);
		travelledTo.setProperty("time", Calendar.getInstance().getTimeInMillis());
		return travelledTo;
	}
	
	private static enum RelTypes implements RelationshipType{
		TravelledTo
	}
	
}
