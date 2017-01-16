
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Pattern;
import java.io.*;

/*
 * Author: Andrea Johnson
 * Date: 1/15/2017
 * 
 * 
 * This is the protocol class to define how the server and client actually talk with one another
 */
 
public class DigitsProtocol {
	//Initialize variables
    private static final int WAITING = 0;
    private static final int SENTINITMESSAGE = 1;
 
    private int state = WAITING;
       
    public String processInput(String strInput, BlockingQueue<String> blockingQueue) 
    {
        String strOutput = null;
 
        if (state == WAITING) 
        {
            strOutput = "Waiting for input number (9 digits)";
            state = SENTINITMESSAGE;
        } 
        else if (state == SENTINITMESSAGE) 
        {
            if (strInput.equalsIgnoreCase("terminate")) 
            {
                ApplicationServer.terminateFlag.set(1);
            	strOutput = "Terminated.";
            } 
            else 
            {
            	String DigitRegex = "[0-9]{1,9}";
            	Pattern digitPattern = Pattern.compile(DigitRegex);
            	if (digitPattern.matcher(strInput).matches())
            	{
            		//make sure the number is 9 digits by padding with zeros
            		strInput = String.format("%09d", Integer.parseInt(strInput));
            		strOutput = "Input: " + strInput;
            		
            		//duplicate checking 
            		if (ApplicationServer.numbers.containsKey(strInput)){
            			ApplicationServer.intervalDuplicates.incrementAndGet();
            		}
            		else{
            			           			
            			ApplicationServer.intervalUnique.incrementAndGet();
            			ApplicationServer.totalUnique.incrementAndGet();
            			ApplicationServer.numbers.putIfAbsent(strInput, strInput);
            			try {
            				strInput = String.format(strInput + "%n");
            	            blockingQueue.put(strInput);
            	            
            	          } catch (InterruptedException ie) {
            	            assert false;
            	          }
            		}
            		
            		state = SENTINITMESSAGE;
            	}
            	else
            	{
            		//this happens when they send bad data
            		//want to terminate the connection without comment
            		strOutput = "close";
            	}
            	
            }
        } 
        return strOutput;
    }
}