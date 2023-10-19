package gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gateway.config.Config;
import gateway.controller.CtrlShareFile;
import gateway.controller.CtrlShareList;
import gateway.services.ServiceMetadata.ResSaveFile;
import gateway.soap.request.Authorization;
import gateway.soap.request.ReqFile;
import gateway.soap.request.ReqShareFile;
import gateway.soap.response.ResShareList;
import gateway.soap.response.ResStatus;
import gateway.testutils.TestUtilAuth;
import gateway.testutils.TestUtilConfig;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ITShareList
{
	@BeforeEach void setup () { Config.initializeFromEnv (); }

	@Test void shareList ()
	{
		String username1 = UUID.randomUUID ().toString ();
		String username2 = UUID.randomUUID ().toString ();

		// Register
		String tokenUser1 = TestUtilAuth.registerAndLoginUserSuccess (username1);
		Authorization authorization = new Authorization (tokenUser1);
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
		// ResShareList resShareList = new ResShareList();
		assertEquals (
			200, CtrlShareList.share_list (authorization).code, "Ok. The directory was listed.");

		// errors
		/*
		 * reqFile.fileUUID = null;
		 * assertEquals(
		 * 400, CtrlShareList.share_list_with_who(reqFile).code,
		 * "Bad Request: Invalid Field");
		 *
		 * reqFile.token = "invalid token";
		 * reqFile.fileUUID = resSaveFile1.fileUUID;
		 * assertEquals(
		 * 401, CtrlShareList.share_list_with_who(reqFile).code,
		 * "Authorization failed");
		 *
		 * reqFile.fileUUID = UUID.randomUUID();
		 * reqFile.token = tokenUser1;
		 * assertEquals(
		 * 404, CtrlShareList.share_list_with_who(reqFile).code,
		 * "There is no file with the given fileUUID");
		 */

		TestUtilConfig.makeInvalidMetadata ();
		assertEquals (500, CtrlShareList.share_list (authorization).code, "Can't reach metadata");
	}
}
