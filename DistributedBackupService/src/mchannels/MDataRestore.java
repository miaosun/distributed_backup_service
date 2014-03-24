package mchannels;

import java.io.IOException;

public class MDataRestore extends MulticastChannel {

	public MDataRestore(String adr, int port) throws IOException {
		super(adr, port);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void processMsg(String msg) {
		// TODO Auto-generated method stub
		System.out.println("Process Restore Message");
		String[] temp = msg.split(" ");
		String cmd = temp[0].trim();
		System.out.println("O commando: " + cmd);
		
		if(cmd.equals("CHUNK")) {
			if(verifyVersion(temp[1].trim())) {

			}
		}
	}
	
	public Boolean verifyVersion(String version) {
		if(version.length()==3 && version.substring(1,2).equals('.') && Character.isDigit(version.charAt(0)) && Character.isDigit(version.charAt(2))) {
			return true;
		}
		else
			return false;
	}

}
