package main.java.com.encryptandupload;

import java.io.ByteArrayInputStream;
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
	public ArrayList<SObject> lstAtt;
	private SalesforceConnector sc;
	
	public static void start(Map<String,String> p, ArrayList<SObject> files){
		UploadFile u = new UploadFile();
		u.params = p;
		u.lstAtt = files;
	}
	
	private void upload(){
		ArrayList<SObject> dsToUpdate = new ArrayList<SObject>();
		try{
			GenericFTPClient sftp = new GenericFTPClient();
			sftp.connect(params.get("ftphost"), params.get("ftpuser"), params.get("ftppass"), 22);
			System.out.println("Change folder status:"+sftp.changeDir(params.get("ftpfolder")));
			sc = new SalesforceConnector(params.get("Username"),params.get("Password"),params.get("environment"));
			sc.login();

			SObject att1 = lstAtt.get(0);
			InputStream is1 = new ByteArrayInputStream(base64ToByte((String)att1.getField("Body")));
			Boolean res = sftp.uploadFile(is1, (String) att1.getField("Name"));

			if(res){
				SObject dsU = new SObject("GCS_Account__c");
				dsU.setField("id", params.get("id"));
				dsU.setField("Docs_Uploaded__c", true);
				dsToUpdate.add(dsU);
			}
			else{
				System.out.println("GCS: Error Uploading : "+att1.getField("Id"));
			}
			System.out.println(res);

			sftp.logout();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally{
			try {
				sc.update(dsToUpdate);
				System.out.println(dsToUpdate);
			} catch (ConnectionException e) {
				e.printStackTrace();
			}
		}
	}
	
	public byte[] base64ToByte(String data) throws Exception {
		return Base64.decodeBase64(data.getBytes());
	}
}
