package multicastMsgs;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;

import Peer.Chunk;
import Peer.Definitions;
import Peer.Peer;
import Peer.PeerAddress;

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
			stringHeader+=Definitions.CRLF+Definitions.CRLF;
			byte[] header = stringHeader.getBytes();
			byte[] message = new byte[header.length+bodyInBytes.length];
			System.arraycopy(header, 0, message, 0, header.length);
			System.arraycopy(bodyInBytes, 0, message, header.length, bodyInBytes.length);	
			sendPacket(message);
		}
	}

	public void run() {
		if(!initiatorPeer)  //MDB Reader
		{
			joinMulticastGroup();
			while(true) {
				//System.out.println("MDB thread waiting for putchunk messages...");
				//String msg = receivePacket();

				processMsg(receivePacketByte());
			}
		}
	}

	private void processMsg(byte[] msg) {
		// TODO Auto-generated method stub
		int offset = 0;
		String header = "";
		System.out.println("MSG size: "+msg.length);
		for(int i=0; i<msg.length; i++)
		{
			if(msg[i] == Definitions.CRLFseq[0] && msg[i+1] == Definitions.CRLFseq[1] && msg[i+2] == Definitions.CRLFseq[0] && msg[i+3] == Definitions.CRLFseq[1])
			{
				offset = i+4;
				header = new String(msg, 0, i);
				break;
			}
		}

		System.out.println("> Putchunk Received: " + header);

		byte[] body = new byte[msg.length-offset];
		System.arraycopy(msg, offset, body, 0, msg.length-offset);

		Random random = new Random();
		String[] temp = header.split(" ");
		String cmd = temp[0].trim();
		String fileID = temp[2].trim();
		int chunkNR = Integer.parseInt(temp[3].trim());
		int replicationDeg = Integer.parseInt(temp[4].substring(0,1));

		if(cmd.equals("PUTCHUNK")) {
			if(verifyVersion(temp[1].trim())) {
				Chunk ch = new Chunk(fileID, chunkNR, replicationDeg);

				//waits timeout time before sending STORED message
				int timeout = random.nextInt(401);
				try {
					Thread.sleep(timeout);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//verificar se ainda n tem o ficheiro //TODO verificar
				if(!ch.exists())
				{
					//guardar chunk
					try {
						ch.saveChunk(body);
						Peer.addtoStoredsInfo(ch, new PeerAddress(InetAddress.getLocalHost(), 0));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
					System.out.println("Chunk already backed up");
				
				//enviar stored
				String storedMsg = "STORED" + header.substring(header.indexOf(' ')) + Definitions.CRLF + Definitions.CRLF;
				try {
					MControlReader MC = new MControlReader(Definitions.MCADDRESS, Definitions.MCPORT);
					MC.sendMessages(storedMsg);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}
		else
		{
			System.out.println("MESSAGE IGNORED");
		}
	}
}
