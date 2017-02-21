package main.java.com.encryptandupload;

import java.io.*;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Iterator;

import org.bouncycastle.*;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.PGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.PGPKeyEncryptionMethodGenerator;
import org.apache.commons.io.FileUtils;

public class EncryptFile {
	
	public static void writeToFile(String fileName, String body) throws IOException{
		File file = new File("files/"+fileName);
		FileOutputStream fop = new FileOutputStream(file);
		fop.write(body.getBytes());
		fop.close();
	}
	
	public static File getFile(String fileName) throws IOException{
		File file = new File("files/"+fileName);
		if(file.exists()){
			return file;
		}
		return null;
	}
	
	public static void clearDir() throws IOException{
		FileUtils.cleanDirectory(new File("/files/"));
	}
	
	public static void getAllFiles(File curDir) {		
        File[] filesList = curDir.listFiles();
        for(File f : filesList){
            //if(f.isDirectory())
            //	System.out.println(f.getName()+"/");
            //	EncryptFile.getAllFiles(f);
            if(f.isFile()){
                System.out.println(f.getName());
            }
        }
    }
	
	public static byte[] encrypt(byte[] data) {
          try{
              // ----- Read in the public key
              PGPPublicKey key = readPublicKeyFromCol(new FileInputStream(new File("keys/ID_Analytics_PGP_Public_Key.asc")));
              System.out.println("Creating a temp file...");
              // create a file and write the string to it
              File tempfile = File.createTempFile("pgp", null);
              FileOutputStream fos = new FileOutputStream(tempfile);
              fos.write(data);
              fos.close();
              System.out.println("Temp file created at ");
              System.out.println(tempfile.getAbsolutePath());
              System.out.println("Reading the temp file to make sure that the bits were written\n--------------");
              BufferedReader isr = new BufferedReader(new FileReader(tempfile));
              
              int count = 0;
              for ( java.util.Iterator iterator = key.getUserIDs(); iterator.hasNext(); )
              {
                      count++;
                      System.out.println((String) iterator.next());
              }
              System.out.println("Key Count = " + count);
              // create an armored ascii file
              // FileOutputStream out = new FileOutputStream(outputfile);
              // encrypt the file
              // encryptFile(tempfile.getAbsolutePath(), out, key);
              // Encrypt the data
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              //_encrypt(tempfile.getAbsolutePath(), baos, key);
              _encrypt(tempfile.getAbsolutePath(), baos, data, key);
              System.out.println("encrypted text length=" + baos.size());			
              tempfile.delete();
              return baos.toByteArray();
          } catch (PGPException e) {
              // System.out.println(e.toString());
              System.out.println(e.getUnderlyingException().toString());
              e.printStackTrace();
          } catch (Exception e) {
              e.printStackTrace();
          }
          return null;
      }
      

	private static void _encrypt(String fileName, ByteArrayOutputStream bOut, byte[] out, PGPPublicKey encKey) throws IOException, NoSuchProviderException, PGPException {
	      //out = new DataOutputStream(out);
		  //
	      OutputStream outStream = null;
	      //System.out.println("creating comData...");
	      // get the data from the original file
	      //PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(PGPCompressedDataGenerator.ZIP);
	      PGPDataEncryptorBuilder deb = null;
	      PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(deb);
	      //PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(PGPEncryptedDataGenerator.CAST5,  new SecureRandom(), "BC");
	      PGPKeyEncryptionMethodGenerator kemg = null;
	      kemg.generate(PGPEncryptedDataGenerator.CAST5, out);
	      cPk.addMethod(kemg);
	      
	      PGPUtil.writeFileToLiteralData(cPk.open(outStream, out.length), PGPLiteralData.BINARY, new File(fileName));
	      //comData.close();
	      //System.out.println("comData created...");
	      //System.out.println("using PGPEncryptedDataGenerator...");
	      // object that encrypts the data
		  
	      System.out.println("used PGPEncryptedDataGenerator...");
	      // take the outputstream of the original file and turn it into a byte
	      // array
	      bOut = (ByteArrayOutputStream) outStream;
	      byte[] bytes = bOut.toByteArray();
	      System.out.println("wrote bOut to byte array...");
	      // write the plain text bytes to the armored outputstream
	      Long l = (long) bytes.length;
	      OutputStream cOut = cPk.open(bOut, l);
	      cOut.write(bytes);
	      cPk.close();
	      outStream.close();
     }
    
     private static PGPPublicKey readPublicKeyFromCol(InputStream in) throws Exception {
    	 PGPUtil.getDecoderStream(in);
    	 PGPPublicKeyRing pkRing = null;
	     PGPPublicKeyRingCollection pkCol = new PGPPublicKeyRingCollection(in);
	     System.out.println("key ring size=" + pkCol.size());
	     Iterator it = pkCol.getKeyRings();
	     while (it.hasNext()) {
	             pkRing = (PGPPublicKeyRing) it.next();
	             Iterator pkIt = pkRing.getPublicKeys();
	             while (pkIt.hasNext()) {
	                     PGPPublicKey key = (PGPPublicKey) pkIt.next();
	                     System.out.println("Encryption key = " + key.isEncryptionKey() + ", Master key = " + 
	                                        key.isMasterKey());
	                     if (key.isEncryptionKey())
	                             return key;
	             }
	     }
	     return null;
	}
	
}
