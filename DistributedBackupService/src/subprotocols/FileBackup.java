package subprotocols;

import java.io.IOException;

import Peer.Definitions;
import muticastMsgs.MDBackupMsg;

public class FileBackup extends Thread {

	String filename;

	public FileBackup(String filename) {
		this.filename=filename;
	}

	public void run() {
		/*
		 * TODO
		 * Split do ficheiro
		 * ciclo enviar ficheiro chunk a chunk (atrav�s de MDbackupmsg)
		 * esperar por respostas
		 * verificar nr de respostas e informa��o
		 */

		try {
			int n=5;
			while(n>0){
				MDBackupMsg putchunk = new MDBackupMsg(Definitions.MDBADDRESS, Definitions.MDBPORT, "file", 1);
				putchunk.putchunkSend(n, "ola");
				n--;
				Thread.sleep(3000);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}



/*
 * 
			String msg = putchunkFormer();

			long waitTime = 500;
			int attempts = 5;

			while(attempts>0){ //e nao atingido nr desejado de stored's
				System.out.println("Sending chunk...");
				sendPacket(msg);

				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//verificar se j� se obteve nr desejado de respostas
				attempts--;
				waitTime*=2;
			}			

		}
 */

