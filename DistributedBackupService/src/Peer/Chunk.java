package Peer;

import java.io.FileOutputStream;
import java.io.IOException;

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
	
	public void saveChunk(byte[] data) throws IOException {
		
		//guardar ficheiro
		System.out.println("TEST DATA: "+data.length);
		FileOutputStream fos = new FileOutputStream(Definitions.backupFilesDirectory+this.fileID+"."+this.chunkNR);
		fos.write(data);
		fos.close();

		//guardar chunk no hashmap/list
		Peer.addBackedupChunk(this);
		
		System.out.println("TEST: "+Peer.backedupChunks.size());
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj instanceof Chunk && ((this.fileID).equals( ((Chunk)obj).fileID)) && ((this.chunkNR) == ((Chunk)obj).chunkNR) )
			return true;
		else
			return false;
	}
}
