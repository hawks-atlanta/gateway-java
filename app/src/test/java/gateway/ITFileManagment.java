package gateway;

import static org.junit.jupiter.api.Assertions.*;

import gateway.config.Config;
import gateway.controller.CtrlAccountRegister;
import gateway.controller.CtrlFileNewDir;
import gateway.soap.request.Credentials;
import gateway.soap.request.ReqFileNewDir;
import gateway.soap.response.ResFileNew;
import gateway.soap.response.ResSession;
import gateway.testutils.TestUtilConfig;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder (OrderAnnotation.class) class ITFileManagment
{
	@BeforeEach void setup () { Config.initializeFromEnv (); }

	@Test @Order (1) void createSubdirectory ()
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
		assertEquals (201, resD.code, "Subdirectory created");

		// errors

		TestUtilConfig.makeInvalidMetadata ();
		assertEquals (500, CtrlFileNewDir.file_new_dir (reqD).code, "Can't reach metadata");

		reqD.token = "invalid-token";
		assertEquals (401, CtrlFileNewDir.file_new_dir (reqD).code, "Unauthorized");

		reqD.directoryName = null;
		assertEquals (400, CtrlFileNewDir.file_new_dir (reqD).code, "Field validation failed");
	}
}
