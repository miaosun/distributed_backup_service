package Peer;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import subprotocols.FileBackup;
import multicastMsgs.MControlReader;
import multicastMsgs.MDBackupMsg;


public class Peer {
	
//	public static Peer instance = null;
	
	static List<Chunk> backedupChunks;
	//static HashMap<Chunk, int> backedupChunks;
	static Queue<String> userBackupRequests; //String: filename
	static List<StoredtypeMessage> storedMessages;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		//instance = new Peer();
		
		//Estruturas de Dados
		backedupChunks = new ArrayList<Chunk>();
		userBackupRequests = new LinkedList<String>();
		storedMessages = new ArrayList<StoredtypeMessage>();
		
		//TODO fazer set dos enderecos multicast e portos??
		
		
		//lançar thread ler MC
		MControlReader mc = new MControlReader(Definitions.MCADDRESS, Definitions.MCPORT);
		mc.start();
		
		//lançar thread ler MDB
		MDBackupMsg mdb = new MDBackupMsg(Definitions.MDBADDRESS, Definitions.MDBPORT);
		mdb.start();
		
		menu();
	}
	
//	public static Peer getInstance() {
//		return instance;
//	}
	
	public static void addBackedupChunk(Chunk newchunk) {
		backedupChunks.add(newchunk);
	}
	public static List<Chunk> getBackedupChunks() {
		return backedupChunks;
	}

	public static void addUserBackupRequest(String filename) {
		userBackupRequests.add(filename);
	}
	public static Queue<String> getUserBackupRequests() {
		return userBackupRequests;
	}
	
	public static void addStoredMessage(StoredtypeMessage storedmsg) {
		storedMessages.add(storedmsg);
	}
	public static List<StoredtypeMessage> getStoredMessages() {
		return storedMessages;
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
				System.out.println("*Backup file*");
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
			System.out.print("filename: ");
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
			String filename = Definitions.backupFilesDirectory+inputStream.readLine();
			
			File f = new File(filename);
			if(f.exists() && !f.isDirectory()) //file exists //f.canRead() ? //f.isFile() ?
			{
				b = false;
				FileBackup backup = new FileBackup(filename);
				backup.start();
				try {
					backup.join(); //espera que thread termine
				} catch (InterruptedException e) {
					System.out.println("Exception: another thread has interrupted the current thread");
					e.printStackTrace();
				}
			}
			else
				System.out.println("File not exists, try again!\n");
		}
		
	}

}
