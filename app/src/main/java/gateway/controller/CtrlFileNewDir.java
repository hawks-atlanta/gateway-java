package gateway.controller;

import gateway.services.ServiceAuth;
import gateway.services.ServiceMetadata;
import gateway.services.UtilValidator;
import gateway.soap.request.ReqFileNewDir;
import gateway.soap.response.ResFileNew;
import gateway.soap.response.ResStatus;
import java.util.UUID;

public class CtrlFileNewDir
{
	public static ResFileNew file_new_dir (ReqFileNewDir args)
	{
		ResFileNew s = new ResFileNew ();
		UUID userUUID;

		// validation

		ResStatus resValidate = UtilValidator.validate (args);
		if (resValidate.error) {
			return ResStatus.downCast (ResFileNew.class, resValidate);
		}

		ResStatus resAuth = ServiceAuth.authenticate (args.token);
		if (resAuth.error) {
			return ResStatus.downCast (ResFileNew.class, resAuth);
		}
		userUUID = UUID.fromString (ServiceAuth.tokenGetClaim (args.token, "uuid"));

		// create dir

		ServiceMetadata.ResSaveFile resMeta =
			ServiceMetadata.saveFile (userUUID, args.location, false, null, args.directoryName, 0);
		if (resMeta.error) {
			return ResStatus.downCast (ResFileNew.class, (ResStatus)resMeta);
		}

		s.fileUUID = resMeta.fileUUID;
		s.code = resMeta.code;
		s.error = false;
		s.msg = "Directory created";

		return s;
	}
}
