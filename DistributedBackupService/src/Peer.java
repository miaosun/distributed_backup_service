import java.io.IOException;

import mchannels.MControl;


public class Peer {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		MControl mc = new MControl("230.0.0.1",1200);
		mc.start();
	}

}
