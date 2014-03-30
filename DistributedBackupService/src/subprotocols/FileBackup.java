package subprotocols;


import java.io.IOException;

import Peer.Chunk;
import Peer.Definitions;
import Peer.FileInfo;
import Peer.Peer;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import multicastMsgs.MDBackupMsg;
import utilities.FileSplitter;
import utilities.SHA256;

public class FileBackup extends Thread {

	String filename;
	int replicationDegree;

	public FileBackup(String filename, int repdegree) {
		this.filename=filename;
		replicationDegree = repdegree;
	}

	public void run() {
		try {
			boolean canGo = false;
			File f = new File(filename);
			String bitString = filename+f.lastModified()+f.length();
			System.out.println("bitString: "+bitString);
			String fileID = SHA256.hash256(bitString);
			System.out.println("fileID: "+fileID);

			FileInfo fx = Peer.existsFile(filename);
			if(fx != null) {
				if(fx.getFileID() == fileID) {
					System.out.println("BACKUP JA REALIZADO E NAO HOUVE ALTERACOES!");
					canGo=false;
				}
				else
				{
					System.out.println("BACKUP JA REALIZADO MAS HOUVE ALTERACOES!");
					//TODO file deletion antes de backup
					canGo=true;
				}
			}
			else
				canGo=true;

			if(canGo) {
				
				FileSplitter.split(filename);

				int numberChunkParts = FileSplitter.getNumberParts(filename);
				System.out.println("numberChunkParts: "+numberChunkParts);
				System.out.println("REPDEGREE: "+replicationDegree);
				
				//save at filesInfo List
				Peer.addtoFilesInfo(filename, fileID, numberChunkParts);
				
				MDBackupMsg bMsg = new MDBackupMsg(Definitions.MDBADDRESS, Definitions.MDBPORT, fileID, replicationDegree);
				System.out.println("MDBackup created");

				for(int chunknr=0; chunknr < numberChunkParts; chunknr++)
				{
					String chunkFilename = filename+"."+chunknr;
					byte[] body = Files.readAllBytes(Paths.get(chunkFilename));

					long waitTime = 500;
					int attempts = 5;
					boolean repdegReached=false;
					Chunk ch = new Chunk(fileID, chunknr, replicationDegree);
					while(attempts>0 && !repdegReached){ //nr tentativas < 5 & nao atingido nr desejado de stored's
						bMsg.putchunkSend(chunknr, body);

						try {
							Thread.sleep(waitTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						int storedsNr = Peer.getStoredsNr(ch);
						//verificar se ja se obteve nr desejado de respostas, se sim repdegReached = true
						if(storedsNr >= replicationDegree)
						{

							System.out.println("Chunk sucessfully backed up in "+storedsNr+" peers!");
							repdegReached=true;
						}
						else
						{
							System.out.println("Waiting for more peers...");
							attempts--;
							waitTime*=2;
						}
					}			
					Files.deleteIfExists(Paths.get(chunkFilename));
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

}