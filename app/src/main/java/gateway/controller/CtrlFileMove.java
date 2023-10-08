package gateway.controller;

import gateway.services.ServiceAuth;
import gateway.services.UtilValidator;
import gateway.soap.request.ReqFileMove;
import gateway.soap.response.ResStatus;

public class CtrlFileMove
{
	public static ResStatus file_move (ReqFileMove args)
	{
		ResStatus statusRes = new ResStatus ();

		// validation all fields
		ResStatus resValidate = UtilValidator.validate (args);
		if (resValidate.error) {
			return ResStatus.downCast (ResStatus.class, resValidate);
		}

		// validation auth
		ResStatus resAuth = ServiceAuth.authenticate (args.token);
		if (resAuth.error) {
			return ResStatus.downCast (ResStatus.class, resAuth);
		}

		return statusRes;
	}
}
