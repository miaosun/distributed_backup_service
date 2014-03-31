package subprotocols;

import java.io.IOException;

import multicastMsgs.MControlReader;
import utilities.FileSplitter;
import Peer.Chunk;
import Peer.Definitions;
import Peer.FileInfo;
import Peer.Peer;

public class FileRestore extends Thread {

	public FileRestore(FileInfo finfo) throws IOException {
		// TODO Auto-generated constructor stub

		String fileID = finfo.getFileID();
		int nTotalChunks = finfo.getnTotalChunks();

		MControlReader rMsg = new MControlReader(Definitions.MCADDRESS, Definitions.MCPORT);

		int count = 0;
		
		for(int chunknr=0; chunknr<nTotalChunks; chunknr++)
		{
			String chunkName = fileID + "." + chunknr;
			String getChunkMsg = "GETCHUNK " + Definitions.version +" " + fileID + " " + chunknr + Definitions.CRLF + Definitions.CRLF;
			byte[] sendData = new byte[100];
			
			sendData = getChunkMsg.getBytes();
			int desiredRepDeg = Peer.getDesiredRepDegByfileID(fileID);
			Chunk ch = new Chunk(fileID, chunknr, desiredRepDeg);
			
			long waitTime = 500;
			int attempts = 5;
			Peer.setWaitingChunk(ch);

			Peer.setReceived(false);
			while(attempts>0 && !Peer.isReceived()){ //nr tentativas < 5 & nao ter recebido o chunk
				
				rMsg.sendPacket(sendData);

				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if(Peer.isReceived())
				{
					System.out.println("Chunk " + chunkName + " sucessfully restored!");
					count++;
				}
				else
				{
					System.out.println("Waiting for more peers...");
					attempts--;
					waitTime*=2;
				}
			}
		}
		if(count == nTotalChunks)
			FileSplitter.join(finfo.getFilename(), Definitions.backupFilesDirectory+fileID, nTotalChunks);
		else
			System.out.println("Nao foi possivel de fazer restore do ficheiro");

	}
}
