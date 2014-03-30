package Peer;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import subprotocols.FileBackup;
import subprotocols.FileRestore;
import multicastMsgs.MControlReader;
import multicastMsgs.MDBackupMsg;
import multicastMsgs.MDRestoreMsg;


public class Peer {


	public static Scanner scanner = new Scanner(System.in);
	
	static List<Chunk> backedupChunks; // Arraylist com chunks armazenados
	static List<FileInfo> filesInfo; // FileInfo(Filename, fileID, nTotalChunks)
	//static Queue<String> userBackupRequests; //String: filename
	//static HashMap<Chunk, Integer> chunksRepDegree; // HashMap com graus de replicao
	//static List<StoredtypeMessage> storedMessages;
	static HashMap<Chunk, ArrayList<PeerAddress>> storedsInfo; // informacao chunk->peers
	static HashMap<Chunk, Boolean> waitingChunksToSend;

	static Chunk waitingChunk = null;
	static boolean received = false;

	public static Chunk getWaitingChunk() {
		return waitingChunk;
	}

	public static void setWaitingChunk(Chunk waitingChunk) {
		Peer.waitingChunk = waitingChunk;
	}

	public static boolean isReceived() {
		return received;
	}

	public static void setReceived(boolean received) {
		Peer.received = received;
	}


	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		//Estruturas de Dados
		backedupChunks = new ArrayList<Chunk>();
		filesInfo = new ArrayList<FileInfo>();
		storedsInfo = new HashMap<Chunk, ArrayList<PeerAddress>>();
		waitingChunksToSend = new HashMap<Chunk, Boolean>();

		//TODO fazer set dos enderecos multicast e portos??

		//lancar thread ler MC
		MControlReader mc = new MControlReader(Definitions.MCADDRESS, Definitions.MCPORT);
		mc.start();

		//lancar thread ler MDB
		MDBackupMsg mdb = new MDBackupMsg(Definitions.MDBADDRESS, Definitions.MDBPORT);
		mdb.start();

		//lancar thread ler MDR
		MDRestoreMsg mdr = new MDRestoreMsg(Definitions.MDRADDRESS, Definitions.MDRPORT);
		mdr.start();

		menu();
	}

	public static boolean wcAlreadySent(Chunk ch) {
		return waitingChunksToSend.get(ch);
	}
	public static void wcIsSent(Chunk ch) {
		waitingChunksToSend.put(ch, true);
	}

	public static void insertWaitingChunkToSend(Chunk ch) {
		waitingChunksToSend.put(ch, false);
	}
	public static boolean verifyWaitingChunk(Chunk ch) {
		if(waitingChunksToSend.containsKey(ch)) {
			return waitingChunksToSend.get(ch);
		}
		else
			return false;
	}

	public static boolean chunkExists(Chunk ch) {
		return backedupChunks.contains(ch);
	}
	
//	public static void addToFilesInfo() {
//		
//	}

	public static FileInfo existsFile(String fname) {
		for(FileInfo f : filesInfo ) {
			if(f.getFilename().equals(fname)) {
				return f;
			}
		}
		return null;
	}

	public static int addtoFilesInfo(String filename, String fileID, int nTotalChunks) {
		for(FileInfo f : filesInfo) {
			if(f.getFilename().equals(filename)) {
				if(f.getFileID().equals(fileID))
					return 0;
				else
					return 1;
			}
		}
		FileInfo finfo = new FileInfo(filename, fileID, nTotalChunks);
		filesInfo.add(finfo);
		return -1;
	}


	public static void addBackedupChunk(Chunk newchunk) {
		backedupChunks.add(newchunk);
	}
	public static List<Chunk> getBackedupChunks() {
		return backedupChunks;
	}

	public static void addtoStoredsInfo(Chunk ch, PeerAddress p) {
		if(storedsInfo.containsKey(ch)) {
			ArrayList<PeerAddress> peerList = storedsInfo.get(ch);

			if(!peerList.contains(p)) {
				peerList.add(p);
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
			return storedsInfo.get(ch).size();
		}
		else
		{
			return 0;
		}
	}

	private static void menu() throws IOException {
		
		while(true) {		

			System.out.println("Please Make a selection:"); 
			System.out.println("[1] Send putchunk message"); 
			System.out.println("[2] Restore a file:"); 
			System.out.println("[3] exit"); 

			System.out.println("Selection: ");

			int selection=0;
			try{
				selection=scanner.nextInt();
			}catch(NoSuchElementException e){}
			
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
				break;
			}
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
				int replicationDeg = getReplicationDeg();
				b = false;
				FileBackup backup = new FileBackup(filename, replicationDeg);
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
		Boolean b = true;
		while(b)
		{
			System.out.println("[RESTORE]filename: ");
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
			String filename = Definitions.backupFilesDirectory+inputStream.readLine();

			FileInfo finfo = existsFile(filename);
			if(finfo != null)
			{
				b = false;
				FileRestore restore = new FileRestore(finfo);
				restore.start();
				try {
					restore.join();
				} catch (InterruptedException e) {
					System.out.println("Exception: another thread has interrupted the current thread");
					e.printStackTrace();
				}
			}
			else
				System.out.println("File hasn't been backed up, try again!\n");
		}
	}

	private static int getReplicationDeg() throws IOException {
		Boolean b = true;
		//Scanner sc = new Scanner(System.in);
		while(b)
		{
			System.out.println("How many replications? ");

			int replicationDeg = scanner.nextInt();
			//sc.close();
			if(replicationDeg <= 9 && replicationDeg > 0) {
				//scanner.close();
				return replicationDeg;
			}
			else
				System.out.println("Can't have this number of replications, try again!\n");
		}
		return 0;
	}
}
