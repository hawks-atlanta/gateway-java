package gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gateway.config.Config;
import gateway.controller.CtrlAccountRegister;
import gateway.controller.CtrlAuthLogin;
import gateway.services.ServiceAuth;
import gateway.soap.request.Credentials;
import gateway.soap.response.ResSession;
import gateway.testutils.TestUtilConfig;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ITServiceAuth
{
	@BeforeEach void setup () { Config.initializeFromEnv (); }

	@Test void Authenticate ()
	{
		// 200
		Credentials cred = new Credentials (UUID.randomUUID ().toString (), "pass");
		ResSession res = CtrlAccountRegister.account_register (cred);
		assertEquals (201, res.code, "Login successfully");
		assertEquals (
			200, ServiceAuth.authenticate (res.auth.token).code, () -> "Auth'd successful");

		// 401
		assertEquals (401, ServiceAuth.authenticate ("invalid token").code, () -> "Auth failed");

		// 500
		TestUtilConfig.makeInvalidAll ();
		assertEquals (500, ServiceAuth.authenticate ("").code, () -> "Can't reach Auth Server");
	}

	@Test void GetUserUUID ()
	{
		// 200
		Credentials cred = new Credentials (UUID.randomUUID ().toString (), "pass");
		ResSession res = CtrlAccountRegister.account_register (cred);
		assertEquals (201, res.code, "Login successfully");
		assertEquals (
			200, ServiceAuth.getUserUUID (res.auth.token, cred.username).code,
			"Ok. Username retrieved");

		// 401
		assertEquals (
			401, ServiceAuth.getUserUUID ("invalid token", cred.username).code, "Auth failed");

		// 500
		TestUtilConfig.makeInvalidAll ();
		assertEquals (
			500, ServiceAuth.getUserUUID (res.auth.token, cred.username).code,
			"Can't reach Auth Server");
	}

	@Test void Login ()
	{
		Credentials cred = new Credentials (UUID.randomUUID ().toString (), "pass");
		// register

		ResSession res = CtrlAccountRegister.account_register (cred);
		assertEquals (201, res.code, "Register successfully");

		ResSession login = CtrlAuthLogin.auth_login (cred);
		assertEquals (201, login.code, "Login succeed");

		cred.username = UUID.randomUUID ().toString ();
		login = CtrlAuthLogin.auth_login (cred);
		assertEquals (401, login.code, "Invalid credentials");

		cred.username = null;
		login = CtrlAuthLogin.auth_login (cred);
		assertEquals (400, login.code, "Bad Request: Invalid Field");

		TestUtilConfig.makeInvalidAll ();
		cred.username = UUID.randomUUID ().toString ();
		login = CtrlAuthLogin.auth_login (cred);
		assertEquals (500, login.code, "Internal error, try again later");
	}
}
