package Peer;

public class Definitions {

	//MC Channel
		public static int MCPORT = 8765;
		public static String MCADDRESS = new String("239.0.0.1"); //any class D address
		
		//MDB Channel
		public static int MDBPORT = 8766;
		public static String MDBADDRESS = new String("239.0.0.1"); //any class D address
		
		//MDR Channel
		public static int MDRPORT = 8767;
		public static String MDRADDRESS = new String("239.0.0.1"); //any class D address
		
		public final static byte[] CRLFseq = {0xD, 0xA};
		public final static String CRLF = new String(CRLFseq);
		public static String version = "1.0";
		
		public final static String backupFilesDirectory = "backup_files/";
		
		public static int plusRepDegree = 1;

		public static void setPlusRepDegree(int plusRepDegree) {
			Definitions.plusRepDegree = plusRepDegree;
		}

		public static void setMCPORT(int mCPORT) {
			MCPORT = mCPORT;
		}

		public static void setMCADDRESS(String mCADDRESS) {
			MCADDRESS = mCADDRESS;
		}

		public static void setMDBPORT(int mDBPORT) {
			MDBPORT = mDBPORT;
		}

		public static void setMDBADDRESS(String mDBADDRESS) {
			MDBADDRESS = mDBADDRESS;
		}

		public static void setMDRPORT(int mDRPORT) {
			MDRPORT = mDRPORT;
		}

		public static void setMDRADDRESS(String mDRADDRESS) {
			MDRADDRESS = mDRADDRESS;
		}

		public static void setVersion(String version) {
			System.out.println("--Using Protocol version 2.0");
			Definitions.version = version;
		}
		
		
		
}
