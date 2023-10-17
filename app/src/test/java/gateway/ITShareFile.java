package gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gateway.config.Config;
import gateway.controller.CtrlShareFile;
import gateway.services.ServiceMetadata.ResSaveFile;
import gateway.soap.request.ReqShareFile;
import gateway.soap.response.ResStatus;
import gateway.testutils.TestUtilAuth;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ITShareFile
{

	@BeforeEach void setup () { Config.initializeFromEnv (); }

	@Test void ShareFile ()
	{

		String username1 = UUID.randomUUID ().toString ();
		String username2 = UUID.randomUUID ().toString ();

		// register
		String tokenUser1 = TestUtilAuth.registerAndLoginUserSuccess (username1);
		String tokenUser2 = TestUtilAuth.registerAndLoginUserSuccess (username2);

		// create a file and aux file
		ResSaveFile resSaveFile1 = TestUtilAuth.createFile (tokenUser1);
		ResSaveFile resSaveFile2 = TestUtilAuth.createFile (tokenUser2);

		// 204
		ReqShareFile reqShareFile = new ReqShareFile ();
		reqShareFile.fileUUID = resSaveFile1.fileUUID;
		reqShareFile.otherUsername = username2;
		reqShareFile.token = tokenUser1;
		ResStatus res = CtrlShareFile.share_file (reqShareFile);
		assertEquals (204, res.code, "The file have been shared");

		// 409
		res = CtrlShareFile.share_file (reqShareFile);
		assertEquals (409, res.code, "The file is already shared with the given user.");

		// 403
		reqShareFile.fileUUID = resSaveFile2.fileUUID;
		res = CtrlShareFile.share_file (reqShareFile);
		assertEquals (403, res.code, "The file is not owned by the user.");

		// 401
		reqShareFile.token = "token_invalid";
		res = CtrlShareFile.share_file (reqShareFile);
		assertEquals (401, res.code, "unauthorized");

		// 400
		reqShareFile.otherUsername = null;
		res = CtrlShareFile.share_file (reqShareFile);
		assertEquals (
			400, res.code,
			"The owner_uuid or file_uuid were not a valid UUID or the JSON body does't fullfill the validations.");
	}
}
