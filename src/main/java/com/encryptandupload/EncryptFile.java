package main.java.com.encryptandupload;
/*
 * Copyright 2008 DidiSoft Ltd. All Rights Reserved.
 */
import java.io.IOException;
import java.io.*;

//import com.didisoft.pgp.*;
import org.bouncycastle.openpgp.*;
import org.apache.commons.io.FileUtils;

public class EncryptFile {
	public static void encrypt(String fileName) throws /*PGPException,*/ IOException{
		/*
		// create an instance of the library
		PGPLib pgp = new PGPLib();
		
		// if true the output file will be in ASCII armored format, 
		// otherwise will be in binary format
        boolean asciiArmor = true;
        // if true additional integrity check information is added
        // set to false for compatibility with older versions of PGP such as 6.5.8.
        boolean withIntegrityCheck = false;
                
        try {
			pgp.encryptFile("/files/"+fileName, 
							"/keys/ID_Analytics_PGP_Public_Key.asc", 
							"/files/"+fileName+".pgp", 
							asciiArmor, 
							withIntegrityCheck);
		} catch (com.didisoft.pgp.PGPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
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
	
	/*
	public static byte[] encrypt(byte[] data) {
              try
              {
                      // ----- Read in the public key
                      PGPPublicKey key = readPublicKeyFromCol(new FileInputStream(publicKeyFile));
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
                      _encrypt(tempfile.getAbsolutePath(), baos, key);
                      System.out.println("encrypted text length=" + baos.size());			
                      tempfile.delete();
                      return baos.toByteArray();
              }
              catch (PGPException e)
              {
                      // System.out.println(e.toString());
                      System.out.println(e.getUnderlyingException().toString());
                      e.printStackTrace();
              }
              catch (Exception e)
              {
                      e.printStackTrace();
              }
              return null;
	      
      }
      
      private static void _encrypt(String fileName, OutputStream out, PGPPublicKey encKey) throws IOException, NoSuchProviderException, PGPException {
              out = new DataOutputStream(out);
              ByteArrayOutputStream bOut = new ByteArrayOutputStream();
              System.out.println("creating comData...");
              // get the data from the original file
              PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(PGPCompressedDataGenerator.ZIP);
              PGPUtil.writeFileToLiteralData(comData.open(bOut), PGPLiteralData.BINARY, new File(fileName));
              comData.close();
              System.out.println("comData created...");
              System.out.println("using PGPEncryptedDataGenerator...");
              // object that encrypts the data
              PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(PGPEncryptedDataGenerator.CAST5, 
                                              new SecureRandom(), "BC");
              cPk.addMethod(encKey);
              System.out.println("used PGPEncryptedDataGenerator...");
              // take the outputstream of the original file and turn it into a byte
              // array
              byte[] bytes = bOut.toByteArray();
              System.out.println("wrote bOut to byte array...");
              // write the plain text bytes to the armored outputstream
              OutputStream cOut = cPk.open(out, bytes.length);
              cOut.write(bytes);
              cPk.close();
              out.close();
      }
	*/
}
