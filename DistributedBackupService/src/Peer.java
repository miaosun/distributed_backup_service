import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import muticastMsgs.MControlReader;
import muticastMsgs.MDBackupMsg;


public class Peer {

	public final static int PORT = 1200;
	public final static String adr = new String("230.0.0.1"); //any class D address
	
	/*
	 * Estruturas de dados
	 * 	Chunks armazenados
	 * 	Respostas "Stored"
	 * 	
	 */

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		MControlReader mc = new MControlReader(adr,PORT);
		mc.start();

		menu();
	}

	private static void menu() throws IOException {

		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);

		while(true) {

			System.out.println("Please Make a selection:"); 
			System.out.println("[1] Send putchunk message"); 
			System.out.println("[2] Receive putchunk message"); 
			System.out.println("[3] exit"); 

			System.out.println("Selection: ");
			int selection=sc.nextInt();     

			switch (selection){

			case 1: 
				System.out.println("Message: ");
				backupRequest();
				break;

			case 2:
				System.out.println("Waiting message...");
				receiveRequest();
				break;

			case 3:
				System.out.println("Exit Successful");
				System.exit(0);

			default:
				System.out.println("Please enter a valid selection.");

			};

		}

	}

	private static void receiveRequest() {

		
		
	}

	private static void backupRequest() throws IOException {
		/*
		BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
		String sentence = inputStream.readLine();

		MDBackupMsg mdbMsg = new MDBackupMsg(adr,PORT);
		*/
	}

}
