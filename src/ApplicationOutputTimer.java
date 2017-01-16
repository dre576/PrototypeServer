import java.io.File;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
/*
 * Author: Andrea Johnson
 * Date: 1/15/2017
 * 
 * 
 * This class performs the 10 second output updates for both the log file and the standard output
 */

public class ApplicationOutputTimer extends TimerTask {

	protected BlockingQueue<String> blockingQueue = null;

	public ApplicationOutputTimer(BlockingQueue<String> blockingQueue){
		    this.blockingQueue = blockingQueue;
		  }
	
	@Override
	public void run() {
		String output = "Received: " + ApplicationServer.intervalUnique.get() +" unique numbers, "+ ApplicationServer.intervalDuplicates.get() 
		+ " duplicates. Unique total: " + ApplicationServer.totalUnique.get();
		System.out.println(output);
		ApplicationServer.intervalUnique.getAndSet(0);
		ApplicationServer.intervalDuplicates.getAndSet(0);
		LogData();
	}
	
	public void LogData(){
		if (ApplicationServer.writingFlag.get() == 0)
		{
			LogWritingThread logWriter = new LogWritingThread(blockingQueue);
			
			new Thread(logWriter).start();
		}
		else
			System.out.println("File Update occuring");
		
	}

}
