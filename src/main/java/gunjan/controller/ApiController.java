package gunjan.controller;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import gunjan.model.Login;
import gunjan.model.SuccessResponse;
import gunjan.utils.GenerateKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;

@RestController
public class ApiController {


 @Autowired
 private GenerateKeys generateKeys ;

    @RequestMapping(value = "/key", method = RequestMethod.GET, produces = "application/json")
    public SuccessResponse key(@RequestParam(value = "appId", required = true) String appId) {
        SuccessResponse res = new SuccessResponse();
        if (null != appId && !appId.equals("")) {
            res.setData(generateKeys.keyGenerateAndReturnPublicKey(appId));
            return res;

        } else {

            res.setData("missing appId");
            return res;
        }

    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
    public SuccessResponse login(@RequestBody Login login) throws NoSuchAlgorithmException, NoSuchPaddingException, Base64DecodingException {
        SuccessResponse res = new SuccessResponse();
        String uName = null;
        String pws = null;
        if (null != login) {

            Cipher cipher = Cipher.getInstance("RSA");
            try {
                cipher.init(Cipher.DECRYPT_MODE, generateKeys.readPrivateKey(login.getAppId()));
                byte[] encodedUser = DatatypeConverter.parseBase64Binary(login.getUserName());
                byte[] encodedPass = DatatypeConverter.parseBase64Binary(login.getPassword());
                uName = new String(cipher.doFinal(encodedUser), "UTF-8");
                pws = new String(cipher.doFinal(encodedPass), "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            res.setData("User name ::  " + uName + " --  password  :: " + pws);
            return res;

        } else {
            res.setData("missing login data");
            return res;
        }

    }








}
