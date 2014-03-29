package multicastMsgs;

import java.io.IOException;
import java.util.Random;

import Peer.Chunk;
import Peer.Definitions;


//MDB Reader
public class MDBackupMsg extends MulticastChannelMsg {

	String body;
	String fileID="";
	int replicationDegree=0;

	//MDB Reader constructor
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

	public void putchunkSend(int chunkNR, byte[] bodyInBytes) {
		if(!initiatorPeer)
			System.out.println("ERROR: not permited!");
		else
		{
			String stringHeader = "PUTCHUNK"+" "+Definitions.version+" "+fileID+" "+chunkNR+" "+replicationDegree;
			System.out.println("\"BackupChunk Message sent: "+stringHeader+"\"");
			System.out.println("BODY: "+bodyInBytes.length);
			stringHeader+=Definitions.CRLF+Definitions.CRLF;
			byte[] header = stringHeader.getBytes();
			byte[] message = new byte[header.length+bodyInBytes.length];
			System.arraycopy(header, 0, message, 0, header.length);
			System.arraycopy(bodyInBytes, 0, message, header.length, bodyInBytes.length);	
			sendPacket(message);
		}
	}

	@Override
	public void processMsg(String msg) {
		System.out.println("> Process Backup Message Received!");
		String[] temp = msg.split(" ");
		String cmd = temp[0].trim();
		Random random = new Random();
		
		String fileID = temp[2].trim();
		int chunkNR = Integer.parseInt(temp[3].trim());
		int replicationDeg = Integer.parseInt(temp[4].trim());

		byte[] body = temp[7].getBytes();
		
		if(cmd.equals("PUTCHUNK")) {
			if(verifyVersion(temp[1].trim())) {
				System.out.println("PEDIDO PUTCHUNK RECEBIDO!");
				Chunk ch = new Chunk(fileID, chunkNR, replicationDeg);
				
				//verificar se ainda n tem o ficheiro //TODO verificar
				if(!ch.exists())
				{
					//waits timeout time before sending STORED message
					int timeout = random.nextInt(401);
					try {
						Thread.sleep(timeout);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//enviar stored
					
					//guardar chunk
					try {
						ch.saveChunk(body);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} //TODO
				}
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
				System.out.println("MDB thread waiting for putchunk messages...");
				String msg = receivePacket();
				processMsg(msg);
			}
		}
	}
}
