package gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gateway.config.Config;
import gateway.controller.CtrlShareFile;
import gateway.controller.CtrlShareListWithWho;
import gateway.services.ServiceMetadata.ResSaveFile;
import gateway.soap.request.ReqFile;
import gateway.soap.request.ReqShareFile;
import gateway.soap.response.ResStatus;
import gateway.testutils.TestUtilAuth;
import gateway.testutils.TestUtilConfig;
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

		String tokenUser1 = TestUtilAuth.registerAndLoginUserSuccess (username1);
		TestUtilAuth.registerAndLoginUserSuccess (username2);

		// Create a file
		ResSaveFile resSaveFile1 = TestUtilAuth.createFile (tokenUser1);

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
}
