package muticastMsgs;

import java.io.IOException;

public class MControlReader extends MulticastChannelMsg {

	public MControlReader(String adr, int port) throws IOException {
		super(adr, port);

	}

	@Override
	public void processMsg(String msg) {

		System.out.println("MCReader-> Process Message");
		String[] temp = msg.split(" ");
		String cmd = temp[0].trim();
		System.out.println("O comando: " + cmd);

		if(cmd.equals("STORED")) {
			if(verifyVersion(temp[1].trim())) {
				//...
				//guarda estrutura dados
			}
		}
		else if(cmd.equals("GETCHUNK")){
			if(verifyVersion(temp[1].trim())) {
				//lan�a thread p restore
			}
		}
		else
		{
			System.out.println("MESSAGE IGNORED");
		}
	}

	public Boolean verifyVersion(String version) {
		if(version.length()==3 && version.substring(1,2).equals('.') && Character.isDigit(version.charAt(0)) && Character.isDigit(version.charAt(2))) {
			return true;
		}
		else
			return false;
	}

	public void run() {
		System.out.println("Running MC Reader");
		//ciclo leitura MC
		while(true) {
			String msg = receivePacket();
			processMsg(msg);
		}
	}

}
