package multicastMsgs;

import java.io.IOException;
import java.net.DatagramPacket;

import Peer.Peer;
import Peer.PeerAddress;
import Peer.StoredtypeMessage;

public class MControlReader extends MulticastChannelMsg {

	private String msg;
	
	public MControlReader(String adr, int port) throws IOException {
		super(adr, port); //TODO change to Definitions. ... ? (& same other mchannels)
	}
	
	public void getMessege(String msg) {
		this.msg = msg;
	}

	public void processPacket(DatagramPacket packet) {
		
		//String msg = new String(packet.getData());
		this.msg = new String(packet.getData());
		System.out.println("Message received: "+ msg);
		PeerAddress peer = new PeerAddress(packet.getAddress(),packet.getPort());
		
		System.out.println("MCReader-> Process Message");
		String[] temp = msg.split(" ");
		String cmd = temp[0].trim();

		if(cmd.equals("STORED")) {
			if(verifyVersion(temp[1].trim())) {
				StoredtypeMessage storedmsg = new StoredtypeMessage(temp[2].trim(), Integer.parseInt(temp[3].trim()), peer);
				Peer.addStoredMessage(storedmsg);
			}
		}
		else if(cmd.equals("GETCHUNK")) {
			if(verifyVersion(temp[1].trim())) {
				//lanca thread p restore
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
		if(version.length()==3 && version.substring(1,2).equals('.') && Character.isDigit(version.charAt(0)) && Character.isDigit(version.charAt(2))) {
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
