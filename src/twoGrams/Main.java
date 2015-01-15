package twoGrams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;


public class Main {
	
	public static void main(String[] args) throws Exception {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("NetworkWordCount");
		JavaStreamingContext jssc = new JavaStreamingContext(conf, new Duration(1000));
		JavaReceiverInputDStream<String> lines = jssc.socketTextStream("localhost", 8060);
		final PackingData pD = new PackingData();
		JavaDStream<String> hash = lines.flatMap(
				  new FlatMapFunction<String, String>() {
					    @Override public Iterable<String> call(String x) {
					    	String[] s=x.split("\\}\\{");
					      return Arrays.asList(s);
					  }
			});
		JavaPairDStream<String, ArrayList<String>> tuple = hash.mapToPair(
				new PairFunction<String,String,ArrayList<String>>(){

					@Override
					public Tuple2<String, ArrayList<String>> call(String arg0)
							throws Exception {
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
						fin.addAll(arg1);
						fin.addAll(arg0);
						return fin;
					}	
				});
		
		output.print();
		jssc.start();
		
		
		
		
	/*	JavaSparkContext sc = new JavaSparkContext(conf);
		JavaRDD<String> lines = sc.textFile("/Users/gautambajaj/Documents/Advertisement/tokyo30m/addr");
		final PositionCounter pC = new PositionCounter();
		
		ReverseHasher rH = new ReverseHasher();
		JavaRDD<HashMap<String,ArrayList<String>>> lineLengths = lines.map(new Function<String, HashMap<String,ArrayList<String>>>() {
			public HashMap<String,ArrayList<String>> call(String s) {
				System.out.println(s);
				return ReverseHasher.getReverseHash(s); }
			
		});
		HashMap<String,ArrayList<String>> finalHash = lineLengths.reduce(new Function2<HashMap<String,ArrayList<String>>, HashMap<String,ArrayList<String>>, HashMap<String,ArrayList<String>>>(){

			@Override
			public HashMap<String,ArrayList<String>> call(HashMap<String,ArrayList<String>> arg0,
					HashMap<String,ArrayList<String>> arg1) throws Exception {
				// TODO Auto-generated method stub
				HashMap<String,ArrayList<String>> merged= new HashMap<String,ArrayList<String>>();
				merged.putAll(arg0);
				merged.putAll(arg1);
				return merged;
			}});
		*/
	//	System.out.println(finalHash);
		
/*		JavaRDD<PositionCounter> lineLengths = lines.map(new Function<String, PositionCounter>() {
			  public PositionCounter call(String s) {
				  pC.addFile(s);
				  return new PositionCounter(); }
			});
		PositionCounter totalLength = lineLengths.reduce(new Function2<PositionCounter, PositionCounter, PositionCounter>(){

			@Override
			public PositionCounter call(PositionCounter arg0,
					PositionCounter arg1) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}});
		
		
//		File folder = new File("/Users/gautambajaj/Documents/Advertisement/tokyo30m/");
		pC.addFile("/Users/gautambajaj/Documents/Advertisement/tokyo30m/2014-10-09T08-45-43.429Z");
		HashMap<String,Integer> count=pC.getCountForAllPair();
		
        ValueComparator bvc =  new ValueComparator(count);
        TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
        sorted_map.putAll(count);
        for(Map.Entry<String, Integer> entry : sorted_map.entrySet()){
			String journey=entry.getKey();
			Integer val= entry.getValue();
			System.out.println(journey+ " : "+val);
		}
        System.out.println(totalLength);
	}
	*/
	}
	
}
	
	class ValueComparator implements Comparator<String> {

	    Map<String, Integer> base;
	    public ValueComparator(Map<String, Integer> base) {
	        this.base = base;
	    }

	    // Note: this comparator imposes orderings that are inconsistent with equals.    
	    public int compare(String a, String b) {
	        if (base.get(a) <= base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}
