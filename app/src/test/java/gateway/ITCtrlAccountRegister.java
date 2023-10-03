package gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gateway.config.Config;
import gateway.controller.CtrlAccountRegister;
import gateway.controller.CtrlAuthLogin;
import gateway.soap.request.Credentials;
import gateway.soap.response.ResSession;
import gateway.testutils.TestUtilConfig;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ITCtrlAccountRegister
{

	@BeforeEach void setup () { Config.initializeFromEnv (); }

	@Test void Register ()
	{

		Credentials credentials = new Credentials (UUID.randomUUID ().toString (), "pass");

		Credentials credentialsCopy = credentials;

		ResSession res = CtrlAccountRegister.account_register (credentials);
		assertEquals (201, res.code, "Register successfully");

		res = CtrlAccountRegister.account_register (credentialsCopy);
		assertEquals (500, res.code, "Internal server error");

		TestUtilConfig.makeInvalidAll ();
		credentials.username = UUID.randomUUID ().toString ();
		res = CtrlAuthLogin.auth_login (credentials);
		assertEquals (500, res.code, "Internal error");
	}
}
