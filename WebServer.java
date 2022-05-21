package WebServer;
import java.net.*;
import java.io.*;
import java.util.Timer;

public class WebServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(1045);
            System.out.println("Server up and running. Listening to clients.\n");
        } catch (IOException e) {
            System.out.println("Server on port 1045 could not be " + "established");
            System.exit(1);
        }
        Socket clientSocket = null;
        try {
            while (true) {
                try {
                        clientSocket = serverSocket.accept(); //handshake accept 
                    if (clientSocket != null) {
                        System.out.println("A client has connected\n");
                    }
                } catch (IOException e) {
                    System.out.println("A client tried to connect but failed.\n");
                }
                InputStream is = clientSocket.getInputStream();
                DataOutputStream os = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                serverhandler startser = new serverhandler(clientSocket, os, is, br);
                Thread thread = new Thread(startser);
                thread.start();
            }

        } catch (IOException e) {
            System.out.println("A client tried to connect but failed.\n");
        }

    }

}
