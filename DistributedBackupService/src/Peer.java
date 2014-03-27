import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MulticastSocket;
import java.util.Scanner;

import subprotocols.FileBackup;

import muticastMsgs.MControlReader;
import muticastMsgs.MDBackupMsg;
import muticastMsgs.MulticastChannelMsg;


public class Peer {

	//MC Channel
	public final static int mcPort = 1200;
	public final static String mcAdr = new String("230.0.0.1"); //any class D address
	
	//MDB Channel
	public final static int mdbPort = 1200;
	public final static String mdbAdr = new String("230.0.0.1"); //any class D address
	
	/*
	 * Estruturas de dados
	 * 	Chunks armazenados
	 * 	Respostas "Stored"
	 *  3 endere�os multicast
	 * 	
	 */

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		//lan�ar thread ler MC
		MControlReader mc = new MControlReader(mcAdr, mcPort);
		mc.start();
		
		//lan�ar thread ler MDB
		MDBackupMsg mdb = new MDBackupMsg(mdbAdr, mdbPort);
		mdb.start();
		
		menu();
	}

	private static void menu() throws IOException {

		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);

		while(true) {

			System.out.println("Please Make a selection:"); 
			System.out.println("[1] Send putchunk message"); 
			//System.out.println("[2] Receive putchunk message"); 
			System.out.println("[3] exit"); 

			System.out.println("Selection: ");
			int selection=sc.nextInt();

			switch (selection){

			case 1: 
				System.out.println("Message: ");
				backupRequest();
				break;

			case 2:
				System.out.println("Nothing here.");
				
				break;

			case 3:
				System.out.println("Exit Successful");
				System.exit(0);

			default:
				System.out.println("Please enter a valid selection.");

			};

		}

	}


	private static void backupRequest() throws IOException {
		
		System.out.println("filename: ");
		BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
		String filename = inputStream.readLine();
		
		//TODO verificar se ficheiro existe!
		
		FileBackup backup = new FileBackup(filename);
		backup.start();
	}

}
