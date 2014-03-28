import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import subprotocols.FileBackup;
import utilities.FileSplitter;
import multicastMsgs.MControlReader;
import multicastMsgs.MDBackupMsg;


public class Peer {

	//MC Channel
	public final static int mcPort = 1201;
	public final static String mcAdr = new String("230.0.0.1"); //any class D address
	
	//MDB Channel
	public final static int mdbPort = 1200;
	public final static String mdbAdr = new String("230.0.0.2"); //any class D address
	
	//MDR Channel
	
	static List<Chunk> backedupChunks;
	static Queue<String> backupRequests; //String: filename
	static List<StoredtypeMessage> storedMessages;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		//Estruturas de Dados
		backedupChunks = new ArrayList<Chunk>();
		backupRequests = new LinkedList<String>();
		storedMessages = new ArrayList<StoredtypeMessage>();
		
		
		//TODO fazer set dos enderecos multicast e portos??
		
		
		//lan�ar thread ler MC
		MControlReader mc = new MControlReader(mcAdr, mcPort);
		mc.start();
		
		//lan�ar thread ler MDB
		MDBackupMsg mdb = new MDBackupMsg(mdbAdr, mdbPort);
		mdb.start();
		//FileSplitter.split("test.pdf");
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
		Boolean b = true;
		while(b)
		{
			System.out.println("filename: ");
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
			String filename = inputStream.readLine();
			
			//TODO verificar se ficheiro existe!
			
			File f = new File(filename);
			if(f.exists() && !f.isDirectory())
			{
				b = false;
				FileBackup backup = new FileBackup(filename);
				backup.start();	
			}
			else
				System.out.println("File not exists, try again!\n");
		}
		
	}

}
