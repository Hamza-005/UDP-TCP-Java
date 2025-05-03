 package networks1;

import java.io.*;
import java.net.*;

public class TCPClient {
    private static final String SERVER_ADDRESS = "localhost"; 
    private static final int SERVER_PORT = 1596; 

    public static void main(String[] args) {
        try (Socket clientSocket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream())) {

            String request;
            while (true) {
                System.out.print("Enter request (type 'exit' to quit): ");
                request = inFromUser.readLine();

                if (request.equalsIgnoreCase("exit")) {
                    break; 
                }

                outToServer.writeBytes(request + "\n");

                System.out.println("Sent to server: " + request);

                String response = inFromServer.readLine();
                System.out.println("Response from server: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




