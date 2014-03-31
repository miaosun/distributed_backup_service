package subprotocols;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import Peer.Chunk;
import Peer.Definitions;
import Peer.Peer;
import Peer.PeerAddress;
import multicastMsgs.MulticastChannelMsg;

public class SpaceReclaiming extends MulticastChannelMsg {

	int nChunkToDelete;
	boolean isInitiatorPeer;

	public SpaceReclaiming(int nChunkToDelete, boolean isInitiatorPeer) throws IOException {
		super(Definitions.MCADDRESS, Definitions.MCPORT);
		this.nChunkToDelete = nChunkToDelete;
		this.isInitiatorPeer = isInitiatorPeer;
	}

	public void run() {

		if(isInitiatorPeer) {

			String chunkName="";

			for(int i=0; i<nChunkToDelete; i++) {

				Chunk chk = null;
				int max = 0;
				for(Chunk ch : Peer.getBackedupChunks()) {
					int dif = (ch.getDesiredReplicationNr() - Peer.getStoredsInfo().get(ch).size());
					if(dif >= max)
					{
						max = dif;
						chk = ch;
					}			
				}

				chunkName = chk.getFileID() + "." + chk.getChunkNR();

				try {
					Files.delete(Paths.get(chunkName));
					System.out.println("Chunk " + chunkName + " deleted!");
					Peer.revemoBackedupChunk(chk);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

		}


		//			Collections.sort(backUpChunks, new Comparator<Chunk>(){
		//				@Override
		//				public int compare(Chunk ch1, Chunk ch2) {
		//					int x1 = ((Chunk) ch1).getDesiredReplicationNr();
		//					int x2 = ((Chunk) ch2).getDesiredReplicationNr();
		//					return x2 - x1;
		//				}
		//			});
		//
		//			String fileID = "";
		//			int chunkNr = 0;
		//			String chunkName = "";
		//			for(int i=0; i<nChunkToDelete; i++) {
		//				fileID = backUpChunks.get(i).getFileID();
		//				chunkNr = backUpChunks.get(i).getChunkNR();
		//				chunkName = fileID + "." + chunkNr;
		//				try {
		//					Files.delete(Paths.get(chunkName));
		//					System.out.println("Chunk " + chunkName + " deleted!");
		//				} catch (IOException e) {
		//					// TODO Auto-generated catch block
		//					e.printStackTrace();
		//				}
		//			}
		//			



		else {

		}

	}
}
