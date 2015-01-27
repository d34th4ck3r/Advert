package twoGrams;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.rdd.RDD;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Function1;
import scala.Tuple2;


public class Main {
	
	public static void main(String[] args) throws Exception {
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount");
		JavaStreamingContext jssc = new JavaStreamingContext(conf, new Duration(10000));
		JavaReceiverInputDStream<String> lines = jssc.receiverStream(new CustomReceiver(8060));
//		lines.print();
		JavaDStream<String> hash = lines.flatMap(
				  new FlatMapFunction<String, String>() {
					    @Override public Iterable<String> call(String x) {
					  //  	System.out.println(x);
					    	String[] s=x.split("\\}\\{");
					      return Arrays.asList(s);
					      
					  }
			});
		
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
		
		output.print();
//		output.dstream().saveAsTextFiles("TEST", "GAME");
		jssc.start();
	}
	
}
	
	