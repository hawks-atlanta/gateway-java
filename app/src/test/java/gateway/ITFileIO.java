package gateway;

import static org.junit.jupiter.api.Assertions.*;

import gateway.config.Config;
import gateway.controller.CtrlAccountRegister;
import gateway.controller.CtrlAuthLogin;
import gateway.controller.CtrlFileDownload;
import gateway.controller.CtrlFileUpload;
import gateway.soap.request.Credentials;
import gateway.soap.request.ReqFile;
import gateway.soap.request.ReqFileUpload;
import gateway.soap.response.ResFileDownload;
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

@TestMethodOrder (OrderAnnotation.class) class ITFileIO
{
	static class State
	{
		public static String username;
		public static UUID fileUUID;
		public static int fileSize;
	}

	@BeforeEach void setup () { Config.initializeFromEnv (); }

	@Test @Order (1) void uploadFile ()
	{

		// register

		State.username = UUID.randomUUID ().toString ();
		ResSession resR =
			CtrlAccountRegister.account_register (new Credentials (State.username, "pass"));
		assertEquals (201, resR.code, "Login successfully");
		String token = resR.auth.token;

		// 400 field validation

		ReqFileUpload args = new ReqFileUpload ();
		assertEquals (400, CtrlFileUpload.file_upload (args).code, "Field validation failed");

		// 401 auth failed

		args.fileName = UUID.randomUUID ().toString ();
		args.fileContent = TestUtilGenerator.randomBytes (1);
		args.location = null;
		args.token = "invalid token";
		assertEquals (401, CtrlFileUpload.file_upload (args).code, "Authorization failed");

		// 400 empty file

		args.token = token;
		args.fileContent = TestUtilGenerator.randomBytes (0);
		assertEquals (400, CtrlFileUpload.file_upload (args).code, "Empty file is rejected");

		// 413 file too big

		args.fileContent = TestUtilGenerator.randomBytes (100000001);
		assertEquals (413, CtrlFileUpload.file_upload (args).code, "Big file is rejected");

		// 201 file uploaded

		State.fileSize = 8;
		args.fileContent = TestUtilGenerator.randomASCIIBytes ((int)State.fileSize);
		ResFileNew resU = CtrlFileUpload.file_upload (args);
		assertEquals (201, resU.code, "File upload success");
		State.fileUUID = resU.fileUUID;

		args.fileName = UUID.randomUUID ().toString ();
		TestUtilConfig.makeInvalidWorker ();
		assertEquals (500, CtrlFileUpload.file_upload (args).code, "Can't reach Worker");

		TestUtilConfig.makeInvalidMetadata ();
		assertEquals (500, CtrlFileUpload.file_upload (args).code, "Can't reach Metadata server");

		TestUtilConfig.makeInvalidAll ();
		assertEquals (500, CtrlFileUpload.file_upload (args).code, "Can't reach Auth");
	}

	@Test @Order (2) void downloadFile ()
	{

		// field validation fail

		ReqFile args = new ReqFile ();
		assertEquals (400, CtrlFileDownload.file_download (args).code, "Field validation failed");

		ResSession resL = CtrlAuthLogin.auth_login (new Credentials (State.username, "pass"));
		assertEquals (201, resL.code, "Login successfully");

		// auth fail

		args.token = "invalid token";
		args.fileUUID = State.fileUUID;
		assertEquals (401, CtrlFileDownload.file_download (args).code, "Authorization failed");

		// can't read

		args.token = resL.auth.token;
		args.fileUUID = UUID.randomUUID ();
		assertEquals (
			404, CtrlFileDownload.file_download (args).code, "Can't read file: Not found");

		// download success

		args.fileUUID = State.fileUUID;
		ResFileDownload resD = CtrlFileDownload.file_download (args);
		assertAll (
			"File downloaded",
			()
				-> assertEquals (200, resD.code, "Download success"),
			() -> assertEquals (State.fileSize, resD.fileContent.length, "Correct file size"));

		TestUtilConfig.makeInvalidWorker ();
		assertEquals (500, CtrlFileDownload.file_download (args).code, "Can't reach Worker");
	}
}
