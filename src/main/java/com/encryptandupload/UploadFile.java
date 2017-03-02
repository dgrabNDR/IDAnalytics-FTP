package main.java.com.encryptandupload;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import main.java.com.salesforce.SalesforceConnector;

import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

public class UploadFile {
	public Map<String,String> params = new HashMap<String,String>();
	public ArrayList<File> lstAtt;

	
	public void start(Map<String,String> p, ArrayList<File> files){
		System.out.println("setting up uploader...");
		params = p;
		lstAtt = files;
	}
	
	public void upload(){
		System.out.println("attempting upload...");
		try{
			GenericFTPClient sftp = new GenericFTPClient();
			System.out.println(sftp);
			sftp.connect(params.get("ftphost"), params.get("ftpuser"), params.get("ftppass"), 22);
			System.out.println("Change folder status:"+sftp.changeDir(params.get("ftpfolder")));

			File att = lstAtt.get(0);
			byte[] fileBytes = readFile(att);
		      
			InputStream is1 = new ByteArrayInputStream(fileBytes);
			Boolean res = sftp.uploadFile(is1, (String) att.getName());

			if(res){
				System.out.println("upload successful");
			}
			System.out.println(res);

			sftp.logout();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public byte[] readFile(File file) throws IOException{
		InputStream is = new FileInputStream(file);
		long length = file.length();
		byte[] bytes = new byte[(int)length];
		int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
        return bytes;
	}
	
	public byte[] base64ToByte(String data) throws Exception {
		return Base64.decodeBase64(data.getBytes());
	}
}
