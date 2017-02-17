package main.java.com.encryptandupload;
/*
 * Copyright 2008 DidiSoft Ltd. All Rights Reserved.
 */
import java.io.IOException;
import java.io.*;

import com.didisoft.pgp.*;
import org.bouncycastle.openpgp.PGPException;
import org.apache.commons.io.FileUtils;

public class EncryptFile {
	public static void encrypt(String fileName) throws PGPException, IOException{
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
}
