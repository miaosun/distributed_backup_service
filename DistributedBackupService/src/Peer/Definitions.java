package Peer;

public class Definitions {

	//MC Channel
		public final static int MCPORT = 1201;
		public final static String MCADDRESS = new String("230.0.0.1"); //any class D address
		
		//MDB Channel
		public final static int MDBPORT = 1200;
		public final static String MDBADDRESS = new String("230.0.0.2"); //any class D address
		
		//MDR Channel
		public final static int MDRPORT = 1202;
		public final static String MDRADDRESS = new String("230.0.0.3"); //any class D address
		
		public final static byte[] CRLFseq = {0xD, 0xA};
		public final static String CRLF = new String(CRLFseq);
		public final static String version = "1.0";
}
