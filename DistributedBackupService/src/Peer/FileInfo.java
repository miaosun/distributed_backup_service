package Peer;

public class FileInfo {
	String filename;
	String fileID;
	int nTotalChunks;
	
	public FileInfo(String filename, String fileID, int nTotalChunks) {
		this.filename = filename;
		this.fileID = fileID;
		this.nTotalChunks = nTotalChunks;
	}
	
}
