package gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gateway.config.Config;
import gateway.controller.CtrlAccountRegister;
import gateway.controller.CtrlShareFile;
import gateway.controller.CtrlShareListWithWho;
import gateway.services.ServiceAuth;
import gateway.services.ServiceMetadata;
import gateway.services.ServiceMetadata.ResSaveFile;
import gateway.soap.request.Credentials;
import gateway.soap.request.ReqFile;
import gateway.soap.request.ReqShareFile;
import gateway.soap.response.ResSession;
import gateway.soap.response.ResStatus;
import gateway.testutils.TestUtilConfig;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ITShareListWhitWho
{
	@BeforeEach void setup () { Config.initializeFromEnv (); }

	@Test void shareListWhitWho ()
	{
		String username1 = UUID.randomUUID ().toString ();
		String username2 = UUID.randomUUID ().toString ();

		// Register
		String tokenUser1 = registerAndLoginUserSuccess (username1);
		registerAndLoginUserSuccess (username2);

		// Create a file
		ResSaveFile resSaveFile1 = createFile (tokenUser1);

		// Share file
		ReqShareFile reqShareFile = new ReqShareFile ();
		reqShareFile.fileUUID = resSaveFile1.fileUUID;
		reqShareFile.otherUsername = username2;
		reqShareFile.token = tokenUser1;
		ResStatus res = CtrlShareFile.share_file (reqShareFile);
		assertEquals (204, res.code, "The file have been shared");

		// List of shared with users successfully
		ReqFile reqFile = new ReqFile ();
		reqFile.fileUUID = resSaveFile1.fileUUID;
		reqFile.token = tokenUser1;
		assertEquals (
			200, CtrlShareListWithWho.share_list_with_who (reqFile).code,
			"List of shared with users successfully");

		// errors
		reqFile.fileUUID = null;
		assertEquals (
			400, CtrlShareListWithWho.share_list_with_who (reqFile).code,
			"Bad Request: Invalid Field");

		reqFile.token = "invalid token";
		reqFile.fileUUID = resSaveFile1.fileUUID;
		assertEquals (
			401, CtrlShareListWithWho.share_list_with_who (reqFile).code, "Authorization failed");

		reqFile.fileUUID = UUID.randomUUID ();
		reqFile.token = tokenUser1;
		assertEquals (
			404, CtrlShareListWithWho.share_list_with_who (reqFile).code,
			"There is no file with the given fileUUID");

		reqFile.fileUUID = resSaveFile1.fileUUID;
		TestUtilConfig.makeInvalidMetadata ();
		assertEquals (
			500, CtrlShareListWithWho.share_list_with_who (reqFile).code, "Can't reach metadata");
	}

	private String registerAndLoginUserSuccess (String username)
	{
		ResSession res = CtrlAccountRegister.account_register (new Credentials (username, "pass"));
		assertEquals (201, res.code, "User registered successfully");
		return res.auth.token;
	}

	private ResSaveFile createFile (String token)
	{
		return ServiceMetadata.saveFile (
			UUID.fromString (ServiceAuth.tokenGetClaim (token, "uuid")), null, true, "txt",
			"filename_t", (new Random ().nextInt (3000) + 1));
	}
}
