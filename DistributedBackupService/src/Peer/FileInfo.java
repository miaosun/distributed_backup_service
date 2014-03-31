package Peer;

public class FileInfo {
	String filename;
	String fileID;
	int replicationDegree;
	int nTotalChunks;
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFileID() {
		return fileID;
	}

	public void setFileID(String fileID) {
		this.fileID = fileID;
	}

	public int getnTotalChunks() {
		return nTotalChunks;
	}

	public void setnTotalChunks(int nTotalChunks) {
		this.nTotalChunks = nTotalChunks;
	}
	
	public FileInfo(String filename, String fileID, int nTotalChunks, int repdegree) {
		this.filename = filename;
		this.fileID = fileID;
		this.nTotalChunks = nTotalChunks;
		replicationDegree=repdegree;
	}

	public int getReplicationDegree() {
		return replicationDegree;
	}
	
}
