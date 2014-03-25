package muticastMsgs;

import java.io.IOException;

public class MDBackupMsg extends MulticastChannelMsg {

	String body;
	
	public MDBackupMsg(String adr, int port) throws IOException {
		super(adr, port);
		initiatorPeer=false;
	}

	public MDBackupMsg(String adr, int port, String fileID, int chunkNR, int repDegree, String body) throws IOException {
		super(adr, port);
		initiatorPeer=true;
		this.fileID=fileID;
		this.chunkNR=chunkNR;
		this.replicationDegree=repDegree;
		this.body=body;
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


	private void putchunkSend() {

	}

	private void mdbReader() {
		while(true) {
			String msg = receivePacket();
			//tratar msg
		}
	}
	
	//forma putchunk message
	private String putchunkFormer() {
		String putchunkMsg = msgHeader()+" "+body;
		return putchunkMsg;
	}


	public void run() {
		if(initiatorPeer) {
			
			String msg = putchunkFormer();

			long waitTime = 500;
			int attempts = 5;

			while(attempts>0){ //e nao atingido nr desejado de stored's
				System.out.println("Sending chunk...");
				sendPacket(msg);

				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//verificar se j� se obteve nr desejado de respostas
				attempts--;
				waitTime*=2;
			}			

		}
		else //MDB Reader
		{
			mdbReader();
		}
	}
}
