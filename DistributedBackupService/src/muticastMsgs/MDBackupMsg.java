package muticastMsgs;

import java.io.IOException;

import Peer.Definitions;


//MDB Reader
public class MDBackupMsg extends MulticastChannelMsg {

	String body;
	String fileID="";
	boolean initiatorPeer;
	int replicationDegree=0;

	//MDB Reader
	public MDBackupMsg(String adr, int port) throws IOException {
		super(adr, port);
		initiatorPeer=false;
	}

	public MDBackupMsg(String adr, int port, String fileID, int repdegree) throws IOException {
		super(adr, port);
		initiatorPeer=true;
		this.fileID=fileID;
		replicationDegree=repdegree;
	}

	public void putchunkSend(int chunkNR, String body) {
		if(!initiatorPeer)
			System.out.println("ERROR: not permited!");
		else
		{
			String message = "PUTCHUNK"+" "+Definitions.version+" "+fileID+" "+chunkNR+" "+replicationDegree+" "+Definitions.CRLF+Definitions.CRLF+body;
			sendPacket(message);
		}
	}

	@Override
	public void processMsg(String msg) {
		System.out.println("Process Backup Message");
		String[] temp = msg.split(" ");
		String cmd = temp[0].trim();
		System.out.println("O commando: " + cmd);

		if(cmd.equals("PUTCHUNK")) {
			if(verifyVersion(temp[1].trim())) {
				System.out.println("PUTCHUNK RECEBIDO!");
				//TODO lançar thread p guardar chunk e responder stored p MC
			}
		}
		else
		{
			System.out.println("MESSAGE IGNORED");
		}

	}

	public Boolean verifyVersion(String version) {
		if(version.length()==3 && version.substring(1,2).equals('.') && Character.isDigit(version.charAt(0)) && Character.isDigit(version.charAt(2))) {
			return true;
		}
		else
			return false;
	}



	public void run() {
		if(!initiatorPeer)  //MDB Reader
		{
			joinMulticastGroup();
			while(true) {
				System.out.println("MDB thread waiting for putchunk messages");
				String msg = receivePacket();
				processMsg(msg);
			}
		}
	}
}
