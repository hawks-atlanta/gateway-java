package gateway.testutils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gateway.controller.CtrlAccountRegister;
import gateway.services.ServiceAuth;
import gateway.services.ServiceMetadata;
import gateway.services.ServiceMetadata.ResSaveFile;
import gateway.soap.request.Credentials;
import gateway.soap.response.ResSession;
import java.util.Random;
import java.util.UUID;

public class TestUtilAuth
{
	public static String registerAndLoginUserSuccess (String username)
	{
		ResSession res = CtrlAccountRegister.account_register (new Credentials (username, "pass"));
		assertEquals (201, res.code, "User registered successfully");
		return res.auth.token;
	}

	public static ResSaveFile createFile (String token)
	{
		return ServiceMetadata.saveFile (
			UUID.fromString (ServiceAuth.tokenGetClaim (token, "uuid")), null, true, "txt",
			"filename_t", (new Random ().nextInt (3000) + 1));
	}

	public static ResSaveFile createFile (String token, UUID directoryUUID, String filename)
	{
		UUID fileType = (directoryUUID != null) ? directoryUUID : null;
		return ServiceMetadata.saveFile (
			UUID.fromString (ServiceAuth.tokenGetClaim (token, "uuid")), fileType, true, "txt",
			filename, (new Random ().nextInt (3000) + 1));
	}
}
