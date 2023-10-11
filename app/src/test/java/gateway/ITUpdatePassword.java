package gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import gateway.config.Config;
import gateway.controller.CtrlAccountRegister;
import gateway.controller.CtrlUpdatePassword;
import gateway.soap.request.Credentials;
import gateway.soap.request.ReqAccPassword;
import gateway.soap.response.ResSession;
import gateway.testutils.TestUtilConfig;


public class ITUpdatePassword {

    @BeforeEach void setup () { Config.initializeFromEnv (); }

    @Test void updatedPassword(){
        Credentials cred = new Credentials (UUID.randomUUID ().toString (), "pass");
        System.err.println(cred.password);
        System.err.println(cred.username);
        ResSession res = CtrlAccountRegister.account_register (cred);
		assertEquals (201, res.code, "User registered successfully");

        ReqAccPassword accPassword = new ReqAccPassword();

        accPassword.newpassword = "pass2";
        accPassword.oldpassword = cred.password;
        accPassword.token = res.auth.token;
        assertEquals (200, CtrlUpdatePassword.account_password(accPassword).code, "Password updated successfully");

        accPassword.newpassword = null;
        assertEquals (400, CtrlUpdatePassword.account_password(accPassword).code, "Bad Request: Invalid Field");

        accPassword.newpassword = "pass2";
        accPassword.oldpassword = "pass2";
        accPassword.token = "invalid token";
        assertEquals (401, CtrlUpdatePassword.account_password(accPassword).code, "Authorization failed");

        accPassword.token = res.auth.token;
        assertEquals (401, CtrlUpdatePassword.account_password(accPassword).code, "Invalid credentials");

        

        TestUtilConfig.makeInvalidAll ();
        assertEquals (500, CtrlUpdatePassword.account_password(accPassword).code, "Can't reach authentication service");
    }
}
