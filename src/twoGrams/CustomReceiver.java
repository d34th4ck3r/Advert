package twoGrams;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;

public class CustomReceiver extends Receiver<String> {

	  int port = -1;
	  

	  public CustomReceiver(int port_) {
	    super(StorageLevel.MEMORY_AND_DISK_2());
	    
	    port = port_;
	  }

	  public void onStart() {
	    // Start the thread that receives data over a connection
		  
	    new Thread()  {
	      @Override public void run() {
	        receive();
	      }
	    }.start();
	  }

	  public void onStop() {
	    // There is nothing much to do as the thread calling receive()
	    // is designed to stop by itself isStopped() returns false
	  }

	  /** Create a socket connection and receive data until receiver is stopped */
	  private void receive() {
	    DatagramSocket socket = null;
	    String userInput = null;

	    try {
	      // connect to the server
	     
	      socket = new DatagramSocket(port);
	      byte[] receiveData = new byte[2048];
	      while(!isStopped()){
	      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	      socket.receive(receivePacket);
	      userInput = new String( receivePacket.getData());

	      // Until stopped or connection broken continue reading
	      
	    	  store(userInput);
	      }
	      socket.close();
	      
	      // Restart in an attempt to connect again when server is active again
	      restart("Trying to connect again");
	    } catch(ConnectException ce) {
	      // restart if could not connect to server
	      restart("Could not connect", ce);
	    } catch(Throwable t) {
	      // restart if there is any other error
	      restart("Error receiving data", t);
	    }
	 }
}