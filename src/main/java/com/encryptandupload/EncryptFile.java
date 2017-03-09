package main.java.com.encryptandupload;

import java.io.*;

import org.bouncycastle.openpgp.PGPPublicKey;

public class EncryptFile {
	private String passphrase;
    private String publicKeyFileName;
    private String secretKeyFileName;
    private String inputFileName;
    private String outputFileName;
    private boolean asciiArmored = false;
    private boolean integrityCheck = true;
    
	
    public File encrypt(byte[] data) throws Exception{
		//FileInputStream keyIn = new FileInputStream("/app/./src/main/java/com/encryptandupload/keys/ID_Analytics_PGP_Public_Key.asc");
		FileInputStream keyIn = new FileInputStream("/app/./src/main/java/com/encryptandupload/keys/DanTest.asc");
		File f = new File("/app/./src/main/java/com/encryptandupload/myTestFile.pgp");
        FileOutputStream out = new FileOutputStream(f); 
        //System.out.println("Body Base64Decode byte[] ==> "+data);
        PGPPublicKey pubKey = PGPUtils.readPublicKey(keyIn);
        //System.out.println("Public Key[] ==> "+pubKey);
        PGPUtils.encryptFile((OutputStream)out, data, pubKey, true, integrityCheck);
        out.close();
        keyIn.close();
        return f;
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