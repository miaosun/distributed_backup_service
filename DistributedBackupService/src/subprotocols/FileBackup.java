package subprotocols;

public class FileBackup extends Thread {

	String filename;
	
	public FileBackup(String filename) {
		this.filename=filename;
	}
	
	public void run() {
		/*
		* TODO
		*
		* Split do ficheiro
		* ciclo enviar ficheiro chunk a chunk (através de MDbackupmsg)
		* esperar por respostas
		* verificar nr de respostas e informação
		*/
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
				//verificar se já se obteve nr desejado de respostas
				attempts--;
				waitTime*=2;
			}			

		}
 */

