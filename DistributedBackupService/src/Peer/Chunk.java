package Peer;

import java.io.FileOutputStream;
import java.io.IOException;

public class Chunk {

	String fileID;
	int chunkNR;
	int desiredReplicationNr;

	public Chunk(String fileID, int chunkNR, int desiredReplicatinNr) {
		this.fileID=fileID;
		this.chunkNR=chunkNR;
		this.desiredReplicationNr = desiredReplicatinNr;
	}

	public int getDesiredReplicationNr() {
		return desiredReplicationNr;
	}

	public String getFileID() {
		return fileID;
	}

	public int getChunkNR() {
		return chunkNR;
	}

	public boolean exists() {
		return Peer.getBackedupChunks().contains(this);	
	}
	
	public void saveChunk(byte[] data) throws IOException {
		
		System.out.println("Saving Chunk: "+fileID+" "+chunkNR);
		//guardar ficheiro
		FileOutputStream fos = new FileOutputStream(Definitions.backupFilesDirectory+this.fileID+"."+this.chunkNR);
		fos.write(data);
		fos.close();

		//guardar chunk no hashmap/list
		Peer.addBackedupChunk(this);
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj instanceof Chunk && ((this.fileID).equals( ((Chunk)obj).fileID)) && ((this.chunkNR) == ((Chunk)obj).chunkNR) )
			return true;
		else
			return false;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 1;
	}
}
