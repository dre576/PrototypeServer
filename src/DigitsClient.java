
import java.io.*;
import java.net.*;
 
/*
 * Author: Andrea Johnson
 * Date: 1/15/2017
 * 
 * 
 * Basic client class I used for testing my application
 */

public class DigitsClient {
    public static void main(String[] args) throws IOException {
    	//Initialize variables
        String hostName = "127.0.0.1";
        int portNumber = 4000;
        boolean terminated = false;
 
        try (
            Socket digitsSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(digitsSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(digitsSocket.getInputStream()));
        ) {
            BufferedReader stdIn =
                new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;
 
            while ((fromServer = in.readLine()) != null && (!terminated)) {
                System.out.println("Server: " + fromServer);
                if (fromServer.toLowerCase().equals("terminated."))
                {
                    terminated = true;
                }
                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +  hostName);
            System.exit(1);
        }
    }
}
