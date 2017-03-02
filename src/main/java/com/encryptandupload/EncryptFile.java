package main.java.com.encryptandupload;

import java.io.*;

public class EncryptFile {
	private String passphrase;
    private String publicKeyFileName;
    private String secretKeyFileName;
    private String inputFileName;
    private String outputFileName;
    private boolean asciiArmored = false;
    private boolean integrityCheck = true;
    
	
    public byte[] encrypt(byte[] data) throws Exception{
    	//FileInputStream keyIn = new FileInputStream("/app/./src/main/java/com/encryptandupload/keys/pubring.pkr");
		FileInputStream keyIn = new FileInputStream("/app/./src/main/java/com/encryptandupload/keys/ID_Analytics_PGP_Public_Key.asc");
        FileOutputStream out = new FileOutputStream("/app/./src/main/java/com/encryptandupload/greatFile.pgp"); 
        System.out.println("Body Base64Decode byte[] ==> "+data);
        System.out.println("PublicKey ==> "+PGPUtils.readPublicKey(keyIn));
        byte[] encryptedData = PGPUtils.encryptFile((OutputStream)out, data, PGPUtils.readPublicKey(keyIn), true, integrityCheck);
        out.close();
        keyIn.close();
        return encryptedData;
    }
	
	public boolean isAsciiArmored() {
        return asciiArmored;
	}
	
	public void setAsciiArmored(boolean asciiArmored) {
	        this.asciiArmored = asciiArmored;
	}
	
	public boolean isIntegrityCheck() {
	        return integrityCheck;
	}
	
	public void setIntegrityCheck(boolean integrityCheck) {
	        this.integrityCheck = integrityCheck;
	}
	
	public String getPassphrase() {
	        return passphrase;
	}
	
	public void setPassphrase(String passphrase) {
	        this.passphrase = passphrase;
	}
	
	public String getPublicKeyFileName() {
	        return publicKeyFileName;
	}
	
	public void setPublicKeyFileName(String publicKeyFileName) {
	        this.publicKeyFileName = publicKeyFileName;
	}
	
	public String getSecretKeyFileName() {
	        return secretKeyFileName;
	}
	
	public void setSecretKeyFileName(String secretKeyFileName) {
	        this.secretKeyFileName = secretKeyFileName;
	}
	
	public String getInputFileName() {
	        return inputFileName;
	}
	
	public void setInputFileName(String inputFileName) {
	        this.inputFileName = inputFileName;
	}
	
	public String getOutputFileName() {
	        return outputFileName;
	}
	
	public void setOutputFileName(String outputFileName) {
	        this.outputFileName = outputFileName;
	}
	
}