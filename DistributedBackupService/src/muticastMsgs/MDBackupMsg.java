package muticastMsgs;

import java.io.IOException;


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

	private void putchunkSend(int chunkNR, String body) {
		if(!initiatorPeer)
			System.out.println("ERROR: not permited!");
		else
		{
			String message = "PUTCHUNK"+" "+version+" "+fileID+" "+chunkNR+" "+replicationDegree+" "+CRLF+body;
			sendPacket(message);
		}
	}

	@Override
	public void processMsg(String msg) {
		// TODO Auto-generated method stub

		System.out.println("Process Backup Message");
		String[] temp = msg.split(" ");
		String cmd = temp[0].trim();
		System.out.println("O commando: " + cmd);

		if(cmd.equals("PUTCHUNK")) {
			if(verifyVersion(temp[1].trim())) {
				//lan�ar thread p guardar chunk e responder stored p MC
			}
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
			String msg = receivePacket();
			processMsg(msg);
		}
	}
}
