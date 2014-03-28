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
	
}
