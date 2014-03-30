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
		
		byte[] message = new byte[packet.getLength()];
		System.arraycopy(packet.getData(), 0, message, 0, packet.getLength());
		String msg = new String(message);
		
		System.out.println("Message received: "+msg.substring(0,msg.length()-4));
		PeerAddress peer = new PeerAddress(packet.getAddress(),packet.getPort());
		System.out.println("by Peer: "+packet.getAddress()+"  "+packet.getPort());
		
		System.out.println("MCReader-> Process Message");
		String[] temp = msg.split(" ");
		String cmd = temp[0].trim();
		String fileID="";
		
		if(cmd.equals("STORED")) {
			if(verifyVersion(temp[1].trim())) {
				fileID = temp[2].trim();
				Chunk ch = new Chunk(fileID, Integer.parseInt(temp[3].trim()));
				Peer.addtoStoredsInfo(ch, peer);
			}
		}
		else if(cmd.equals("GETCHUNK")) {
			if(verifyVersion(temp[1].trim())) {
				fileID = temp[2].trim();
				ChunkRestore chRestore;
				try {
					chRestore = new ChunkRestore(fileID, Integer.parseInt(temp[3].trim()));
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
