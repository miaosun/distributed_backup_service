package subprotocols;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import multicastMsgs.MDBackupMsg;
import utilities.FileSplitter;
import utilities.SHA256;
import Peer.Chunk;
import Peer.Definitions;
import Peer.Peer;

public class FileRestore {

	public FileRestore(String filename) {
		// TODO Auto-generated constructor stub
		File f = new File(filename);
		String fileID = Peer.getFilesHash().get(filename);
		
		//FileSplitter.join(filename);
		
		MDBackupMsg bMsg = new MDBackupMsg(Definitions.MDBADDRESS, Definitions.MDBPORT, fileID, replicationDeg);
		System.out.println("MDBackup created");

		for(int chunknr=0; chunknr < numberChunkParts; chunknr++)
		{
			String chunkFilename = filename+"."+chunknr;
			byte[] body = Files.readAllBytes(Paths.get(chunkFilename));

			long waitTime = 500;
			int attempts = 5;
			boolean repdegReached=false;
			Chunk ch = new Chunk(fileID, chunknr);
			while(attempts>0 && !repdegReached){ //nr tentativas < 5 & nao atingido nr desejado de stored's
				bMsg.putchunkSend(chunknr, body);

				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				int storedsNr = Peer.getStoredsNr(ch);
				//verificar se ja se obteve nr desejado de respostas, se sim repdegReached = true
				if(storedsNr >= replicationDeg)
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

		}
	}
}
