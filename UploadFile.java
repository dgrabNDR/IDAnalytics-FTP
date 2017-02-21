package main.java.com.encryptandupload;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import main.java.com.salesforce.SalesforceConnector;

import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

public class UploadFile {
	public Map<String,String> params = new HashMap<String,String>();
	public ArrayList<SObject> lstAtt;

	
	public void start(Map<String,String> p, ArrayList<SObject> files){
		System.out.println("setting up uploader...");
		params = p;
		lstAtt = files;
	}
	
	public void upload(){
		System.out.println("attempting upload...");
		try{
			InetAddress ip = InetAddress.getLocalHost();
			System.out.println("My current IP address : " + ip);
			
			
			GenericFTPClient sftp = new GenericFTPClient();
			System.out.println(sftp);
			sftp.connect(params.get("ftphost"), params.get("ftpuser"), params.get("ftppass"), 22);
			System.out.println("Change folder status:"+sftp.changeDir(params.get("ftpfolder")));

			SObject att1 = lstAtt.get(0);
			InputStream is1 = new ByteArrayInputStream(base64ToByte((String)att1.getField("Body")));
			Boolean res = sftp.uploadFile(is1, (String) att1.getField("Name"));

			if(res){
				System.out.println("upload successful");
				// add pgp attachment to sf record?
			}
			else{
				System.out.println("Error Uploading : "+att1.getField("Id"));
			}
			System.out.println(res);

			sftp.logout();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public byte[] base64ToByte(String data) throws Exception {
		return Base64.decodeBase64(data.getBytes());
	}
}
