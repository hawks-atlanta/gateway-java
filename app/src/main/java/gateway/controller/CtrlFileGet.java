package gateway.controller;

import gateway.services.ServiceAuth;
import gateway.services.ServiceMetadata;
import gateway.services.UtilValidator;
import gateway.soap.request.ReqFile;
import gateway.soap.response.File;
import gateway.soap.response.ResFileGet;
import gateway.soap.response.ResStatus;
import java.util.UUID;

public class CtrlFileGet
{
	public static ResFileGet file_get (ReqFile args)
	{
		ResFileGet s = new ResFileGet ();

		// validations
		ResStatus resValidate = UtilValidator.validate (args);
		if (resValidate.error) {
			return ResStatus.downCast (ResFileGet.class, resValidate);
		}

		// Check if user is authenticated
		ResStatus resAuth = ServiceAuth.authenticate (args.token);
		if (resAuth.error) {
			return ResStatus.downCast (ResFileGet.class, resAuth);
		}

		// Check if user can read the file
		UUID userUUID = UUID.fromString (ServiceAuth.tokenGetClaim (args.token, "uuid"));
		ResStatus resCanRead = ServiceMetadata.canRead (userUUID, args.fileUUID);
		if (resCanRead.error) {
			return ResStatus.downCast (ResFileGet.class, resCanRead);
		}

		// get metadata
		ServiceMetadata.ResFileMetadata resM = ServiceMetadata.getFileMetadata (args.fileUUID);
		s.code = resM.code;
		s.error = resM.error;
		s.msg = resM.msg;

		if (s.code == 200) {
			File file = new File ();
			file.uuid = args.fileUUID;
			file.name = resM.name;
			file.isFile = resM.volume != null;

			s.file = file;
			s.msg = "File metadata has been retrieved";
		}

		return s;
	}
}
