package main.java.com.encryptandupload;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.java.com.salesforce.*;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.*;

import com.google.gson.Gson;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

public class EncryptAndUpload extends HttpServlet{
	private SalesforceConnector sc;
	Map<String,String> params = new HashMap<String,String>();
	
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Gson gson = new Gson();
		Map<String,String> parameters = new HashMap<String,String>();		
		String paramStr = getBody(req);
		System.out.println("Incoming Request => "+paramStr);
		
		// get params from request body
		parameters = (HashMap<String,String>) gson.fromJson(paramStr, params.getClass());
		parameters.putAll(CredentialManager.getLogin());
		this.params = parameters;
		System.out.println("Params ==>"+gson.toJson(params));	
		
		// login to salesforce and pull attachment
		sc = new SalesforceConnector(params.get("Username"),params.get("Password"),params.get("environment"));
		ArrayList<SObject> attachments = new ArrayList<SObject>();		
		try {			
			sc.login();
			attachments = query(params.get("attId"));
			System.out.println("got attachments ==> "+attachments.size());	
		} catch (ConnectionException e1) {
			e1.printStackTrace();
		}
		
		ArrayList<SObject> encryptedSObjs = new ArrayList<SObject>();
		ArrayList<File> encryptedFiles = new ArrayList<File>();
		
		// encrypt file
		System.out.println("encrypting attachment...");
		for(SObject so : attachments){			
			//EncryptFile.writeToFile((String)so.getField("Name"), (String)so.getField("Body"));
			try {
				EncryptFile ef = new EncryptFile();
				byte[] suchEncrypt = ef.encrypt(base64ToByte((String)so.getField("Body")));							
				encryptedSObjs.add(fileToSObj((String)so.getField("ParentId"),(String)so.getField("Name")+".pgp",suchEncrypt));
				encryptedFiles.add(newFile("/app/./src/main/java/com/encryptandupload/testFile.pgp",suchEncrypt));
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
		
		// add attachment to report in salesforce
		try {
			sc.create(encryptedSObjs);
		} catch (ConnectionException e) {
			e.printStackTrace();
		}

		// upload files to ida ftp		
		System.out.println(InetAddress.getLocalHost());
		UploadFile uf = new UploadFile();
		uf.start(params, encryptedFiles);
		//uf.upload();
	}
	
	private SObject fileToSObj(String pId, String fileName, byte[] body){
		SObject sObj = new SObject("Attachment");
		sObj.setField("ParentId", pId);
		sObj.setField("Name", fileName);
		sObj.setField("Body", body);
		return sObj;
	}
	
	private File newFile(String fileName, byte[] body) throws FileNotFoundException, IOException{
		File theFile = new File(fileName);
		if(theFile.createNewFile()) {
        	FileOutputStream fos = new FileOutputStream(theFile);
        	fos.write(body);
        	fos.close();
        }
		return theFile;
	}
	
	private String getBody(HttpServletRequest req) throws IOException{
		BufferedReader br = req.getReader();
		StringBuilder sb = new StringBuilder();  
		String str;
	    while( (str = br.readLine()) != null ){
	        sb.append(str);
		 } 
	    return sb.toString().isEmpty() ? "{}" : sb.toString();
	}
	
	private ArrayList<SObject> query(String ids) throws ConnectionException{
		String idFilter;
		if(ids.contains(",")){
			String[] idParts = ids.split(",");
			idFilter = " Id IN(";
			for(String attId : idParts){
				if(idFilter == " Id IN("){
					idFilter = idFilter + "'" + attId + "'";
				}else {
					idFilter = idFilter + ",'" + attId + "'";
				}
			}
			idFilter = idFilter + ")";
		} else {
			idFilter = " Id = '"+ids+"'";
		}
		System.out.println("idFilter: "+ idFilter);

		String soql = "SELECT Id, Name, ParentId, Body FROM Attachment WHERE "+idFilter;
		return sc.query(soql);
	}
	
	public byte[] base64ToByte(String data) throws Exception {
		return Base64.decodeBase64(data.getBytes());
	}
}