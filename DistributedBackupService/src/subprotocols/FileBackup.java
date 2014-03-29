package subprotocols;


import java.io.IOException;

import Peer.Definitions;
import Peer.Peer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import multicastMsgs.MDBackupMsg;
import utilities.FileSplitter;
import utilities.SHA256;

public class FileBackup extends Thread {

	String filename;

	public FileBackup(String filename) {
		this.filename=filename;
	}

	public void run() {
		try {
			File f = new File(filename);
			FileSplitter.split(filename);
			String bitString = filename+f.lastModified()+f.length();
			System.out.println("bitString: "+bitString);
			String fileID = SHA256.hash256(bitString);
			System.out.println("fileID: "+fileID);
			int numberChunkParts = FileSplitter.getNumberParts(filename);
			System.out.println("numberChunkParts: "+numberChunkParts);
			int replicationDeg = getReplicationDeg();
			System.out.println("REPDEGREE: "+replicationDeg);

			MDBackupMsg bMsg = new MDBackupMsg(Definitions.MDBADDRESS, Definitions.MDBPORT, fileID, replicationDeg);
			System.out.println("MDBackup created");

			for(int chunknr=0; chunknr < numberChunkParts; chunknr++)
			{
				String chunkFilename = filename+"."+chunknr;
				byte[] body = Files.readAllBytes(Paths.get(chunkFilename));

				long waitTime = 500;
				int attempts = 5;
				boolean repdegReached=false;
				while(attempts>0 && !repdegReached){ //nr tentativas < 5 & nao atingido nr desejado de stored's
					System.out.println("Sending chunk...");
					bMsg.putchunkSend(chunknr, body);

					try {
						Thread.sleep(waitTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					int storedsNr = Peer.getStoredMessages().size();
					//verificar se ja se obteve nr desejado de respostas, se sim repdegReached = true
					if(storedsNr >= replicationDeg)
					{
						System.out.println("Chunk sucessfully backed up in "+storedsNr+" peers!");
						repdegReached=true;
					}
					else
					{
						attempts--;
						waitTime*=2;
					}
					Peer.resetStoredMessagesList();
				}			

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	private static int getReplicationDeg() throws IOException {
		Boolean b = true;
		Scanner sc = new Scanner(System.in);
		while(b)
		{
			System.out.println("How many replications? ");

			int replicationDeg = sc.nextInt();

			if(replicationDeg <= 9 && replicationDeg > 0){
				sc.close();
				return replicationDeg;
			}
			else
				System.out.println("Can't have this number of replications, try again!\n");
		}
		sc.close();
		return 0;
	}

}