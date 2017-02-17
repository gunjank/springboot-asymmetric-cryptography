package gunjan.utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.xml.bind.DatatypeConverter;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import gunjan.dao.JDBCInMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GenerateKeys {

	@Autowired
	private JDBCInMemory jdbcInMemory;

	private KeyPairGenerator keyGen;
	private KeyPair pair;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	static final String PUBLICKEY_PREFIX    = "-----BEGIN PUBLIC KEY-----";
	static final String PUBLICKEY_POSTFIX   = "-----END PUBLIC KEY-----";
	static final String PRIVATEKEY_PREFIX   = "-----BEGIN RSA PRIVATE KEY-----";
	static final String PRIVATEKEY_POSTFIX  = "-----END RSA PRIVATE KEY-----";
	
	
	public GenerateKeys(){};
	public void generateSecureKeys(int keylength) throws NoSuchAlgorithmException, NoSuchProviderException {
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
//
//	public void writeToFile(String path, String key) throws IOException {
//		File f = new File(path);
//		f.getParentFile().mkdirs();
//		FileWriter fw = new FileWriter(f);
//		fw.write(key);
//		fw.close();
//
//
//	}
	private void saveKeysInDb (String appId, String publicKey, String privateKey)
	{
		this.jdbcInMemory.insertData(appId, publicKey, privateKey);
	}


	public String keyGenerateAndReturnPublicKey(String appId) {
		//GenerateKeys gk;
		String publicKeyPEM = null;
		String privateKeyPEM = null;
		System.out.println("main method of generator");
		try {
			//gk = new GenerateKeys(1024);
			//gk.createKeys();
			this.generateSecureKeys(1024);
			this.createKeys();

			// THIS IS PEM:
	        publicKeyPEM = PUBLICKEY_PREFIX + "\n" + DatatypeConverter.printBase64Binary(this.getPublicKey().getEncoded()).replaceAll("(.{64})", "$1\n") + "\n" + PUBLICKEY_POSTFIX;
	        privateKeyPEM = PRIVATEKEY_PREFIX + "\n" + DatatypeConverter.printBase64Binary(this.getPrivateKey().getEncoded()).replaceAll("(.{64})", "$1\n") + "\n" + PRIVATEKEY_POSTFIX;
	        
	        //gk.writeToFile("KeyPair/publicKey", publicKeyPEM);
			//gk.writeToFile("KeyPair/privateKey", privateKeyPEM);
			this.saveKeysInDb(appId,publicKeyPEM,privateKeyPEM);
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return publicKeyPEM;
	}
	public static PrivateKey readPrivateKey()
			throws IOException, GeneralSecurityException, Base64DecodingException {
		PrivateKey key = null;
		String fileString = new String(Files.readAllBytes(Paths.get("KeyPair/privateKey")), StandardCharsets.UTF_8);
		fileString = fileString.replace(
				"-----BEGIN RSA PRIVATE KEY-----\n", "")
				.replace("-----END RSA PRIVATE KEY-----", "");
		byte [] keyBytes = Base64.decode(fileString);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		key = kf.generatePrivate(spec);
		return key;
	}

}
