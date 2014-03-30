package multicastMsgs;

import java.io.IOException;
import java.net.DatagramPacket;

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

	public void processPacket(DatagramPacket packet) {
		
		String msg = new String(packet.getData());
		System.out.println("Message received: "+ msg);
		PeerAddress peer = new PeerAddress(packet.getAddress(),packet.getPort());
		System.out.println("Peer: "+packet.getAddress()+"  "+packet.getPort());
		
		System.out.println("MCReader-> Process Message");
		String[] temp = msg.split(" ");
		String cmd = temp[0].trim();
		String fileID = temp[2].trim();
		int chunkNR = Integer.parseInt(temp[3].trim());

		if(cmd.equals("STORED")) {
			if(verifyVersion(temp[1].trim())) {
				Chunk ch = new Chunk(fileID, chunkNR);
				Peer.addtoStoredsInfo(ch, peer);
			}
		}
		else if(cmd.equals("GETCHUNK")) {
			if(verifyVersion(temp[1].trim())) {
				ChunkRestore chRestore;
				try {
					chRestore = new ChunkRestore(fileID, chunkNR);
					chRestore.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else
		{
			System.out.println("MESSAGE IGNORED");
		}
		
	}
	
	@Override
	public void processMsg(String msg) {}
	

	public Boolean verifyVersion(String version) {
		if(version.length()==3 && version.substring(1,2).equals(".") && Character.isDigit(version.charAt(0)) && Character.isDigit(version.charAt(2))) {
			return true;
		}
		else
			return false;
	}

	public void run() {
		System.out.println("Running MC Reader...");
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
