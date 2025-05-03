package networks1;

import java.io.*;
import java.net.*;

public class UDPClient {
    private static final String SERVER_ADDRESS = "localhost"; 
    private static final int SERVER_PORT = 1596; 

    public static void main(String[] args) {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            String request;

            while (true) {
                System.out.print("Enter request (type 'exit' to quit): ");
                request = inFromUser.readLine();

                if (request.equalsIgnoreCase("exit")) {
                    break; 
                }
                
                byte[] sendData = request.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(SERVER_ADDRESS), SERVER_PORT);
                clientSocket.send(sendPacket);
                
                System.out.println("Sent to server: " + request);
                
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);

                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Response from server: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

