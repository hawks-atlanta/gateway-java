package gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gateway.config.Config;
import gateway.controller.CtrlAccountRegister;
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
		assertEquals (201, res.code, "Register successfully");
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
		assertEquals (201, res.code, "Register successfully");
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

	@Test void authRefresh () throws Exception
	{
		// register
		Credentials cred = new Credentials (UUID.randomUUID ().toString (), "pass");
		ResSession resReg = CtrlAccountRegister.account_register (cred);
		assertEquals (201, resReg.code, "Register successfully");

		// 200 auth
		ResSession resAuth = ServiceAuth.authenticate (resReg.auth.token);
		assertEquals (200, resAuth.code, () -> "Auth'd successful");

		// check new token
		Long expNew = ServiceAuth.tokenGetClaimLong (resAuth.auth.token, "exp");
		Long currentTime = System.currentTimeMillis () / 1000;

		assertTrue (expNew > currentTime, () -> "New token fresh");
	}
}
