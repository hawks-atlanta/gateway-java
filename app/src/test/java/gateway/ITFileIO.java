package gateway;

import static org.junit.jupiter.api.Assertions.*;

import gateway.config.Config;
import gateway.controller.CtrlAccountRegister;
import gateway.controller.CtrlAuthLogin;
import gateway.controller.CtrlFileCheck;
import gateway.controller.CtrlFileDownload;
import gateway.controller.CtrlFileNewDir;
import gateway.controller.CtrlFileUpload;
import gateway.soap.request.Credentials;
import gateway.soap.request.ReqFile;
import gateway.soap.request.ReqFileNewDir;
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

	@Test @Order (1) void uploadFile () throws InterruptedException
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

		// wait until file it's ready
		Thread.sleep (2_000);

		args.fileName = UUID.randomUUID ().toString ();
		TestUtilConfig.makeInvalidWorker ();
		assertEquals (500, CtrlFileUpload.file_upload (args).code, "Can't reach Worker");

		TestUtilConfig.makeInvalidMetadata ();
		assertEquals (500, CtrlFileUpload.file_upload (args).code, "Can't reach Metadata server");

		TestUtilConfig.makeInvalidAll ();
		assertEquals (500, CtrlFileUpload.file_upload (args).code, "Can't reach Auth");
	}

	@Test @Order (3) void uploadFileOnDirectory ()
	{

		ResSession resL = CtrlAuthLogin.auth_login (new Credentials (State.username, "pass"));
		assertEquals (201, resL.code, "Login successfully");

		// create three level deep subdirectory

		ReqFileNewDir reqD = new ReqFileNewDir ();
		reqD.directoryName = UUID.randomUUID ().toString ();
		reqD.token = resL.auth.token;
		reqD.location = null;

		ResFileNew resD = CtrlFileNewDir.file_new_dir (reqD);
		assertEquals (201, resD.code, "Subdirectory #1 created");

		reqD.location = resD.fileUUID;
		resD = CtrlFileNewDir.file_new_dir (reqD);
		assertEquals (201, resD.code, "Subdirectory #2 created");

		reqD.location = resD.fileUUID;
		resD = CtrlFileNewDir.file_new_dir (reqD);
		assertEquals (201, resD.code, "Subdirectory #3 created");

		// upload file on directory

		ReqFileUpload args = new ReqFileUpload ();
		args.fileName = UUID.randomUUID ().toString ();
		args.fileContent = TestUtilGenerator.randomBytes (1);
		args.location = resD.fileUUID;
		args.token = resL.auth.token;

		ResFileNew resU = CtrlFileUpload.file_upload (args);
		assertEquals (201, resU.code, "File upload on subdirectory success");
	}

	@Test @Order (2) void fileCheck () throws InterruptedException
	{
		UUID smallFile;
		UUID bigFile;
		String token;

		// register

		ResSession resR = CtrlAccountRegister.account_register (
			new Credentials (UUID.randomUUID ().toString (), "pass"));
		assertEquals (201, resR.code, "Login successfully");
		token = resR.auth.token;

		// upload small file

		ReqFileUpload reqU = new ReqFileUpload ();
		reqU.location = null;
		reqU.token = token;

		reqU.fileName = UUID.randomUUID ().toString ();
		reqU.fileContent = TestUtilGenerator.randomBytes (16);
		ResFileNew resU = CtrlFileUpload.file_upload (reqU);

		assertEquals (201, resU.code, "Small file upload success");
		smallFile = resU.fileUUID;

		// upload large file that takes longer

		reqU.fileName = UUID.randomUUID ().toString ();
		reqU.fileContent = TestUtilGenerator.randomBytes (99999999);
		resU = CtrlFileUpload.file_upload (reqU);

		assertEquals (201, resU.code, "Big file upload success");
		bigFile = resU.fileUUID;

		// 202 not ready

		ReqFile reqC = new ReqFile ();
		reqC.token = token;
		reqC.fileUUID = bigFile;
		assertEquals (202, CtrlFileCheck.file_check (reqC).code, "File not ready");

		// 200 ready

		Thread.sleep (2_000); // wait till is fully uploaded
		reqC.fileUUID = smallFile;
		assertEquals (200, CtrlFileCheck.file_check (reqC).code, "File ready");

		reqC.token = "invalid token";
		assertEquals (401, CtrlFileCheck.file_check (reqC).code, "Auth failed");

		reqC.fileUUID = null;
		assertEquals (400, CtrlFileCheck.file_check (reqC).code, "Wrong fields");
	}

	@Test @Order (3) void downloadFile ()
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
