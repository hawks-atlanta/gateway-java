package gateway;

import static org.junit.jupiter.api.Assertions.*;

import gateway.config.Config;
import gateway.controller.CtrlAccountRegister;
import gateway.controller.CtrlFileNewDir;
import gateway.controller.CtrlFileRename;
import gateway.controller.CtrlFileUpload;
import gateway.soap.request.Credentials;
import gateway.soap.request.ReqFileNewDir;
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
import gateway.controller.*;
import gateway.soap.request.*;
import gateway.soap.response.ResFileGet;

import gateway.controller.CtrlFileMove;

import gateway.services.ServiceAuth;
import gateway.services.ServiceMetadata;
import gateway.services.ServiceMetadata.ResSaveFile;

import gateway.soap.request.ReqFileMove;

import gateway.soap.response.ResStatus;

import java.util.Random;

@TestMethodOrder(OrderAnnotation.class)
class ITFileManagment {
	@BeforeEach
	void setup() {
		Config.initializeFromEnv();
	}

	@Test
	@Order(1)
	void renameFile() throws InterruptedException {
		// register

		ResSession resR = CtrlAccountRegister.account_register(
				new Credentials(UUID.randomUUID().toString(), "pass"));
		assertEquals(201, resR.code, "Register successful");

		// upload file

		ReqFileUpload reqU = new ReqFileUpload();
		reqU.fileContent = TestUtilGenerator.randomBytes(1);
		reqU.fileName = UUID.randomUUID().toString();
		reqU.location = null;
		reqU.token = resR.auth.token;

		ResFileNew resU = CtrlFileUpload.file_upload(reqU);
		assertEquals(201, resU.code, "File upload success");

		Thread.sleep(1000); // wait for upload

		// rename file

		ReqFileRename reqN = new ReqFileRename();
		reqN.token = resR.auth.token;
		reqN.newName = UUID.randomUUID().toString();
		reqN.fileUUID = resU.fileUUID;
		assertEquals(204, CtrlFileRename.file_rename(reqN).code, "Rename success");

		// errors

		assertEquals(
				409, CtrlFileRename.file_rename(reqN).code, "A file with that name already exists");

		TestUtilConfig.makeInvalidMetadata();
		assertEquals(500, CtrlFileRename.file_rename(reqN).code, "Can't reach Metadata server");

		reqN.token = "invalid-token";
		assertEquals(401, CtrlFileRename.file_rename(reqN).code, "Authorization failed");

		reqN.newName = null;
		assertEquals(400, CtrlFileRename.file_rename(reqN).code, "Field validation failed");
	}

	@Test
	@Order(2)
	void createSubdirectory() {
		// register

		ResSession resR = CtrlAccountRegister.account_register(
				new Credentials(UUID.randomUUID().toString(), "pass"));
		assertEquals(201, resR.code, "Register successfully");

		// create directory

		ReqFileNewDir reqD = new ReqFileNewDir();
		reqD.directoryName = UUID.randomUUID().toString();
		reqD.token = resR.auth.token;
		reqD.location = null;

		ResFileNew resD = CtrlFileNewDir.file_new_dir(reqD);
		assertEquals(201, resD.code, "Directory created");

		// create subdirectory

		reqD.location = resD.fileUUID;
		assertEquals(201, CtrlFileNewDir.file_new_dir(reqD).code, "Subdirectory created");

		// errors

		assertEquals(
				409, CtrlFileNewDir.file_new_dir(reqD).code, "File with the same name already exists");

		TestUtilConfig.makeInvalidMetadata();
		assertEquals(500, CtrlFileNewDir.file_new_dir(reqD).code, "Can't reach metadata");

		reqD.token = "invalid-token";
		assertEquals(401, CtrlFileNewDir.file_new_dir(reqD).code, "Unauthorized");

		reqD.directoryName = null;
		assertEquals(400, CtrlFileNewDir.file_new_dir(reqD).code, "Field validation failed");
	}

	@Test
	@Order(3)
	void getFileMetadata() throws InterruptedException {
		// register
		ResSession resR = CtrlAccountRegister.account_register(
				new Credentials(UUID.randomUUID().toString(), "pass"));
		assertEquals(201, resR.code, "Register successfully");

		// upload file
		ReqFileUpload reqU = new ReqFileUpload();
		reqU.fileContent = TestUtilGenerator.randomBytes(1);
		reqU.fileName = UUID.randomUUID().toString();
		reqU.location = null;
		reqU.token = resR.auth.token;

		ResFileNew resU = CtrlFileUpload.file_upload(reqU);
		assertEquals(201, resU.code, "File upload success");

		// wait for upload
		Thread.sleep(2_000);

		// get file metadata
		ReqFile reqF = new ReqFile();
		reqF.fileUUID = resU.fileUUID;
		reqF.token = resR.auth.token;

		ResFileGet resF = CtrlFileGet.file_get(reqF);

		// Check SOAP response fields
		assertEquals(200, resF.code, "Metadata retrieved successfully");
		assertFalse(resF.error, "There is no error in the SOAP response");

		// Check file metadata fields
		assertEquals(resU.fileUUID, resF.file.uuid, "File UUID matches");
		assertEquals(reqU.fileName, resF.file.name, "File name matches");
		assertTrue(resF.file.isFile, "File is an archive");

		// errors
		TestUtilConfig.makeInvalidMetadata();
		assertEquals(500, CtrlFileGet.file_get(reqF).code, "Can't reach metadata");

		reqF.token = "invalid-token";
		assertEquals(401, CtrlFileGet.file_get(reqF).code, "Unauthorized");

		reqF.fileUUID = null;
		assertEquals(400, CtrlFileGet.file_get(reqF).code, "Field validation failed");
	}

	@Test
	@Order(4)
	void MoveFile() {
		String username1 = UUID.randomUUID().toString();
		String username2 = UUID.randomUUID().toString();

		// register
		String tokenUser1 = registerAndLoginUserSuccess(username1);
		String tokenUser2 = registerAndLoginUserSuccess(username2);

		// create 2 files user1 in root, 1 file for user2
		ResSaveFile resSaveFile1 = createFile(tokenUser1, null, "filename");
		ResSaveFile resSaveFile2 = createFile(tokenUser2, null, "filename");
		createFile(tokenUser1, null, "filename_alt");

		// create a directory and a file on it
		ResSaveFile resSaveDirectory = ServiceMetadata.saveFile(
				UUID.fromString(ServiceAuth.tokenGetClaim(tokenUser1, "uuid")), null, false, null,
				"nested", 0);
		ResSaveFile resSaveFile3 = createFile(tokenUser1, resSaveDirectory.fileUUID, "filename_alt");

		// 204
		ReqFileMove reqFileMove = new ReqFileMove();
		reqFileMove.fileUUID = resSaveFile1.fileUUID;
		reqFileMove.targetDirectoryUUID = resSaveDirectory.fileUUID;
		reqFileMove.token = tokenUser1;
		ResStatus resStatus = CtrlFileMove.file_move(reqFileMove);
		assertEquals(204, resStatus.code, "The file have been moved");

		// 403
		reqFileMove.fileUUID = resSaveFile2.fileUUID;
		resStatus = CtrlFileMove.file_move(reqFileMove);
		assertEquals(403, resStatus.code, "The file is not owned by the user.");

		// 404
		reqFileMove.fileUUID = UUID.randomUUID();
		resStatus = CtrlFileMove.file_move(reqFileMove);
		assertEquals(
				404, resStatus.code, "Not found. No file with the given file_uuid was found.");

		// 409
		reqFileMove.fileUUID = resSaveFile3.fileUUID;
		reqFileMove.targetDirectoryUUID = null;
		resStatus = CtrlFileMove.file_move(reqFileMove);
		assertEquals(
				409, resStatus.code, "There is another file in the same folder with the same name.");

		// 500
		TestUtilConfig.makeInvalidMetadata();
		assertEquals(500, CtrlFileMove.file_move(reqFileMove).code, "Can't reach metadata");

		// 401
		reqFileMove.token = "token_invalid";
		resStatus = CtrlFileMove.file_move(reqFileMove);
		assertEquals(401, resStatus.code, "unauthorized");

		// 400
		reqFileMove.fileUUID = null;
		resStatus = CtrlFileMove.file_move(reqFileMove);
		assertEquals(
				400, resStatus.code,
				"The owner_uuid or file_uuid were not a valid UUID or the JSON body does't fullfill the validations.");
	}

	private String registerAndLoginUserSuccess(String username) {
		ResSession res = CtrlAccountRegister.account_register(new Credentials(username, "pass"));
		assertEquals(201, res.code, "User registered successfully");
		return res.auth.token;
	}

	private ResSaveFile createFile(String token, UUID directoryUUID, String filename) {
		UUID fileType = (directoryUUID != null) ? directoryUUID : null;
		return ServiceMetadata.saveFile(
				UUID.fromString(ServiceAuth.tokenGetClaim(token, "uuid")), fileType, true, "txt",
				filename, (new Random().nextInt(3000) + 1));
	}
}
