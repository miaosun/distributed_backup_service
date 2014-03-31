package subprotocols;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import multicastMsgs.MControlReader;
import multicastMsgs.MDBackupMsg;
import multicastMsgs.MulticastChannelMsg;
import Peer.Chunk;
import Peer.Definitions;
import Peer.Peer;
import Peer.PeerAddress;

public class SpaceReclaiming extends MulticastChannelMsg {

	int nChunkToDelete;
	Chunk removedChunk;
	boolean isInitiatorPeer;
	PeerAddress peer;

	public SpaceReclaiming(int nChunkToDelete) throws IOException {
		super(Definitions.MCADDRESS, Definitions.MCPORT);
		this.nChunkToDelete = nChunkToDelete;
		this.isInitiatorPeer = true;
		this.removedChunk=null;
		this.peer=null;
	}

	public SpaceReclaiming(Chunk c, PeerAddress p) throws IOException {
		super(Definitions.MCADDRESS, Definitions.MCPORT);
		this.isInitiatorPeer = false;
		this.removedChunk = c;
		this.nChunkToDelete=-1;
		this.peer=p;
	}

	public void run() {

		if(isInitiatorPeer) {
			
			String chunkName="";
			for(Chunk ch : Peer.getBackedupChunks()) {
			System.out.println(":::: "+ch.getFileID()+" "+ch.getChunkNR() );
              }
							 
			for(int i=0; i<nChunkToDelete; i++) {

				Chunk chk = null;
				int max = 0;
				for(Chunk ch : Peer.getBackedupChunks()) {
					int dif = (Peer.getStoredsInfo().get(ch).size() - ch.getDesiredReplicationNr());	
					if(dif >= max)
					{
						max = dif;
						chk = ch;
					}			
				}

				chunkName = chk.getFileID() + "." + chk.getChunkNR();

				try {
					Files.delete(Paths.get(Definitions.backupFilesDirectory+chunkName));
					System.out.println("Chunk " + chunkName + " deleted!");
					Peer.removeBackedupChunk(chk);
					MControlReader rMsg = new MControlReader(Definitions.MCADDRESS, Definitions.MCPORT);
					
					String removedMsg = "REMOVED " + Definitions.version +" " + chk.getFileID() + " " + chk.getChunkNR() + Definitions.CRLF + Definitions.CRLF;
					byte[] sendData = new byte[100];
					
					sendData = removedMsg.getBytes();
					rMsg.sendPacket(sendData);
					Thread.sleep(200);
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			}
		}


		else { //normal peer

			Peer.removePeerInHash(removedChunk,peer);
			if(!Peer.verifyDesiredRepDegree(removedChunk)) {
				if(!Peer.verifycontainsWaitingPutChunk(removedChunk)) {
					Peer.insertwaitingPutChunk(removedChunk);
					Random random = new Random();
					//waits timeout time before sending STORED message
					int timeout = random.nextInt(401);
					try {
						Thread.sleep(timeout);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(!Peer.wputchunkAlreadySent(removedChunk)) {
						try {
							String fileIDx = removedChunk.getFileID();
							MDBackupMsg bMsg = new MDBackupMsg(Definitions.MDBADDRESS, Definitions.MDBPORT, removedChunk.getFileID(), removedChunk.getDesiredReplicationNr());
							String chunkFilename = Definitions.backupFilesDirectory+removedChunk.getFileID()+"."+removedChunk.getChunkNR();
							byte[] body = Files.readAllBytes(Paths.get(chunkFilename));

							long waitTime = 500;
							int attempts = 5;
							boolean repdegReached=false;

							while(attempts>0 && !repdegReached){ //nr tentativas < 5 & nao atingido nr desejado de stored's
								bMsg.putchunkSend(removedChunk.getChunkNR(), body);

								try {
									Thread.sleep(waitTime);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}

								int storedsNr = Peer.getStoredsNr(removedChunk);
								//verificar se ja se obteve nr desejado de respostas, se sim repdegReached = true
								if(storedsNr >= removedChunk.getDesiredReplicationNr())
								{
									System.out.println("Chunk sucessfully backed up in "+storedsNr+" peers!");
									repdegReached=true;
								}
								else
								{
									System.out.println("Waiting for more peers...");
									attempts--;
									waitTime*=2;
								}
							}
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		}

	}
}
