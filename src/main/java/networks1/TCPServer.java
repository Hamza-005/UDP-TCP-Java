package networks1;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPServer {
    private static final String DATABASE_FILE = "students.txt";

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(1596);
        System.out.println("TCP Server started...");

        while (true) {
            Socket connectionSocket = serverSocket.accept();
            System.out.println("Client connected.");

            handleClient(connectionSocket);
        }
    }

    private static void handleClient(Socket connectionSocket) {
        try (
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream())
        ) {
            String clientRequest;

            while ((clientRequest = inFromClient.readLine()) != null) {
                System.out.println("Received from client: " + clientRequest);

                String serverResponse = handleRequest(clientRequest);
                outToClient.writeBytes(serverResponse + "\n");

                System.out.println("Response sent to client: " + serverResponse);

                if (clientRequest.equalsIgnoreCase("exit")) {
                    System.out.println("Client disconnected.");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String handleRequest(String request) throws IOException {
        String[] parts = request.split(": ", 2);
        String command = parts[0].trim();
        String data = parts.length > 1 ? parts[1].trim() : "";

        List<String> records = readDatabase();

        switch (command) {
            case "INQ":
                for (String record : records) {
                    if (record.startsWith(data)) {
                        return record;
                    }
                }
                return "Student's record is not found";
            case "ADD":
                String[] studentData = data.split(",", 2);
                String studentId = studentData[0].trim();

                for (String record : records) {
                    if (record.startsWith(studentId)) {
                        return "Student with ID " + studentId + " already exists.";
                    }
                }
                writeDatabase(data, true);
                return "Record added";
            case "DEL":
                if (deleteRecord(data)) {
                    return "Record deleted";
                } else {
                    return "Student ID is not found";
                }
            case "UPD":
                String[] updateParts = data.split(":", 2);
                if (updateParts.length < 2) {
                    return "Invalid update format. Correct format: 'UPD: <StudentID>: <NewRecord>'";
                }

                studentId = updateParts[0].trim();
                String newRecord = updateParts[1].trim();
                String[] recordFields = newRecord.split(",");

                if (recordFields.length != 4) {
                    return "Invalid new record format. Correct format: 'name,gender,faculty,year'";
                }

                if (updateRecord(studentId, newRecord)) {
                    return "Record updated";
                } else {
                    return "Student ID is not found";
                }
            default:
                return "Invalid command";
        }
    }

    private static List<String> readDatabase() throws IOException {
        List<String> records = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE));
        String line;
        while ((line = reader.readLine()) != null) {
            records.add(line);
        }
        reader.close();
        return records;
    }

    private static void writeDatabase(String data, boolean append) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE, append));
        writer.write(data);
        writer.newLine();
        writer.close();
    }

    private static boolean deleteRecord(String studentId) throws IOException {
        List<String> records = readDatabase();
        boolean found = false;

        BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE));
        for (String record : records) {
            if (!record.startsWith(studentId)) {
                writer.write(record);
                writer.newLine();
            } else {
                found = true;
            }
        }
        writer.close();
        return found;
    }

    private static boolean updateRecord(String studentId, String newRecord) throws IOException {
        List<String> records = readDatabase();
        boolean found = false;

        BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE));
        for (String record : records) {
            if (!record.startsWith(studentId)) {
                writer.write(record);
            } else {
                String[] recordFields = newRecord.split(",");
                writer.write(studentId + "," + recordFields[0].trim() + "," + recordFields[1].trim() + "," +
                        recordFields[2].trim() + "," + recordFields[3].trim());
                found = true;
            }
            writer.newLine();
        }
        writer.close();
        return found;
    }
}