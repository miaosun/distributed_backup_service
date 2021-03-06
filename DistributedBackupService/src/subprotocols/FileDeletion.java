package subprotocols;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import Peer.Definitions;
import Peer.Peer;

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
			System.out.println("Message Sent: "+deleteMsg.substring(0,deleteMsg.length()-4));
			byte[] sendData = new byte[100];

			int count = 3;

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

		}
		else { // receptor delete peer
			try {
				String[] filesToDelete = Peer.getFileChunkstoDelete(Definitions.backupFilesDirectory+fileID);
				for(String s : filesToDelete) {
					System.out.println("Deleting File: " + s);
					Files.deleteIfExists(Paths.get(Definitions.backupFilesDirectory+s));

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}