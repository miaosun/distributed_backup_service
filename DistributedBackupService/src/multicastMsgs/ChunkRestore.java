package multicastMsgs;

import java.io.IOException;
import java.util.Random;

import Peer.Chunk;
import Peer.Definitions;

public class ChunkRestore extends MulticastChannelMsg{

	private String fileID;
	private int chunkNR;

	public ChunkRestore(String fileID, int chunkNR) throws IOException {
		super(Definitions.MCADDRESS, Definitions.MCPORT);
		this.fileID=fileID;
		this.chunkNR=chunkNR;
	}

	@Override
	public void run() {

		Chunk ch = new Chunk(fileID, chunkNR);
		if(chunkExists(ch)){
			
			Random random = new Random();
			//waits timeout time before sending STORED message
			int timeout = random.nextInt(401);
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//verificar se ainda n tem o ficheiro //TODO verificar
			if(!ch.exists())
			{
				//guardar chunk
				try {
					ch.saveChunk(body);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //TODO
			}
			//enviar stored
			String storedMsg = "STORED" + header.substring(header.indexOf(' ')) + Definitions.CRLF + Definitions.CRLF;
			try {
				MControlReader MC = new MControlReader(Definitions.MCADDRESS, Definitions.MCPORT);
				MC.sendMessages(storedMsg);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
		}
	}
}
