package subprotocols;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import Peer.Definitions;

import multicastMsgs.MulticastChannelMsg;

public class FileDeletion  extends MulticastChannelMsg{

	String fileID;
	boolean isInitiatorPeer;

	public FileDeletion(String fileID, boolean iPeer) throws IOException {
		super(Definitions.MCADDRESS, Definitions.MCPORT);
		isInitiatorPeer=iPeer;
		this.fileID=fileID;
	}

	@Override
	public void run() {
		if(isInitiatorPeer) {

			String deleteMsg = "DELETE " + fileID + Definitions.CRLF + Definitions.CRLF;
			byte[] sendData = new byte[100];

			int count = 5;

			while(count > 0) {
				sendData = deleteMsg.getBytes();
				sendPacket(sendData);
				count--;
				if(count > 0)
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			//delete file
			try {
				Files.deleteIfExists(Paths.get(Peer.Peer.getFilenameByFileID(fileID)));
				System.out.println("File deleted successfully.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		else {
			try {
				String[] filesToDelete = getFileChunkstoDelete(Definitions.backupFilesDirectory+fileID);
				for(String s : filesToDelete) {
					Files.deleteIfExists(Paths.get(s));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static String[] getFileChunkstoDelete(String baseFileID) throws IOException
	{
		File directory = new File(baseFileID).getAbsoluteFile().getParentFile();
		final String justFilename = new File(baseFileID).getName();
		String[] matchingFiles = directory.list(new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				return name.startsWith(justFilename) && name.substring(justFilename.length()).matches("^\\.\\d+$");
			}
		});
		return matchingFiles;
	}

}