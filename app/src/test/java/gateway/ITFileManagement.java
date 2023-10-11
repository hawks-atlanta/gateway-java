package gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gateway.config.Config;
import gateway.controller.CtrlAccountRegister;
import gateway.controller.CtrlFileMove;
import gateway.services.ServiceAuth;
import gateway.services.ServiceMetadata;
import gateway.services.ServiceMetadata.ResSaveFile;
import gateway.soap.request.Credentials;
import gateway.soap.request.ReqFileMove;
import gateway.soap.response.ResSession;
import gateway.soap.response.ResStatus;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ITFileManagement
{

	@BeforeEach void setup () { Config.initializeFromEnv (); }

	@Test void MoveFile ()
	{
		String username1 = UUID.randomUUID ().toString ();
		String username2 = UUID.randomUUID ().toString ();

		// register
		String tokenUser1 = registerAndLoginUserSuccess (username1);
		String tokenUser2 = registerAndLoginUserSuccess (username2);

		// create a file and aux file
		ResSaveFile resSaveFile1 = createFile (tokenUser1);
		ResSaveFile resSaveFile2 = createFile (tokenUser2);

		// create a directory
		ResSaveFile resSaveDirectory = ServiceMetadata.saveFile (
			UUID.fromString (ServiceAuth.tokenGetClaim (tokenUser1, "uuid")), null, "directory",
			"nested", 0);

		// 204
		ReqFileMove reqFileMove = new ReqFileMove ();
		reqFileMove.fileUUID = resSaveFile1.fileUUID;
		reqFileMove.targetDirectoryUUID = resSaveDirectory.fileUUID;
		reqFileMove.newName = "";
		reqFileMove.token = tokenUser1;
		ResStatus resStatus = CtrlFileMove.file_move (reqFileMove);
		assertEquals (204, resStatus.code, "The file have been moved");

		// 403
		reqFileMove.fileUUID = resSaveFile2.fileUUID;
		resStatus = CtrlFileMove.file_move (reqFileMove);
		assertEquals (403, resStatus.code, "The file is not owned by the user.");

		// 404
		reqFileMove.fileUUID = UUID.randomUUID ();
		resStatus = CtrlFileMove.file_move (reqFileMove);
		assertEquals (
			404, resStatus.code, "Not found. No file with the given file_uuid was found.");

		// 409

		// 500
	}

	// TODO UTILS?
	private String registerAndLoginUserSuccess (String username)
	{
		ResSession res = CtrlAccountRegister.account_register (new Credentials (username, "pass"));
		assertEquals (201, res.code, "User registered successfully");
		return res.auth.token;
	}

	private ResSaveFile createFile (String token)
	{
		return ServiceMetadata.saveFile (
			UUID.fromString (ServiceAuth.tokenGetClaim (token, "uuid")), null, "txt", "filename_t",
			(new Random ().nextInt (3000) + 1));
	}
}
