package gateway;

import static org.junit.jupiter.api.Assertions.*;

import gateway.config.Config;
import gateway.controller.*;
import gateway.soap.request.*;
import gateway.soap.response.ResFileGet;
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

	@Test @Order (2) void createSubdirectory ()
	{
		// register

		ResSession resR = CtrlAccountRegister.account_register (
			new Credentials (UUID.randomUUID ().toString (), "pass"));
		assertEquals (201, resR.code, "Register successfully");

		// create directory

		ReqFileNewDir reqD = new ReqFileNewDir ();
		reqD.directoryName = UUID.randomUUID ().toString ();
		reqD.token = resR.auth.token;
		reqD.location = null;

		ResFileNew resD = CtrlFileNewDir.file_new_dir (reqD);
		assertEquals (201, resD.code, "Directory created");

		// create subdirectory

		reqD.location = resD.fileUUID;
		assertEquals (201, CtrlFileNewDir.file_new_dir (reqD).code, "Subdirectory created");

		// errors

		assertEquals (
			409, CtrlFileNewDir.file_new_dir (reqD).code, "File with the same name already exists");

		TestUtilConfig.makeInvalidMetadata ();
		assertEquals (500, CtrlFileNewDir.file_new_dir (reqD).code, "Can't reach metadata");

		reqD.token = "invalid-token";
		assertEquals (401, CtrlFileNewDir.file_new_dir (reqD).code, "Unauthorized");

		reqD.directoryName = null;
		assertEquals (400, CtrlFileNewDir.file_new_dir (reqD).code, "Field validation failed");
	}

	@Test @Order (3) void getFileMetadata () throws InterruptedException
	{
		// register
		ResSession resR = CtrlAccountRegister.account_register (
			new Credentials (UUID.randomUUID ().toString (), "pass"));
		assertEquals (201, resR.code, "Register successfully");

		// upload file
		ReqFileUpload reqU = new ReqFileUpload ();
		reqU.fileContent = TestUtilGenerator.randomBytes (1);
		reqU.fileName = UUID.randomUUID ().toString ();
		reqU.location = null;
		reqU.token = resR.auth.token;

		ResFileNew resU = CtrlFileUpload.file_upload (reqU);
		assertEquals (201, resU.code, "File upload success");

		// wait for upload
		Thread.sleep (2_000);

		// get file metadata
		ReqFile reqF = new ReqFile ();
		reqF.fileUUID = resU.fileUUID;
		reqF.token = resR.auth.token;

		ResFileGet resF = CtrlFileGet.file_get (reqF);

		// Check SOAP response fields
		assertEquals (200, resF.code, "Metadata retrieved successfully");
		assertFalse (resF.error, "There is no error in the SOAP response");

		// Check file metadata fields
		assertEquals (resU.fileUUID, resF.file.uuid, "File UUID matches");
		assertEquals (reqU.fileName, resF.file.name, "File name matches");
		assertTrue (resF.file.isFile, "File is an archive");

		// errors
		TestUtilConfig.makeInvalidMetadata ();
		assertEquals (500, CtrlFileGet.file_get (reqF).code, "Can't reach metadata");

		reqF.token = "invalid-token";
		assertEquals (401, CtrlFileGet.file_get (reqF).code, "Unauthorized");

		reqF.fileUUID = null;
		assertEquals (400, CtrlFileGet.file_get (reqF).code, "Field validation failed");
	}
}
