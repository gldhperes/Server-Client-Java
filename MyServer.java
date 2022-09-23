package br.edu.uni7.sd;

import com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class MyServer {
	public static void main(String[] args) throws IOException {
		System.out.println("Iniciando o server ...");
		ServerSocket server = new ServerSocket(8000);

		while (true) {
			Socket socket = server.accept();

			Thread t = new Thread(new commands(socket));
			t.start();
		}
	}

	private static class commands implements Runnable {
		private Socket socket;

		public commands(Socket socket) throws IOException {
			this.socket = socket;
			welcomeMsg();
		}

		private void welcomeMsg() throws IOException {
			// Print that a client has connected
			System.out.println(this.socket.getLocalAddress() + " has connected!");

			// Sends to client a Welcome message
			String welcome = "Welcome to the Server, " + this.socket.getLocalAddress();
			PrintStream saida = new PrintStream(this.socket.getOutputStream());
			Scanner teclado = new Scanner(welcome);
			while (teclado.hasNextLine()) {
				saida.println(teclado.nextLine());
			}
		}


		private void sendMsgToClient(String msg) {

			if(this.socket.isConnected()){

				PrintStream saida = null;

				try {
					saida = new PrintStream(this.socket.getOutputStream());

					Scanner sc = new Scanner(msg);
					while (sc.hasNextLine()) {
						saida.println(sc.nextLine());
					}

				} catch (IOException e) {
					System.out.println("Error: " + e);
				}
			}
		}


		@Override
		public void run() {
			try {
				while (this.socket.isConnected()) {

					Scanner entrada = new Scanner(this.socket.getInputStream());

					while (entrada.hasNextLine()) {
						String msg = entrada.nextLine();

						// Checks if received message contains "#"
						// If yes, then, it's a Command
						if (msg.endsWith("#")) {
							String cmd;
							String param;

							// If message contains ";"
							// Then it's a message with parameters
							// Else, it's a normal command
							if (msg.contains(";")) {
								String[] _msg = msg.split(";");
								cmd = _msg[0];
								param = _msg[1].substring(0, _msg[1].length() - 1);
							} else {
								cmd = msg.substring(0, msg.length() - 1);
								param = null;
							}
							System.out.println("Cmd: " + cmd + " param: " + param);
							executeCommand(cmd, param);
						}
					}
				}

				System.out.println("Bye " + this.socket.getLocalAddress());
				socket.close();

			} catch (Exception e) {
				System.out.println("Error: " + e);
			}
		}


		private void executeCommand(String cmd, String param) {
			String path = "C:\\Users\\GUILHERME\\Documents\\UNI7\\2022.2\\Sistemas Distribuidos\\";

			if (param == null) {
				if (cmd.equals("list")) {
					getFiles(path);
				}
			}else{
				if (cmd.equals("delete")) {
					deleteFiles(path, param);
				}
			}
		}

		private void getFiles(String path) {
			File file = new File(path);
			File[] files = file.listFiles();

			ArrayList<String> fileNames = new ArrayList<>();

			for (File f : files) {
//				System.out.println(f.getName());
//				fileNames[i] = f.getName();
//				fileNames.add(f.getName());
				sendMsgToClient(f.getName());
			}

//			sendMsgToClient(fileNames);
//			System.out.println(fileNames);
			
		}

		private void deleteFiles(String path, String param) {
			try{
				File myObj = new File(path + param);
				if (myObj.delete()) {
					sendMsgToClient(myObj.getName() + " deleted.");
				} else {
					sendMsgToClient("Failed to delete.");
				}
			} catch (Exception e) {
				System.out.println("Error: " + e);
			}
		}
	}

}
