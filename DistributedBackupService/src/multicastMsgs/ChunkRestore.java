package multicastMsgs;

import java.io.IOException;

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
		
		
	}
}
