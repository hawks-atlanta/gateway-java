package gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gateway.config.Config;
import gateway.controller.CtrlAccountRegister;
import gateway.controller.CtrlFileList;
import gateway.controller.CtrlFileNewDir;
import gateway.controller.CtrlFileUpload;
import gateway.soap.request.Credentials;
import gateway.soap.request.ReqFileList;
import gateway.soap.request.ReqFileNewDir;
import gateway.soap.request.ReqFileUpload;
import gateway.soap.response.ResFileNew;
import gateway.soap.response.ResSession;
import gateway.testutils.TestUtilConfig;
import gateway.testutils.TestUtilGenerator;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ITFileList
{
	@BeforeEach void setup () { Config.initializeFromEnv (); }

	@Test void fileList () throws InterruptedException
	{
		// register
		Credentials cred = new Credentials (UUID.randomUUID ().toString (), "pass");
		ResSession resSession = CtrlAccountRegister.account_register (cred);
		assertEquals (201, resSession.code, "User registered successfully");

		// upload file

		ReqFileUpload reqFileUpload = new ReqFileUpload ();
		reqFileUpload.fileContent = TestUtilGenerator.randomBytes (1);
		reqFileUpload.fileName = UUID.randomUUID ().toString ();
		reqFileUpload.location = null;
		reqFileUpload.token = resSession.auth.token;

		ResFileNew resFileNew = CtrlFileUpload.file_upload (reqFileUpload);
		assertEquals (201, resFileNew.code, "File upload success");

		Thread.sleep (1000); // wait for upload

		// create directory

		ReqFileNewDir reqFileNewDir = new ReqFileNewDir ();
		reqFileNewDir.directoryName = UUID.randomUUID ().toString ();
		reqFileNewDir.token = resSession.auth.token;
		reqFileNewDir.location = null;

		ResFileNew resD = CtrlFileNewDir.file_new_dir (reqFileNewDir);
		assertEquals (201, resD.code, "Directory created");

		// file list (parentUUID query parameter is not provided)
		ReqFileList reqFileList = new ReqFileList ();
		reqFileList.token = resSession.auth.token;
		reqFileList.location = null;
		assertEquals (
			200, CtrlFileList.file_list (reqFileList).code,
			"File list success from root (location is null)");

		reqFileList.location = new UUID (0L, 0L);
		assertEquals (
			200, CtrlFileList.file_list (reqFileList).code,
			"File list success from root (location is empty)");

		// file list (parentUUID query parameter is provided)
		reqFileList.location = resD.fileUUID;
		assertEquals (
			200, CtrlFileList.file_list (reqFileList).code, "File list success from directory");

		// errors
		reqFileList.token = "invalid token";
		assertEquals (401, CtrlFileList.file_list (reqFileList).code, "Authorization failed");

		reqFileList.location = UUID.randomUUID ();
		reqFileList.token = resSession.auth.token;
		assertEquals (
			404, CtrlFileList.file_list (reqFileList).code,
			"No directory with the given was found");

		// register another user
		Credentials credAnotherUser =
			new Credentials (UUID.randomUUID ().toString (), "anotherUser");
		ResSession resSessionAnotherUser = CtrlAccountRegister.account_register (credAnotherUser);
		assertEquals (201, resSessionAnotherUser.code, "Another User registered successfully");

		// create directory another user

		ReqFileNewDir reqFileNewDirAnotherUser = new ReqFileNewDir ();
		reqFileNewDirAnotherUser.directoryName = UUID.randomUUID ().toString ();
		reqFileNewDirAnotherUser.token = resSessionAnotherUser.auth.token;
		reqFileNewDirAnotherUser.location = null;

		ResFileNew resDAnotherUser = CtrlFileNewDir.file_new_dir (reqFileNewDirAnotherUser);
		assertEquals (201, resDAnotherUser.code, "Directory created Another User");

		reqFileList.location = resDAnotherUser.fileUUID;
		assertEquals (
			403, CtrlFileList.file_list (reqFileList).code, "Directory not owned by the user");

		TestUtilConfig.makeInvalidMetadata ();
		assertEquals (500, CtrlFileList.file_list (reqFileList).code, "Can't reach metadata");
	}
}
