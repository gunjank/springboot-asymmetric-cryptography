package gunjan.utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.xml.bind.DatatypeConverter;

import org.springframework.stereotype.Component;


public class GenerateKeys {

	private KeyPairGenerator keyGen;
	private KeyPair pair;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	static final String PUBLICKEY_PREFIX    = "-----BEGIN PUBLIC KEY-----";
	static final String PUBLICKEY_POSTFIX   = "-----END PUBLIC KEY-----";
	static final String PRIVATEKEY_PREFIX   = "-----BEGIN RSA PRIVATE KEY-----";
	static final String PRIVATEKEY_POSTFIX  = "-----END RSA PRIVATE KEY-----";
	
	
	

	public GenerateKeys(int keylength) throws NoSuchAlgorithmException, NoSuchProviderException {
		this.keyGen = KeyPairGenerator.getInstance("RSA");
		this.keyGen.initialize(keylength);
	}

	public void createKeys() {
		this.pair = this.keyGen.generateKeyPair();
		this.privateKey = pair.getPrivate();
		this.publicKey = pair.getPublic();
	}

	public PrivateKey getPrivateKey() {
		return this.privateKey;
	}

	public PublicKey getPublicKey() {
		return this.publicKey;
	}

	public void writeToFile(String path, String key) throws IOException {
		File f = new File(path);
		f.getParentFile().mkdirs();
		FileWriter fw = new FileWriter(f);
		fw.write(key);
		fw.close();

	}
	public void writeToFile(String path, byte[] key) throws IOException {
		File f = new File(path);
		f.getParentFile().mkdirs();

		FileOutputStream fos = new FileOutputStream(f);
		fos.write(key);
		fos.flush();
		fos.close();
	}

	public static String keyGenerateAndReturnPublicKey() {
		GenerateKeys gk;
		String publicKeyPEM = null;
		String privateKeyPEM = null;
		System.out.println("main method of generator");
		try {
			gk = new GenerateKeys(512);
			gk.createKeys();


			// THIS IS PEM:
	        publicKeyPEM = PUBLICKEY_PREFIX + "\n" + DatatypeConverter.printBase64Binary(gk.getPublicKey().getEncoded()).replaceAll("(.{64})", "$1\n") + "\n" + PUBLICKEY_POSTFIX;
	        privateKeyPEM = PRIVATEKEY_PREFIX + "\n" + DatatypeConverter.printBase64Binary(gk.getPrivateKey().getEncoded()).replaceAll("(.{64})", "$1\n") + "\n" + PRIVATEKEY_POSTFIX;

	        
	        
	        gk.writeToFile("KeyPair/publicKey", publicKeyPEM);
			gk.writeToFile("KeyPair/privateKey", gk.getPrivateKey().getEncoded());

			
			
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return publicKeyPEM+"::::::::"+privateKeyPEM;

	}

}
