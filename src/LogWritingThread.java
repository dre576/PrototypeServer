import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
/*
 * Author: Andrea Johnson
 * Date: 1/15/2017
 * 
 * 
 * This class writes the data from the threads to a log file
 */
public class LogWritingThread implements Runnable{

protected BlockingQueue<String> blockingQueue = null;

  public LogWritingThread(BlockingQueue<String> blockingQueue){
    this.blockingQueue = blockingQueue;
  }

  @Override
  public void run() {
	  ApplicationServer.writingFlag.set(1);
	  PrintWriter writer = null;
    try {
    	FileWriter fw = new FileWriter("numbers.log", true);
	    BufferedWriter bw = new BufferedWriter(fw);
	    writer = new PrintWriter(bw);
        
        while(!blockingQueue.isEmpty()){
            String buffer = blockingQueue.take();
       
            //System.out.print(buffer);
            writer.print(buffer);
            
        }               
    } catch (FileNotFoundException e) {

        e.printStackTrace();
    } catch(InterruptedException e){
    	System.out.println("Interrupted");
    } catch (IOException e) {
				e.printStackTrace();
	}finally{
        writer.close();
        ApplicationServer.writingFlag.set(0);
    } 

  }

}