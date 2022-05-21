package WebServer;

import java.io.*;
import java.net.*;
import java.net.ServerSocket;

public final class serverhandler extends Thread {

	private Socket clientSocket;
	private DataOutputStream os;
	private InputStream is;
	private BufferedReader br;

	serverhandler(Socket clientSocket, DataOutputStream os, InputStream is, BufferedReader br) {
		this.clientSocket = clientSocket;
		this.os = os;
		this.is = is;
		this.br = br;
	}

	private void request() {
		try {
			clientSocket.setKeepAlive(true);
			while (clientSocket.getKeepAlive()) {
				clientSocket.setSoTimeout(10000);
				File file = readFileFromRequest(br);
				sendFile(file, os);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		try {
			if (br != null)
				br.close();
			if (os != null)
				os.close();
			if (clientSocket != null)
				clientSocket.close();
			System.out.println("Client has disconnected\n");
		} catch (Exception e) {
		}
	}

	static void sendFile(File myFile, DataOutputStream os) throws Exception {

		byte[] buffer = new byte[1024 * 4];
		int i = 0;
		if (myFile.exists() && !myFile.isDirectory()) {
			FileInputStream fis = new FileInputStream(myFile);
			if (myFile.getName().endsWith(".html")) {
				os.writeBytes("HTTP/1.1 200 OK" + "\r\n");
				os.writeBytes("Content-Type: text/html" + "\r\n");
				os.writeBytes("Connection: keep-alive" + "\r\n");
				os.writeBytes("Content-length: " + myFile.length() + "\r\n");
				os.writeBytes("\r\n");
				while ((i = fis.read(buffer)) != -1) {
					os.write(buffer, 0, i);
				}
				System.out.println("File " + myFile.getName() + " has been sent\n");
			} else if (myFile.getName().endsWith(".jpg")) {
				os.writeBytes("HTTP/1.1 200 OK" + "\r\n");
				os.writeBytes("Content-Type: image/jpg" + "\r\n");
				os.writeBytes("Connection: keep-alive" + "\r\n");
				os.writeBytes("Content-length: " + myFile.length() + "\r\n");
				os.writeBytes("\r\n");
				while ((i = fis.read(buffer)) != -1) {
					os.write(buffer, 0, i);
				}
				System.out.println("File " + myFile.getName() + " has been sent\n");
			}
		}
		else {
			os.writeBytes("HTTP/1.1 404 Not Found");
			os.writeBytes("Content-Type: text/html");
			os.writeBytes("Connection: keep-alive" + "\r\n");
			os.writeBytes("\r\n");
			os.writeBytes("<h1>Error 404</h1>");
			os.writeBytes("<h3> File not Found  </h3>");
			System.out.println("Client requested file and it was not found on the server\n");
		}
	}

	static File readFileFromRequest(BufferedReader br) throws Exception {
		String inputRequest = br.readLine();
		System.out.println("requested file: " + inputRequest);
		String[] userInput = inputRequest.split(" ");
		String fileName = "." + userInput[1];
		System.out.println(fileName);
		File myFile = new File(fileName);
		String empty;
		while (!(empty = br.readLine()).isEmpty()) {
			System.out.println(empty);
		}
		return myFile;
	}

	public void run() {
		request();
	}
}