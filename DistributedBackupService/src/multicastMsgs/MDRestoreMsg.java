package multicastMsgs;

import java.io.IOException;
import Peer.Chunk;
import Peer.Definitions;
import Peer.Peer;

public class MDRestoreMsg extends MulticastChannelMsg {

	public MDRestoreMsg(String adr, int port) throws IOException {
		super(adr, port);

	}

	@Override
	public void run() {

		joinMulticastGroup();
		while(true) {
			//System.out.println("MDR thread waiting for chunk messages...");
			byte[] data = new byte[65000];
			data = receivePacketByte();
			if(data!=null)
				processMsg(data);
		}

	}

	private void processMsg(byte[] msg) {

		int offset = 0;
		String header = "";
		for(int i=0; i<msg.length; i++)
		{
			if(msg[i] == Definitions.CRLFseq[0] && msg[i+1] == Definitions.CRLFseq[1] && msg[i+2] == Definitions.CRLFseq[0] && msg[i+3] == Definitions.CRLFseq[1])
			{
				offset = i+4;
				header = new String(msg, 0, i);
				break;
			}
		}

		String[] temp = header.split(" ");

		String cmd = temp[0].trim();

		if(cmd.equals("CHUNK")) {
			if(verifyVersion(temp[1].trim())) {

				String fileID = temp[2].trim();
				int chunkNR = Integer.parseInt(temp[3].trim());

				System.out.println("> CHUNK Received: " + header);

				byte[] body = new byte[msg.length-offset];
				System.arraycopy(msg, offset, body, 0, msg.length-offset);

				int desiredRepDeg = Peer.getDesiredRepDegByfileID(fileID);

				Chunk ch = new Chunk(fileID, chunkNR, desiredRepDeg);

				if(Peer.getWaitingChunk()!=null) {
					if( Peer.getWaitingChunk().equals(ch) ) {
						Peer.setReceived(true);
						//guardar chunk
						try {
							ch.saveChunk(body);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else
						Peer.wcIsSent(ch);
				}
			}
		}
		else
		{
			System.out.println("MESSAGE IGNORED");
		}
	}

}
