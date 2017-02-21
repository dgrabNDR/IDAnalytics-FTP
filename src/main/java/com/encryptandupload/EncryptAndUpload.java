package main.java.com.encryptandupload;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
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
		parameters = (HashMap<String,String>) gson.fromJson(paramStr, params.getClass());
		parameters.putAll(CredentialManager.getLogin());
		this.params = parameters;
		System.out.println("Params ==>"+gson.toJson(params));	
		
		sc = new SalesforceConnector(params.get("Username"),params.get("Password"),params.get("environment"));
		ArrayList<SObject> attachments = new ArrayList<SObject>();		
		try {			
			sc.login();
			attachments = query(params.get("attId"));
			System.out.println("got attachments ==> "+attachments.size());	
		} catch (ConnectionException e1) {
			e1.printStackTrace();
		}
		
		ArrayList<SObject> encrypted = new ArrayList<SObject>();		
		// encrypt files

		System.out.println("attempting to encrypt attachment...");
		//EncryptFile.getAllFiles(new File("."));
		for(SObject so : attachments){			
			//EncryptFile.writeToFile((String)so.getField("Name"), (String)so.getField("Body"));
			try {
				byte[] suchEncrypt = EncryptFile.encrypt(base64ToByte((String)so.getField("Body")));
				System.out.println("suchEncrypt: "+suchEncrypt);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//EncryptFile.getFile((String)so.getField("Name"));			
		}

		// upload files to ftp
		/*
		UploadFile uf = new UploadFile();
		uf.start(params, attachments);
		uf.upload();
		*/
		for(SObject so : attachments){
			SObject newAtt = new SObject("Attachment");
			newAtt.setField("ParentId", params.get("id"));
			newAtt.setField("Docs_Uploaded__c", true);
		}
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