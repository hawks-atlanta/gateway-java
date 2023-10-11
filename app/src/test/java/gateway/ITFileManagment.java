package gateway;

import static org.junit.jupiter.api.Assertions.*;

import gateway.config.Config;
import gateway.controller.CtrlAccountRegister;
import gateway.controller.CtrlFileRename;
import gateway.controller.CtrlFileUpload;
import gateway.soap.request.Credentials;
import gateway.soap.request.ReqFileRename;
import gateway.soap.request.ReqFileUpload;
import gateway.soap.response.ResFileNew;
import gateway.soap.response.ResSession;
import gateway.testutils.TestUtilConfig;
import gateway.testutils.TestUtilGenerator;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder (OrderAnnotation.class) class ITFileManagment
{
	static class State
	{
		public static String username;
		public static UUID fileUUID;
		public static int fileSize;
	}

	@BeforeEach void setup () { Config.initializeFromEnv (); }

	@Test @Order (1) void renameFile () throws InterruptedException
	{
		// register

		ResSession resR = CtrlAccountRegister.account_register (
			new Credentials (UUID.randomUUID ().toString (), "pass"));
		assertEquals (201, resR.code, "Register successful");

		// upload file

		ReqFileUpload reqU = new ReqFileUpload ();
		reqU.fileContent = TestUtilGenerator.randomBytes (1);
		reqU.fileName = UUID.randomUUID ().toString ();
		reqU.location = null;
		reqU.token = resR.auth.token;

		ResFileNew resU = CtrlFileUpload.file_upload (reqU);
		assertEquals (201, resU.code, "File upload success");

		Thread.sleep (1000); // wait for upload

		// rename file

		ReqFileRename reqN = new ReqFileRename ();
		reqN.token = resR.auth.token;
		reqN.newName = UUID.randomUUID ().toString ();
		reqN.fileUUID = resU.fileUUID;
		assertEquals (204, CtrlFileRename.file_rename (reqN).code, "Rename success");

		// errors

		assertEquals (
			409, CtrlFileRename.file_rename (reqN).code, "A file with that name already exists");

		TestUtilConfig.makeInvalidMetadata ();
		assertEquals (500, CtrlFileRename.file_rename (reqN).code, "Can't reach Metadata server");

		reqN.token = "invalid-token";
		assertEquals (401, CtrlFileRename.file_rename (reqN).code, "Authorization failed");

		reqN.newName = null;
		assertEquals (400, CtrlFileRename.file_rename (reqN).code, "Field validation failed");
	}
}
