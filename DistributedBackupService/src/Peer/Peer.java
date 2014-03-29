package Peer;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import subprotocols.FileBackup;
import multicastMsgs.MControlReader;
import multicastMsgs.MDBackupMsg;


public class Peer {
	
	static List<Chunk> backedupChunks; // Arraylist com chunks armazenados
	static HashMap<String, String> filesHash; // HashMap<filename,fileID>
	//static Queue<String> userBackupRequests; //String: filename
	//static HashMap<Chunk, Integer> chunksRepDegree; // HashMap com graus de replica��o
	//static List<StoredtypeMessage> storedMessages;
	static HashMap<Chunk, ArrayList<PeerAddress>> storedsInfo; // informacao chunk->peers
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		//Estruturas de Dados
		backedupChunks = new ArrayList<Chunk>();
		filesHash = new HashMap<String, String>();
		storedsInfo = new HashMap<Chunk, ArrayList<PeerAddress>>();
		
		//TODO fazer set dos enderecos multicast e portos??
		
		//lancar thread ler MC
		MControlReader mc = new MControlReader(Definitions.MCADDRESS, Definitions.MCPORT);
		mc.start();
		
		//lancar thread ler MDB
		MDBackupMsg mdb = new MDBackupMsg(Definitions.MDBADDRESS, Definitions.MDBPORT);
		mdb.start();
		
		menu();
	}

	
	public static void addBackedupChunk(Chunk newchunk) {
		backedupChunks.add(newchunk);
	}
	public static List<Chunk> getBackedupChunks() {
		return backedupChunks;
	}
	
	public static void addtoFilesHash(String filename, String fileID) {
		filesHash.put(filename, fileID);
	}
	public static HashMap<String, String> getFilesHash() {
		return filesHash;
	}

	public static void addtoStoredsInfo(Chunk ch, PeerAddress p) {
		if(storedsInfo.containsKey(ch)) {

			ArrayList<PeerAddress> peerList = storedsInfo.get(ch);

			if(!storedsInfo.get(ch).contains(p)) {
				storedsInfo.get(ch).add(p);
			}
			storedsInfo.put(ch, peerList);
		}
		else {
			ArrayList<PeerAddress> plist = new ArrayList<PeerAddress>();
			plist.add(p);
			storedsInfo.put(ch,plist);
		}
	}
	
	public static int getStoredsNr(Chunk ch) {
		if(storedsInfo.containsKey(ch)) {
			return (storedsInfo.get(ch)).size();
		}
		else
			return 0;

	}
	
	private static void menu() throws IOException {

		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);

		while(true) {

			System.out.println("Please Make a selection:"); 
			System.out.println("[1] Send putchunk message"); 
			System.out.println("[2] Restore a file:");
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
				System.out.println("*Restore file*");
				restoreRequest();
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
			System.out.print("[BACKUP]filename: ");
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
	
	private static void restoreRequest() throws IOException {
		
	}

}
