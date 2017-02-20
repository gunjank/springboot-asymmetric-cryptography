package gunjan.utils;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import gunjan.dao.JDBCInMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;

@Component
public class GenerateKeys {

	@Autowired
	private JDBCInMemory jdbcInMemory;

	private KeyPairGenerator keyGen;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	private static final String PUBLICKEY_PREFIX    = "-----BEGIN PUBLIC KEY-----";
	private static final String PUBLICKEY_POSTFIX   = "-----END PUBLIC KEY-----";
	private static final String PRIVATEKEY_PREFIX   = "-----BEGIN RSA PRIVATE KEY-----";
	private static final String PRIVATEKEY_POSTFIX  = "-----END RSA PRIVATE KEY-----";


	public GenerateKeys(){}
	private void generateSecureKeys() throws NoSuchAlgorithmException, NoSuchProviderException {
		this.keyGen = KeyPairGenerator.getInstance("RSA");
		this.keyGen.initialize(1024);
	}

	private void createKeys() {
		KeyPair pair = this.keyGen.generateKeyPair();
		this.privateKey = pair.getPrivate();
		this.publicKey = pair.getPublic();
	}

	private PrivateKey getPrivateKey() {
		return this.privateKey;
	}

	private PublicKey getPublicKey() {
		return this.publicKey;
	}

	private void saveKeysInDb (String appId, String publicKey, String privateKey)
	{
		this.jdbcInMemory.insertData(appId, publicKey, privateKey);
	}


	public String keyGenerateAndReturnPublicKey(String appId) {
		String publicKeyPEM = null;
		String privateKeyPEM;
		System.out.println("main method of generator");
		try {
			this.generateSecureKeys();
			this.createKeys();

			// THIS IS PEM:
	        publicKeyPEM = PUBLICKEY_PREFIX + "\n" + DatatypeConverter.printBase64Binary(this.getPublicKey().getEncoded()).replaceAll("(.{64})", "$1\n") + "\n" + PUBLICKEY_POSTFIX;
	        privateKeyPEM = PRIVATEKEY_PREFIX + "\n" + DatatypeConverter.printBase64Binary(this.getPrivateKey().getEncoded()).replaceAll("(.{64})", "$1\n") + "\n" + PRIVATEKEY_POSTFIX;
	        this.saveKeysInDb(appId,publicKeyPEM,privateKeyPEM);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return publicKeyPEM;
	}
	public PrivateKey readPrivateKey(String appId)
			throws IOException, GeneralSecurityException, Base64DecodingException {
		PrivateKey key;
		String fileString = this.jdbcInMemory.getPrivateKeyForAppId(appId);
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
