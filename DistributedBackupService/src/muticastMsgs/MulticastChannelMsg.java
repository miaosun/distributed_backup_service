package muticastMsgs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public abstract class MulticastChannelMsg extends Thread {

	public int port;
	public String adr;
	MulticastSocket msocket;
	InetAddress maddress;
	
	boolean initiatorPeer;
	String fileID;
	int chunkNR;
	int replicationDegree;
	//String body;

	public MulticastChannelMsg(String adr, int port) throws IOException {
		super("MulticastChannel");
		this.adr = adr;
		this.port = port;
		msocket = new MulticastSocket(port);
		maddress = InetAddress.getByName(adr);
		msocket.joinGroup(maddress);
	}

	public String receivePacket() {
		try{
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data,data.length);
			msocket.receive(packet);
			String msg = new String(packet.getData());
			System.out.println("Message received: "+ msg);
			return msg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public void sendPacket(String msg) {

		try{
			DatagramSocket socket = new DatagramSocket();

			byte[] buffer = new byte[msg.length()];
			buffer = msg.getBytes();

			DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, maddress, port);
			socket.send(sendPacket);
			socket.close();
			System.out.println("Message sent.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected String msgHeader() {
		byte[] CRLFseq = {0xD, 0xA};
		String CRLF = new String(CRLFseq);
		String header = "PUTCHUNK 1.0 "+fileID+" "+chunkNR+" "+replicationDegree+" "+CRLF;
		return header;
	}
	
	public abstract void processMsg(String msg);

	public abstract void run();

}
