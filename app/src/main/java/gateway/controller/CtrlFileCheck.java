package gateway.controller;

import gateway.services.ServiceAuth;
import gateway.services.ServiceMetadata;
import gateway.services.UtilValidator;
import gateway.soap.request.ReqFile;
import gateway.soap.response.ResFileCheck;
import gateway.soap.response.ResStatus;

public class CtrlFileCheck
{
	public static ResFileCheck file_check (ReqFile args)
	{
		ResFileCheck s = new ResFileCheck ();

		// validations

		ResStatus resValidate = UtilValidator.validate (args);
		if (resValidate.error) {
			return ResStatus.downCast (ResFileCheck.class, resValidate);
		}

		ResStatus resAuth = ServiceAuth.authenticate (args.token);
		if (resAuth.error) {
			return ResStatus.downCast (ResFileCheck.class, resAuth);
		}

		// get metadata

		ServiceMetadata.ResFileMetadata resM = ServiceMetadata.getFileMetadata(args.fileUUID);
		s.code = resM.code;
		s.error = resM.error;
		s.msg = resM.msg;

		if (s.code == 200) {
			s.ready = true;
			s.msg = "File ready";
		}

		return s;
	}
}
