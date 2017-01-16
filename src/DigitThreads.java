
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.io.*;

/*
 * Author: Andrea Johnson
 * Date: 1/15/2017
 * 
 * 
 * This is the thread class
 * I saw online Runnable is usually the preferred method but for this particular instance 
 * it didn't seem as pressing to implement it that way, if I were going to extend this further
 *  I would look into converting it into using Runnable instead
 */

public class DigitThreads extends Thread {
	//Initialize variables
    private Socket socket = null;
    protected BlockingQueue<String> blockingQueue = null;

    public DigitThreads(Socket socket, BlockingQueue<String> blockingQueue) {
        super("DigitThreads");
        this.socket = socket;
        this.blockingQueue = blockingQueue; 
    }
       
    public void run() {
    	//Initialize variables
    	int localTerminateFlag = 0;
    	int localClientCount = ApplicationServer.clientCount.incrementAndGet();
    	
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()));
        ) {
        	//This section reads and sends the information from/to the client
        	//Specifically it'll send the client the padded version of the numbers if theirs was shorter than 9
        	//this was mostly used for debugging, but it wasn't clear exactly how this server is being used
        	//it can easily be modified not to return the information by commenting out the out.println and out.flush lines
            String inputLine, outputLine;
            DigitsProtocol digitProtocol = new DigitsProtocol();
            outputLine = digitProtocol.processInput(null, blockingQueue);
            out.println(outputLine);
            
            while ((localTerminateFlag !=1 ) && (inputLine = in.readLine()) != null  && (!outputLine.toLowerCase().equals("close"))) {
                outputLine = digitProtocol.processInput(inputLine, blockingQueue);
                if (!outputLine.toLowerCase().equals("close")){
                	out.println(outputLine);
                	out.flush();
                }
                else{
                	out.println("");
                	out.flush();
                	break;               	
                }
                
                localTerminateFlag = ApplicationServer.terminateFlag.get();           
            }
                        
            socket.close();
            ApplicationServer.clientCount.decrementAndGet();
        } catch( EOFException eof ) {
            System.out.println("Client closed the connection.");
            ApplicationServer.clientCount.decrementAndGet();
        }catch (IOException e) {
            //e.printStackTrace();
            ApplicationServer.clientCount.decrementAndGet();
            Thread.currentThread().interrupt();
            return;
        }
    }
}
