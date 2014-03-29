package Peer;

public class Definitions {

	//MC Channel
		public final static int MCPORT = 8765;
		public final static String MCADDRESS = new String("239.0.0.1"); //any class D address
		
		//MDB Channel
		public final static int MDBPORT = 8766;
		public final static String MDBADDRESS = new String("239.0.0.1"); //any class D address
		
		//MDR Channel
		public final static int MDRPORT = 8767;
		public final static String MDRADDRESS = new String("239.0.0.1"); //any class D address
		
		public final static byte[] CRLFseq = {0xD, 0xA};
		public final static String CRLF = new String(CRLFseq);
		public final static String version = "1.0";
		
		public final static String backupFilesDirectory = "backup_files/";
}
