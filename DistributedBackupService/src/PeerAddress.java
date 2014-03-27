import java.net.InetAddress;


public class PeerAddress {

	InetAddress address;
	int port;
	
	public PeerAddress(InetAddress address, int port) {
		this.address=address;
		this.port=port;
	}
}
