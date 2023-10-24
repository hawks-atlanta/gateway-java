package gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gateway.config.Config;
import gateway.controller.CtrlShareFile;
import gateway.controller.CtrlShareList;
import gateway.controller.CtrlShareListWithWho;
import gateway.controller.CtrlUnshareFile;
import gateway.services.ServiceMetadata.ResSaveFile;
import gateway.soap.request.Authorization;
import gateway.soap.request.ReqFile;
import gateway.soap.request.ReqShareFile;
import gateway.soap.request.ReqShareRemove;
import gateway.soap.response.ResStatus;
import gateway.testutils.TestUtilAuth;
import gateway.testutils.TestUtilConfig;
import gateway.testutils.TestUtilShare;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ITShareManagement
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
		ReqShareFile reqShareFile =
			TestUtilShare.createShareFile (resSaveFile1, username2, tokenUser1);
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

	@Test void UnshareFile ()
	{

		String username1 = UUID.randomUUID ().toString ();
		String username2 = UUID.randomUUID ().toString ();

		// register
		String tokenUser1 = TestUtilAuth.registerAndLoginUserSuccess (username1);
		String tokenUser2 = TestUtilAuth.registerAndLoginUserSuccess (username2);

		// create a file and aux file
		ResSaveFile resSaveFile1 = TestUtilAuth.createFile (tokenUser1);
		ResSaveFile resSaveFile2 = TestUtilAuth.createFile (tokenUser2);
		ReqShareFile reqShareFile =
			TestUtilShare.createShareFile (resSaveFile1, username2, tokenUser1);

		// 204
		ReqShareRemove reqShareRemove =
			new ReqShareRemove (tokenUser1, reqShareFile.fileUUID, reqShareFile.otherUsername);
		assertEquals (
			204, CtrlUnshareFile.unshare_file (reqShareRemove).code, "The file was unshared");

		// 409
		assertEquals (
			409, CtrlUnshareFile.unshare_file (reqShareRemove).code,
			"The file is already unshared with the given user.");

		// 403
		reqShareFile = TestUtilShare.createShareFile (resSaveFile1, username2, tokenUser1);
		reqShareFile.fileUUID = resSaveFile2.fileUUID;
		assertEquals (
			403, CtrlUnshareFile.unshare_file (reqShareRemove).code,
			"The file is not owned by the user.");

		// 401
		reqShareFile = TestUtilShare.createShareFile (resSaveFile1, username2, tokenUser1);
		reqShareFile.token = "token_invalid";
		assertEquals (401, CtrlUnshareFile.unshare_file (reqShareRemove).code, "unauthorized");

		// 400
		reqShareFile = TestUtilShare.createShareFile (resSaveFile1, username2, tokenUser1);
		reqShareFile.otherUsername = null;
		assertEquals (
			400, CtrlUnshareFile.unshare_file (reqShareRemove).code,
			"The owner_uuid or file_uuid were not a valid UUID or the JSON body does't fullfill the validations.");

		// 500
		// authorization.token = tokenUser1;
		reqShareFile = TestUtilShare.createShareFile (resSaveFile1, username2, tokenUser1);
		TestUtilConfig.makeInvalidMetadata ();
		assertEquals (
			500, CtrlUnshareFile.unshare_file (reqShareRemove).code, "Can't reach metadata");
	}

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
		ReqShareFile reqShareFile =
			TestUtilShare.createShareFile (resSaveFile1, username2, tokenUser1);
		ResStatus res = CtrlShareFile.share_file (reqShareFile);
		assertEquals (204, res.code, "The file have been shared");

		// List of shared with me
		assertEquals (
			200, CtrlShareList.share_list (authorization).code, "Ok. The directory was listed.");

		// 401
		authorization.token = "invalid_token";
		assertEquals (401, CtrlShareList.share_list (authorization).code, "unauthorized");

		authorization.token = tokenUser1;
		TestUtilConfig.makeInvalidMetadata ();
		assertEquals (500, CtrlShareList.share_list (authorization).code, "Can't reach metadata");
	}

	@Test void shareListWithWho ()
	{
		String username1 = UUID.randomUUID ().toString ();
		String username2 = UUID.randomUUID ().toString ();

		// Register

		String tokenUser1 = TestUtilAuth.registerAndLoginUserSuccess (username1);
		TestUtilAuth.registerAndLoginUserSuccess (username2);

		// Create a file
		ResSaveFile resSaveFile1 = TestUtilAuth.createFile (tokenUser1);

		// Share file
		ReqShareFile reqShareFile =
			TestUtilShare.createShareFile (resSaveFile1, username2, tokenUser1);
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
