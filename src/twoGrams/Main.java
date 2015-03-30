package twoGrams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import scala.Tuple2;


public class Main {
	
	public static void main(String[] args) throws Exception {
		
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount");
		JavaStreamingContext jssc = new JavaStreamingContext(conf, new Duration(10000));
		JavaReceiverInputDStream<String> lines = jssc.receiverStream(new CustomReceiver(8060));
	
//		final Neo4jOperations nO = new Neo4jOperations("/Users/gautambajaj/Documents/Advertisement/neo4j/data" );
		
		JavaDStream<String> hash = lines.flatMap(
				  new FlatMapFunction<String, String>() {
					    @Override public Iterable<String> call(String x) {
					  //  	System.out.println(x);
					    	String[] s=x.split("\\}\\{");
					      return Arrays.asList(s);
					  }
			});
		JavaDStream<Long> count = hash.count();
		count.print();
/*		
		JavaPairDStream<String, ArrayList<String>> tuple = hash.mapToPair(
				new PairFunction<String,String,ArrayList<String>>(){

					@Override
					public Tuple2<String, ArrayList<String>> call(String arg0)
							throws Exception {
						arg0=StringUtils.trim(arg0);
						arg0=StringUtils.removeStart(arg0, "{");
				    	arg0=StringUtils.removeEnd(arg0, "}");
				    	String[] tuple = arg0.split(":");
				    	tuple[0]=tuple[0].replace("\"","");
				    	tuple[1]=tuple[1].replace("\"","");
				    	ArrayList<String> zipcodes=new ArrayList<String>();
				    	zipcodes.add(tuple[1]);
						// TODO Auto-generated method stub
						return new Tuple2<String,ArrayList<String>>(tuple[0],zipcodes);
					}
				});
		JavaPairDStream<String, ArrayList<String>> output = tuple.reduceByKey(
				
				new Function2<ArrayList<String>,ArrayList<String>,ArrayList<String>>(){

					@Override
					public ArrayList<String> call(ArrayList<String> arg0,
							ArrayList<String> arg1)  {
						// TODO Auto-generated method stub
						ArrayList<String> fin = new ArrayList<String>();
						fin.addAll(arg0);
						if(!arg0.get(arg0.size()-1).equals(arg1.get(0))){
							fin.addAll(arg1);
						}
						return fin;
					}	
				});
		
		output.foreachRDD(
				new Function2<JavaPairRDD<String,ArrayList<String>>,Time,Void>(){
					@Override
					public Void call(
							JavaPairRDD<String, ArrayList<String>> arg0,
							Time arg1) throws Exception {
						// TODO Auto-generated method stub
						arg0.foreachPartition(
								new VoidFunction<Iterator<Tuple2<String,ArrayList<String>>>>(){

									@Override
									public void call(
											Iterator<Tuple2<String, ArrayList<String>>> arg0)
											throws Exception {
										
										// TODO Auto-generated method stub
										GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder("/dev/shm/Advertisement/data/")
									            .setConfig("remote_shell_enabled", "true")
									            .newGraphDatabase();	
									            
										try (Transaction tx = graphDb.beginTx()) {
											while (arg0.hasNext()) {
												Tuple2 < String, ArrayList < String >> tuple = arg0.next();
												Node HMac=Neo4jOperations.getHMacFromValue(graphDb, tuple._1);
												boolean oldHMac=false;
												if (HMac!= null){
													System.out.println("Alread in Database:" + tuple._1);
													oldHMac=true;
												}
												else
													HMac=Neo4jOperations.createHMac(graphDb, tuple._1);
												
												ArrayList<String> zipcodes=tuple._2;
												for(String zipcode : zipcodes){
													Node Zipcode=Neo4jOperations.getZipcodeFromValue(graphDb, zipcode);
													if(Zipcode!=null){
														System.out.println("Already in Database:" + zipcode);
														if(oldHMac==true && Neo4jOperations.getRelationshipBetween(HMac, Zipcode)!=null)
															Neo4jOperations.updateToCurrentTime(HMac, Zipcode);
														else
															Neo4jOperations.travelTo(HMac, Zipcode);
													}
													else{
														Zipcode=Neo4jOperations.createZipcode(graphDb, zipcode);
														Neo4jOperations.travelTo(HMac, Zipcode);
													}
												}
											}
											tx.success();
										}
										graphDb.shutdown();
									}				
						});
						return null;
					}
				});
				*/
		jssc.start();
	}	
}