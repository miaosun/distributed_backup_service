package multicastMsgs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import Peer.Chunk;
import Peer.Definitions;
import Peer.Peer;

public class ChunkRestore extends MulticastChannelMsg{

	private String fileID;
	private int chunkNR;

	public ChunkRestore(String fileID, int chunkNR) throws IOException {
		super(Definitions.MDRADDRESS, Definitions.MDRPORT);
		this.fileID=fileID;
		this.chunkNR=chunkNR;
	}

	@Override
	public void run() {

		Chunk ch = new Chunk(fileID, chunkNR);
		if(Peer.chunkExists(ch)){
			if(!Peer.verifyWaitingChunk(ch)) {
				Peer.insertWatingChunkToSend(ch);
				Random random = new Random();
				//waits timeout time before sending STORED message
				int timeout = random.nextInt(401);
				try {
					Thread.sleep(timeout);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(!wcAlreadySent(ch)) {

					String stringHeader = "CHUNK" + Definitions.version + " " + fileID + " " + chunkNR;
					System.out.println("\"Chunk Message sent: "+stringHeader+"\"");
					stringHeader+=Definitions.CRLF+Definitions.CRLF;
					byte[] header = stringHeader.getBytes();
					String chunkFilename = fileID+"."+chunkNR;
					byte[] body = Files.readAllBytes(Paths.get(chunkFilename));
					
					byte[] message = new byte[header.length+body.length];
					
					System.arraycopy(header, 0, message, 0, header.length);
					System.arraycopy(body, 0, message, header.length, body.length);	
					sendPacket(message);			
				}
			}
		}
	}
}
