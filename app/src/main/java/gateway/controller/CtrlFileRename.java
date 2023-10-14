package gateway.controller;

import gateway.config.Config;
import gateway.services.ServiceAuth;
import gateway.services.UtilValidator;
import gateway.soap.request.ReqFileRename;
import gateway.soap.response.ResStatus;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.UUID;
import org.json.JSONObject;

public class CtrlFileRename
{
	public static ResStatus file_rename (ReqFileRename args)
	{
		ResStatus s = new ResStatus ();
		UUID userUUID;

		// validations

		ResStatus resValidate = UtilValidator.validate (args);
		if (resValidate.error) {
			return resValidate;
		}

		ResStatus resAuth = ServiceAuth.authenticate (args.token);
		if (resAuth.error) {
			return resAuth;
		}
		userUUID = UUID.fromString (ServiceAuth.tokenGetClaim (args.token, "uuid"));

		// rename

		JSONObject body = new JSONObject ();
		body.put ("name", args.newName);

		// post

		try {
			HttpResponse<String> res = HttpClient.newHttpClient ().send (
				HttpRequest.newBuilder ()
					.uri (URI.create (String.format (
						"%s/files/rename/%s/%s", Config.getMetadataBaseUrl (), userUUID.toString (),
						args.fileUUID.toString ())))
					.PUT (BodyPublishers.ofString (body.toString ()))
					.build (),
				HttpResponse.BodyHandlers.ofString ());

			// response
			s.code = res.statusCode ();
			s.error = true;

			if (s.code == 204) {
				s.error = false;
				s.msg = "Rename successful";
			} else {
				s.msg = new JSONObject (res.body ()).getString ("message");
			}
		} catch (Exception e) {
			e.printStackTrace ();
			s.code = 500;
			s.error = true;
			s.msg = "Internal server error. Try again later";
		}

		return s;
	}
}
