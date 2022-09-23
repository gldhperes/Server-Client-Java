package br.edu.uni7.sd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class MyClient{
	public static void main(String[] args) throws Exception {

		Socket socket = new Socket("localhost", 8000);

		if (socket.isConnected()){
			Thread sma = new Thread(new sendMsgAnytime(socket));
			sma.start();
			Thread rma = new Thread(new receiveMsgAnytime(socket));
			rma.start();
		}


	}

	private static class sendMsgAnytime implements Runnable {
		private Socket socket;

		public sendMsgAnytime(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			// Client sends a message to Server

			while(this.socket.isConnected()){

				PrintStream saida = null;

				try {
					saida = new PrintStream(this.socket.getOutputStream());

					Scanner teclado = new Scanner(System.in);
					while (teclado.hasNextLine()) {
						saida.println(teclado.nextLine());
					}

				} catch (IOException e) {
					System.out.println("Error: " + e);
				}
			}
		}
	}

	private static class receiveMsgAnytime implements Runnable {
		private Socket socket;

		public receiveMsgAnytime(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			while(this.socket.isConnected()){
				//	Client receive a message from Server

				try {
					Scanner entrada = new Scanner(this.socket.getInputStream());
					while(entrada.hasNextLine()){
						System.out.println(entrada.nextLine());
					}
				} catch (IOException e) {
					System.out.println("Error: " + e);
				}
			}
		}
	}
}
