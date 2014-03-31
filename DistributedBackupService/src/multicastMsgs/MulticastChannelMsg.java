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
	boolean initiatorPeer;

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
	
	public byte[] receivePacketByte() {
		try{
			byte[] data = new byte[64085];
			DatagramPacket packet = new DatagramPacket(data,data.length);
			msocket.receive(packet);
			byte[] receiveD = new byte[packet.getLength()];
			System.arraycopy(packet.getData(), 0, receiveD, 0, packet.getLength());
			
			return receiveD;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
			DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, maddress, port);
			socket.send(sendPacket);
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public Boolean verifyVersion(String version) {
		if(version.length()==3 && version.substring(1,2).equals(".") && Character.isDigit(version.charAt(0)) && Character.isDigit(version.charAt(2))) {
			return true;
		}
		else
			return false;
	}

	public void run() {
		System.out.println("at MCmsg run");
		
	}

}
