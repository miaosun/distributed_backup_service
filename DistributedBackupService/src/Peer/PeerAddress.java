package Peer;
import java.net.InetAddress;


public class PeerAddress {

	InetAddress address;
	int port;
	
	public PeerAddress(InetAddress address, int port) {
		this.address=address;
		this.port=port;
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj instanceof PeerAddress && ((this.address).equals( ((PeerAddress)obj).address)) && ((this.port) == ((PeerAddress)obj).port) )
			return true;
		else
			return false;
	}
}