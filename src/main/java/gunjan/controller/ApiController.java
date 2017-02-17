package gunjan.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gunjan.model.Login;
import gunjan.model.SuccessResponse;
import gunjan.utils.GenerateKeys;

@RestController
public class ApiController {

private String prvKey = "";
    @RequestMapping(value = "/key", method = RequestMethod.GET, produces = "application/json")
    public SuccessResponse key(@RequestParam(value="appId", required=true) String appId) {
      SuccessResponse res = new SuccessResponse();
    	if(null !=appId && !appId.equals(""))	
    	{    	
    		String keyValue = GenerateKeys.keyGenerateAndReturnPublicKey();
    		prvKey = keyValue.split("::::::::")[1];
    		res.setData(GenerateKeys.keyGenerateAndReturnPublicKey());
            return res;
            
    	}
    	else
    	{
    
    		res.setData("missing appId");
            return res;
    	}
    	
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
    public SuccessResponse login(@RequestBody Login login) throws NoSuchAlgorithmException, NoSuchPaddingException {
      SuccessResponse res = new SuccessResponse();
    	if(null !=login )	
    	{    	
    		
    		 Cipher cipher = Cipher.getInstance("RSA");
    		 try {
				cipher.init(Cipher.DECRYPT_MODE, loadPrivateKey());
				
				//writeToFile(new File("KeyPair/text_decrypted.txt"), cipher.doFinal(login.getUserName().getBytes()));
				byte[] encodedUser = DatatypeConverter.parseBase64Binary(login.getUserName());
				byte[] encodedPass = DatatypeConverter.parseBase64Binary(login.getPassword());
				String uName = new String(cipher.doFinal(encodedUser), "UTF-8");
				String pws = new String(cipher.doFinal(encodedPass), "UTF-8");
				writeToFile("KeyPair/user_decrypted.txt", uName);
				writeToFile("KeyPair/pass_decrypted.txt", pws);
				
				
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		res.setData(login.getUserName()+ " :: "+login.getPassword());
            return res;
            
    	}
    	else
    	{
    
    		res.setData("missing login data");
            return res;
    	}
    	
    }
    public void writeToFile(String path, String key) throws IOException {
		File f = new File(path);
		f.getParentFile().mkdirs();
		FileWriter fw = new FileWriter(f);
		fw.write(key);
		fw.close();

	}
	private void writeToFile(File output, byte[] toWrite)
			throws IllegalBlockSizeException, BadPaddingException, IOException {
		FileOutputStream fos = new FileOutputStream(output);
		fos.write(toWrite);
		fos.flush();
		fos.close();
	}
    public static PrivateKey loadPrivateKey() 
            throws IOException, GeneralSecurityException {
        PrivateKey key = null;
      	
            
            byte[] keyBytes = Files.readAllBytes(new File("KeyPair/privateKey").toPath());
    		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
    		KeyFactory kf = KeyFactory.getInstance("RSA");
    		key =  kf.generatePrivate(spec);
        
        return key;
    }
 

}
