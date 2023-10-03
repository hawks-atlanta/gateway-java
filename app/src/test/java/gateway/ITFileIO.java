package gateway;

import static org.junit.jupiter.api.Assertions.*;

import gateway.config.Config;
import gateway.controller.CtrlAccountRegister;
import gateway.controller.CtrlFileCheck;
import gateway.controller.CtrlFileUpload;
import gateway.soap.request.Credentials;
import gateway.soap.request.ReqFileUpload;
import gateway.soap.request.ReqFile;
import gateway.soap.response.ResSession;
import gateway.soap.response.ResFileNew;
import gateway.testutils.TestUtilConfig;
import gateway.testutils.TestUtilGenerator;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ITFileIO
{
	@BeforeEach void setup () { Config.initializeFromEnv (); }

	@Test void uploadFile ()
	{
		// register

		ResSession res = CtrlAccountRegister.account_register (
			new Credentials (UUID.randomUUID ().toString (), "pass"));
		String token = res.auth.token;
		assertEquals (201, res.code, "Login successfully");

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

		args.fileContent = TestUtilGenerator.randomBytes (1);
		assertEquals (201, CtrlFileUpload.file_upload (args).code, "File upload success");

		args.fileName = UUID.randomUUID ().toString ();
		TestUtilConfig.makeInvalidWorker ();
		assertEquals (500, CtrlFileUpload.file_upload (args).code, "Can't reach Worker");

		TestUtilConfig.makeInvalidMetadata ();
		assertEquals (500, CtrlFileUpload.file_upload (args).code, "Can't reach Metadata server");

		TestUtilConfig.makeInvalidAll ();
		assertEquals (500, CtrlFileUpload.file_upload (args).code, "Can't reach Auth");
	}

	@Test void file_check () throws InterruptedException {

		UUID smallFile;
		UUID bigFile;
		
		// register

		ResSession resR = CtrlAccountRegister.account_register (
			new Credentials (UUID.randomUUID ().toString (), "pass"));
		String token = resR.auth.token;
		assertEquals (201, resR.code, "Login successfully");

		// upload small file that takes little time

		ReqFileUpload reqU = new ReqFileUpload ();
		reqU.fileName = UUID.randomUUID ().toString ();
		reqU.location = null;
		reqU.token = token;

		reqU.fileContent = TestUtilGenerator.randomBytes (16);
		ResFileNew resU = CtrlFileUpload.file_upload (reqU);
		smallFile = resU.fileUUID;

		assertEquals (201, resU.code, "Small file upload success");

		// upload large file so takes longer

		reqU.fileContent = TestUtilGenerator.randomBytes (100000001);
		resU = CtrlFileUpload.file_upload (reqU);
		bigFile = resU.fileUUID;

		assertEquals (201, resU.code, "Big file upload success");

		// 202 not ready

		ReqFile reqC = new ReqFile();
		reqC.fileUUID = bigFile;
		assertEquals (202, CtrlFileCheck.file_check(reqC).code, "File not ready");

		// 200 ready

		Thread.sleep(2_000); // wait till is fully uploaded
		reqC.fileUUID = smallFile;
		assertEquals (200, CtrlFileCheck.file_check(reqC).code, "File ready");

	}
}
