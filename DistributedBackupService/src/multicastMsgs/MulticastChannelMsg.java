package multicastMsgs;

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

	public MulticastChannelMsg(String adr, int port) throws IOException {
		super("MulticastChannel");
		this.adr = adr;
		this.port = port;
		msocket = new MulticastSocket(port);
		maddress = InetAddress.getByName(adr);
	}
	
	public void listen() throws IOException {
		msocket.joinGroup(maddress);
	}
	
	public void joinMulticastGroup() {
		try {
			msocket.joinGroup(maddress);
			msocket.setTimeToLive(1);
			msocket.setLoopbackMode(true);
		} catch (IOException e) {
			System.out.println("ERROR multicast joinGroup");
			e.printStackTrace();
		}
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
	
	public DatagramPacket getPacket() throws IOException {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data,data.length);
			msocket.receive(packet);
			return packet;
	}

	public void sendPacket(byte[] buffer) {

		try{
			DatagramSocket socket = new DatagramSocket();
			//byte[] buffer = new byte[msg.length()];
			//buffer = msg.getBytes();

			DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, maddress, port);
			socket.send(sendPacket);
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void processMsg(String msg) {
	}

	public void run() {
		System.out.println("at MCmsg run");
		
	}

}
