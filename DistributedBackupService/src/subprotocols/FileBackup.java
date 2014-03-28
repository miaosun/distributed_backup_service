package subprotocols;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;

import multicastMsgs.MDBackupMsg;
import utilities.FileSplitter;
import utilities.SHA256;

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
		 * ciclo enviar ficheiro chunk a chunk (atraves de MDbackupmsg)
		 * esperar por respostas
		 * verificar nr de respostas e informacao
		 */


		try {
			FileSplitter.split(filename);
			String fileID = SHA256.hash256(filename);
			int numberChunkParts = FileSplitter.getNumberParts(filename);
			
			

			for(int i=0; i<numberChunkParts; i++)
			{
				//TODO envia chunk um a um, while.. 
				int replicationDeg = getReplicationDeg(numberChunkParts);
				MDBackupMsg bMsg = new MDBackupMsg("localhost", 1200, fileID, replicationDeg);
				
				
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private static int getReplicationDeg(int numberChunkParts) throws IOException {
		Boolean b = true;
		while(b)
		{
			System.out.println("How many replications? ");
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
			int replicationDeg = Integer.parseInt(inputStream.readLine());
			
			if(replicationDeg <= numberChunkParts && replicationDeg > 0)
				return replicationDeg;
			else
				System.out.println("Can't have this number of replications, try again!\n");
		}
		return 0;
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

