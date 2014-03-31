package multicastMsgs;

import java.io.IOException;
import java.net.DatagramPacket;

import subprotocols.FileDeletion;
import subprotocols.SpaceReclaiming;
import Peer.Chunk;
import Peer.Peer;
import Peer.PeerAddress;

public class MControlReader extends MulticastChannelMsg {
	
	public MControlReader(String adr, int port) throws IOException {
		super(adr, port); //TODO change to Definitions. ... ? (& same other mchannels)
	}
	
	public void sendMessages(String msg) {
		sendPacket(msg.getBytes());
	}

	public void processPacket(DatagramPacket packet) throws IOException {
		
		byte[] message = new byte[packet.getLength()-4];
		System.arraycopy(packet.getData(), 0, message, 0, packet.getLength()-4);
		String msg = new String(message);
		
		System.out.println("Message received: "+msg.substring(0,msg.length())+" > from: "+packet.getAddress());
		PeerAddress peer = new PeerAddress(packet.getAddress(),packet.getPort());
		//System.out.println("by Peer: "+packet.getAddress()+"  "+packet.getPort());
		
		//System.out.println("MCReader-> Process Message");
		String[] temp = msg.split(" ");
		String cmd = temp[0].trim();
		String fileID;
		
		if(cmd.equals("STORED")) {
			if(verifyVersion(temp[1].trim())) {
				fileID = temp[2].trim();
				int chunkNr = Integer.parseInt(temp[3].trim());
				int desiredRepDeg = Peer.getDesiredRepDegByfileID(fileID);
				Chunk ch = new Chunk(fileID, chunkNr, desiredRepDeg);
				Peer.addtoStoredsInfo(ch, peer);
			}
		}
		else if(cmd.equals("GETCHUNK")) {
			if(verifyVersion(temp[1].trim())) {
				fileID = temp[2].trim();
				int chunkNr = Integer.parseInt(temp[3].trim());
				ChunkRestore chRestore;
				try {
					int desiredRepDeg = Peer.getDesiredRepDegByfileID(fileID);
					chRestore = new ChunkRestore(fileID, chunkNr, desiredRepDeg);
					chRestore.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else if(cmd.equals("DELETE")) {
			fileID = temp[1].substring(0, temp[1].length()-4);
			FileDeletion fdeletion= new FileDeletion(fileID, false);
			fdeletion.start();
		}
		else if(cmd.equals("REMOVED")) {
			if(verifyVersion(temp[1].trim())) {
				fileID = temp[2].trim();
				System.out.println("FILEID::::::::"+fileID);
				int chunkNr = Integer.parseInt(temp[3].trim());
				int desiredRepDeg = Peer.getDesiredRepDegByfileID(fileID);
				Chunk ch = new Chunk(fileID, chunkNr, desiredRepDeg);
				if(Peer.chunkExists(ch)) {
					SpaceReclaiming s = new SpaceReclaiming(ch, peer);
					s.start();
				}
				else
				{
					System.out.println("Don't have that Chunk");
				}
			}
		}
		else
		{
			System.out.println("MESSAGE IGNORED");
		}
		
	}

	public void run() {
		//System.out.println("Running MC Reader...");
		//ciclo leitura MC
		joinMulticastGroup();
		while(true) {
			try {
				DatagramPacket packet = getPacket();
				processPacket(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
