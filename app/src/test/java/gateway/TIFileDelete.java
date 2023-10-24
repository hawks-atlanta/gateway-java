package gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gateway.config.Config;
import gateway.controller.CtrlAccountRegister;
import gateway.controller.CtrlFileDelete;
import gateway.controller.CtrlFileUpload;
import gateway.soap.request.Credentials;
import gateway.soap.request.ReqFileDelete;
import gateway.soap.request.ReqFileUpload;
import gateway.soap.response.ResFileNew;
import gateway.soap.response.ResSession;
import gateway.testutils.TestUtilConfig;
import gateway.testutils.TestUtilGenerator;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TIFileDelete
{
	@BeforeEach void setup () { Config.initializeFromEnv (); }

	@Test void fileDelete () throws InterruptedException
	{
		// Register
		Credentials cred = new Credentials (UUID.randomUUID ().toString (), "pass");
		ResSession resSession = CtrlAccountRegister.account_register (cred);
		assertEquals (201, resSession.code, "User registered successfully");

		// Upload file
		ReqFileUpload reqFileUpload = new ReqFileUpload ();
		reqFileUpload.fileContent = TestUtilGenerator.randomBytes (1);
		reqFileUpload.fileName = UUID.randomUUID ().toString ();
		reqFileUpload.location = null;
		reqFileUpload.token = resSession.auth.token;

		ResFileNew resFileNew = CtrlFileUpload.file_upload (reqFileUpload);
		assertEquals (201, resFileNew.code, "File upload success");

		Thread.sleep (1000); // wait for upload

		// Bad Request
		ReqFileDelete reqFileDelete = new ReqFileDelete ();
		reqFileDelete.token = "invalid token";
		reqFileDelete.fileUUID = null;
		assertEquals (
			400, CtrlFileDelete.file_delete (reqFileDelete).code, "Bad Request: Invalid Field");

		// Authorization failed
		reqFileDelete.fileUUID = resFileNew.fileUUID;
		assertEquals (401, CtrlFileDelete.file_delete (reqFileDelete).code, "Authorization failed");

		// Delete File
		reqFileDelete.token = resSession.auth.token;
		assertEquals (204, CtrlFileDelete.file_delete (reqFileDelete).code, "Delete File");

		// File not found
		assertEquals (404, CtrlFileDelete.file_delete (reqFileDelete).code, "File not found");

		// Register another user
		Credentials credAnotherUser =
			new Credentials (UUID.randomUUID ().toString (), "anotherUser");
		ResSession resSessionAnotherUser = CtrlAccountRegister.account_register (credAnotherUser);
		assertEquals (201, resSessionAnotherUser.code, "Another User registered successfully");

		// Upload file of another user
		ReqFileUpload reqFileUploadAnotherUser = new ReqFileUpload ();
		reqFileUploadAnotherUser.fileContent = TestUtilGenerator.randomBytes (1);
		reqFileUploadAnotherUser.fileName = UUID.randomUUID ().toString ();
		reqFileUploadAnotherUser.location = null;
		reqFileUploadAnotherUser.token = resSessionAnotherUser.auth.token;

		ResFileNew resFileNewAnotherUser = CtrlFileUpload.file_upload (reqFileUploadAnotherUser);
		assertEquals (201, resFileNewAnotherUser.code, "File upload success");

		Thread.sleep (1000); // wait for upload

		// The user tried to delete a file that not own
		reqFileDelete.fileUUID = resFileNewAnotherUser.fileUUID;
		assertEquals (
			403, CtrlFileDelete.file_delete (reqFileDelete).code,
			"The user tried to delete a file that not own");

		// Can't reach metadata
		TestUtilConfig.makeInvalidMetadata ();
		assertEquals (500, CtrlFileDelete.file_delete (reqFileDelete).code, "Can't reach metadata");
	}
}
