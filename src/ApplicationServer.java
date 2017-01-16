
import java.net.*;
import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;

/*
 * Author: Andrea Johnson
 * Date: 1/15/2017
 * 
 * 
 * This is the main server class, it clears and creates a new numbers.log file every time it runs
 */
public class ApplicationServer {
	 //use queue in thread to track what needs to go into the log file
	//use global static atomic int to track unique,duplicates between 10 second reporting track total unique
	static AtomicInteger clientCount = new AtomicInteger(1);
	static final AtomicInteger totalUnique = new AtomicInteger(0);
	static final AtomicInteger intervalDuplicates = new AtomicInteger(0);
	static final AtomicInteger intervalUnique = new AtomicInteger(0);
	static AtomicInteger terminateFlag = new AtomicInteger(0);
	static final ConcurrentHashMap<String, String> numbers =  new ConcurrentHashMap<>();
	static AtomicInteger writingFlag = new AtomicInteger(0);
		
    public static void main(String[] args) throws IOException {
    	//clears the log file if it exists
    	 ClearFile();
    	 
    	 //Initialize variables
    	BlockingQueue<String> queue = new ArrayBlockingQueue<String>(2056);
        int portNumber = 4000;
        boolean listening = true;
        int localClientCount = 1;
        int localTerminateFlag = 0;
        Timer outputTimer = new Timer(true);
        outputTimer.schedule(new ApplicationOutputTimer(queue), 0, 10000);
        
        try (ServerSocket serverSocket = new ServerSocket(portNumber, 0)) { 
        	        	
        	//System.out.println("listening");
            while (listening) {
            	if (localClientCount < 5 && localTerminateFlag != 1)
            	{
            		try{
            		serverSocket.setSoTimeout(10000);
            		new DigitThreads(serverSocket.accept(), queue).start();
            		//System.out.println("client allowed");
            		}catch(IOException se){
                    	//continue while
            			localClientCount = clientCount.get();
                        localTerminateFlag = terminateFlag.get();  
            			continue;
                    }
            	}
                localClientCount = clientCount.get();
                localTerminateFlag = terminateFlag.get();  
                
                //ensure it shuts down after terminate flag is set, also ensures file is done writing
                if (localTerminateFlag == 1 && ApplicationServer.writingFlag.get() == 0){
                	System.exit(0);
                }
                
            }
            
        }  catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
                System.exit(-1);
            }
    }
    
    private static void ClearFile(){
    	PrintWriter writer = null;
		try {
			FileWriter fw = new FileWriter("numbers.log",false);
		    BufferedWriter bw = new BufferedWriter(fw);
		    writer = new PrintWriter(bw);
		    
		    writer.print("");
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		} finally {
			writer.close();
		}
    	
    	
    }
}