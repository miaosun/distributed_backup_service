package Peer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import subprotocols.FileBackup;
import subprotocols.FileDeletion;
import subprotocols.FileRestore;
import subprotocols.SpaceReclaiming;
import multicastMsgs.MControlReader;
import multicastMsgs.MDBackupMsg;
import multicastMsgs.MDRestoreMsg;

public class Peer {


	public static Scanner scanner = new Scanner(System.in);

	static List<Chunk> backedupChunks; // Arraylist com chunks armazenados
	static List<FileInfo> filesInfo; // FileInfo(Filename, fileID, nTotalChunks, repDegree)
	static HashMap<Chunk, ArrayList<PeerAddress>> storedsInfo; // informacao chunk->peers
	static HashMap<Chunk, Boolean> waitingChunksToSend;
	static HashMap<Chunk, Boolean> waitingPutChunksAtReclaiming;

	static Chunk waitingChunk = null;
	static boolean received = false;

	public static HashMap<Chunk, ArrayList<PeerAddress>> getStoredsInfo() {
		return storedsInfo;
	}

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

		if(args.length==2) {
			Definitions.setVersion(args[0]);
			Definitions.setPlusRepDegree(Integer.parseInt(args[1]));
		}
		
		if(args.length >=6) {
			Definitions.setMCADDRESS(args[0]);
			Definitions.setMCPORT(Integer.parseInt(args[1]));
			Definitions.setMDBADDRESS(args[2]);
			Definitions.setMDBPORT(Integer.parseInt(args[3]));
			Definitions.setMDRADDRESS(args[4]);
			Definitions.setMDRPORT(Integer.parseInt(args[5]));
			if(args.length==8) {
				Definitions.setVersion(args[6]);
				Definitions.setPlusRepDegree(Integer.parseInt(args[7]));
			}
			System.out.println("Address/Ports:");
		}
		else
		{
			System.out.println("Default Address/Ports:");
		}
		System.out.println("MC: "+Definitions.MCADDRESS+" : "+Definitions.MCPORT);
		System.out.println("MDB: "+Definitions.MDBADDRESS+" : "+Definitions.MDBPORT);
		System.out.println("MDR: "+Definitions.MDRADDRESS+" : "+Definitions.MDRPORT);


		//Estruturas de Dados
		backedupChunks = new ArrayList<Chunk>();
		filesInfo = new ArrayList<FileInfo>();
		storedsInfo = new HashMap<Chunk, ArrayList<PeerAddress>>();
		waitingChunksToSend = new HashMap<Chunk, Boolean>();
		waitingPutChunksAtReclaiming = new HashMap<Chunk, Boolean>();


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

	public static int getDesiredRepDegByfileID(String fileID) {
		for(FileInfo f : filesInfo) {
			if(f.getFileID().equals(fileID)) {
				return f.getReplicationDegree();
			}
		}
		return 0;
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
		if(waitingChunksToSend.containsKey(ch))
			return waitingChunksToSend.get(ch);
		else
			return false;
	}

	//SPACE RECLAIMING
	public static void insertwaitingPutChunk(Chunk ch) {
		waitingPutChunksAtReclaiming.put(ch, false);
	}
	public static boolean verifycontainsWaitingPutChunk(Chunk ch) {
		if(waitingPutChunksAtReclaiming.containsKey(ch))
			return waitingPutChunksAtReclaiming.get(ch);
		else
			return false;
	}
	public static boolean wputchunkAlreadySent(Chunk ch) {
		return waitingPutChunksAtReclaiming.get(ch);
	}



	public static boolean chunkExists(Chunk ch) {
		return backedupChunks.contains(ch);
	}

	public static String getFilenameByFileID(String fileID) {
		for(FileInfo f : filesInfo) {
			if(f.getFileID().equals(fileID))
				return f.getFilename();
		}
		return "";
	}

	public static FileInfo existsFile(String fname) {
		for(FileInfo f : filesInfo ) {
			if((f.getFilename()).equals(fname)) {
				return f;
			}
		}
		return null;
	}

	public static int addtoFilesInfo(String filename, String fileID, int nTotalChunks, int repdeg) {
		for(FileInfo f : filesInfo) {
			if(f.getFilename().equals(filename)) {
				if(f.getFileID().equals(fileID))
					return 0;
				else
					return 1;
			}
		}
		FileInfo finfo = new FileInfo(filename, fileID, nTotalChunks, repdeg);
		filesInfo.add(finfo);
		return -1;
	}


	public static void addBackedupChunk(Chunk newchunk) {
		backedupChunks.add(newchunk);
	}
	public static void removeBackedupChunk(Chunk c) {
		backedupChunks.remove(c);
		storedsInfo.remove(c);
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

	public static void removePeerInHash(Chunk ch, PeerAddress p) {
		ArrayList<PeerAddress> peerList = storedsInfo.get(ch);

		if(peerList.contains(p)) {
			peerList.remove(p);
		}
	}

	//return true se nr de peers com removedChunk >= desiredReplicationDegree
	public static boolean verifyDesiredRepDegree(Chunk removedChunk) {
		int listsize = (storedsInfo.get(removedChunk)).size();
		int drepdeg=0;
		for(Chunk c : backedupChunks) {
			if(c.equals(removedChunk)) {
				drepdeg=c.getDesiredReplicationNr();
				break;
			}
		}
		if(listsize < drepdeg)
			return false;
		else
			return true;
	}

	private static void menu() throws IOException {

		while(true) {		
			//loadFiles();
			System.out.println("\n\nPlease Make a selection:"); 
			System.out.println("[1] Backup a file"); 
			System.out.println("[2] Restore a file"); 
			System.out.println("[3] Delete a file");
			System.out.println("[4] Space Reclaiming");
			System.out.println("[5] Exit"); 

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
				System.out.println("*Delete File*");
				deleteFileRequest();
				break;

			case 4:
				System.out.println("*Space Reclaiming*");
				spaceReclaimingRequest();
				break;

			case 5:
				saveFiles();
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

	private static void deleteFileRequest() throws IOException {
		Boolean b = true;
		while(b)
		{
			System.out.println("[DELETE]filename: ");
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
			String filename = Definitions.backupFilesDirectory+inputStream.readLine();
			String fID;
			FileInfo finfo = existsFile(filename);
			if(finfo != null)
			{
				b = false;
				fID = finfo.getFileID();
				FileDeletion deletion = new FileDeletion(finfo.getFileID(), true);
				deletion.start();
				try {
					deletion.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//delete file
				try {
					Files.deleteIfExists(Paths.get(Peer.getFilenameByFileID(fID)));
					System.out.println("File deleted successfully.");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
				System.out.println("File hasn't been backed up, try again!\n");
		}

	}

	private static void spaceReclaimingRequest() throws IOException {
		Boolean b = true;
		while(b)
		{
			System.out.print("How many chunks to delete? ");

			int chunksToDelete = scanner.nextInt();
			if(chunksToDelete <= backedupChunks.size() && chunksToDelete > 0)
			{
				b = false;
				SpaceReclaiming sReclaiming = new SpaceReclaiming(chunksToDelete);
				sReclaiming.start();

				try {
					sReclaiming.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
				System.out.println("There is no "+chunksToDelete+" chunks to delete!\n");
		}

	}

	private static int getReplicationDeg() throws IOException {
		Boolean b = true;
		//Scanner sc = new Scanner(System.in);
		while(b)
		{
			System.out.print("How many replications? ");

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


	public static String[] getFileChunkstoDelete(String baseFileID) throws IOException
	{
		File directory = new File(baseFileID).getAbsoluteFile().getParentFile();
		final String justFilename = new File(baseFileID).getName();
		String[] matchingFiles = directory.list(new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				return name.startsWith(justFilename);
			}
		});
		return matchingFiles;
	}

	// LOAD FILES
	public static void loadFiles() {
		try {
			loadBackedupChunks();
			loadFilesInfo();
			loadStoredsInfo();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void loadBackedupChunks() throws IOException {
		File bChunks = new File("backedupChunks.txt");
		FileReader reader = new FileReader(bChunks);
		BufferedReader breader = new BufferedReader(reader);

		String line="";
		while (true){
			line = breader.readLine();
			if(line != null) {
				String[] chunkInfo = line.split(" ");
				Chunk c = new Chunk(chunkInfo[0], Integer.parseInt(chunkInfo[1]), Integer.parseInt(chunkInfo[2].trim()));

				Peer.backedupChunks.add(c);
			}
			else
				break;
		}
		System.out.println("backedupChunks file loaded!");
		breader.close();
	}
	public static void loadFilesInfo() throws IOException {
		File finfo = new File("filesInfo.txt");
		FileReader reader = new FileReader(finfo);
		BufferedReader breader = new BufferedReader(reader);

		String line="";
		while (true){
			line = breader.readLine();
			if(line != null) {
				String[] filesInfo = line.split(" ");
				FileInfo f = new FileInfo(filesInfo[0], filesInfo[1], Integer.parseInt(filesInfo[3]),Integer.parseInt(filesInfo[2]));
				Peer.filesInfo.add(f);
			}
			else
				break;
		}
		System.out.println("filesInfo file loaded!");
		breader.close();
	}
	public static void loadStoredsInfo() throws IOException {
		File sinfo = new File("storedsInfo.txt");
		FileReader reader = new FileReader(sinfo);
		BufferedReader breader = new BufferedReader(reader);

		String line="";
		while (true){
			line = breader.readLine();
			if(line != null) {
				String[] sInfo = line.split(" ");
				int nrA = (sInfo.length-3);
				Chunk c = new Chunk(sInfo[0], Integer.parseInt(sInfo[1]), Integer.parseInt(sInfo[2]));
				ArrayList<PeerAddress> plist = new ArrayList<PeerAddress>();
				for(int i=0; i<nrA;i++) {
					InetAddress ad = InetAddress.getByName(sInfo[3+i].substring(sInfo[3+i].indexOf('/')+1));
					PeerAddress p = new PeerAddress(ad, 0);
					plist.add(p);
				}
				storedsInfo.put(c, plist);
			}
			else
				break;
		}
		System.out.println("storedsInfo file loaded!");
		breader.close();
	}


	// SAVE FILES
	// LOAD FILES
	public static void saveFiles() {
		try {
			saveBackedupChunks();
			saveFilesInfo();
			savestoredsInfo();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void saveBackedupChunks() throws IOException
	{
		FileOutputStream saveFile = new FileOutputStream("backedupChunks.txt");
		String line = "";
		byte[] lineByte = new byte[1024];
		for(Chunk ch : backedupChunks)
		{
			line = ch.getFileID() + " " + ch.getChunkNR() + " " + ch.getDesiredReplicationNr() + "\n";
			lineByte = line.getBytes();
			saveFile.write(lineByte);
		}
		System.out.println("backedupChunks file saved!");
		saveFile.close();				
	}

	public static void saveFilesInfo() throws IOException
	{
		FileOutputStream saveFile = new FileOutputStream("filesInfo.txt");
		String line = "";
		byte[] lineByte = new byte[1024];
		for(FileInfo finfo : filesInfo)
		{
			line = finfo.getFilename() + " " + finfo.getFileID() + " " + finfo.getReplicationDegree() + " " + finfo.getnTotalChunks() + "\n";
			lineByte = line.getBytes();
			saveFile.write(lineByte);
		}
		System.out.println("filesInfo file saved!");
		saveFile.close();				
	}

	public static void savestoredsInfo() throws IOException
	{
		FileOutputStream saveFile = new FileOutputStream("storedsInfo.txt");
		String line = "";
		byte[] lineByte = new byte[1024];
		for(Map.Entry<Chunk, ArrayList<PeerAddress>> entry : storedsInfo.entrySet())
		{
			line = entry.getKey().getFileID() + " " + entry.getKey().getChunkNR() + " " + entry.getKey().getDesiredReplicationNr();
			for(PeerAddress pa : entry.getValue())
			{
				line += " " + pa.getAddress();
			}
			line += "\n";
			lineByte = line.getBytes();
			saveFile.write(lineByte);
		}
		System.out.println("storedsInfo file saved!");
		saveFile.close();				
	}

}
