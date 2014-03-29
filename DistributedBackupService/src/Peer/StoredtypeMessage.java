package Peer;

public class StoredtypeMessage {
	
	String fileID;
	int chunkNR;
	PeerAddress peer;
	
	public StoredtypeMessage(String fileID, int chunkNR, PeerAddress peer) {
		this.fileID=fileID;
		this.chunkNR=chunkNR;
		this.peer=peer;
	}
	
	public String getFileID() {
		return fileID;
	}
}
