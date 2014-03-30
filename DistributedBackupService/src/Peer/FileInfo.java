package Peer;

public class FileInfo {
	String filename;
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

	String fileID;
	int nTotalChunks;
	
	public FileInfo(String filename, String fileID, int nTotalChunks) {
		this.filename = filename;
		this.fileID = fileID;
		this.nTotalChunks = nTotalChunks;
	}
	
}
