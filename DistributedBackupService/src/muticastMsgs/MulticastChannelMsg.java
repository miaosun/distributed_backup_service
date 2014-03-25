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

	public MulticastChannelMsg(String adr, int port) throws IOException {
		super("MulticastChannel");
		this.adr = adr;
		this.port = port;
		msocket = new MulticastSocket(port);
		maddress = InetAddress.getByName(adr);
		msocket.joinGroup(maddress);
	}

	public void receivePacket() {
		try{
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data,data.length);
			msocket.receive(packet);
			String msg = new String(packet.getData());
			System.out.println("Message received: "+ msg);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public abstract void processMsg(String msg);

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

	public void run() {
		//receivePacket();
	}

}
