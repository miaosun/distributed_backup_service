package Peer;

public class Chunk {

	String fileID;
	int chunkNR;
	int replicationDegree;

	public Chunk(String fileID, int chunkNR, int repDegree) {
		this.fileID=fileID;
		this.chunkNR=chunkNR;
		this.replicationDegree=repDegree;
	}

	public boolean exists() {
		return Peer.getBackedupChunks().contains(this);	
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj instanceof Chunk && ((this.fileID).equals( ((Chunk)obj).fileID)) && ((this.chunkNR) == ((Chunk)obj).chunkNR) )
			return true;
		else
			return false;
	}
}
