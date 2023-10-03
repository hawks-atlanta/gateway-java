package gateway.controller;

import gateway.config.Config;
import gateway.services.ServiceAuth;
import gateway.services.UtilValidator;
import gateway.soap.request.ReqFile;
import gateway.soap.response.ResFileCheck;
import gateway.soap.response.ResStatus;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

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

		try {
			HttpResponse<String> response = HttpClient.newHttpClient ().send (
				HttpRequest.newBuilder ()
					.uri (URI.create (String.format (
						"%s/files/metadata/%s", Config.getMetadataBaseUrl (),
						args.fileUUID.toString ())))
					.GET ()
					.header ("Content-Type", "application/json")
					.build (),
				HttpResponse.BodyHandlers.ofString ());
			s.code = response.statusCode ();

			if (s.code == 200) {
				s.ready = true;
				s.error = false;
				s.msg = "File ready";
			} else {
				s.ready = false;
				s.error = true;
				s.msg = new JSONObject (response.body ()).getString ("message");
			}

		} catch (Exception e) {
			s.code = 500;
			s.error = true;
			s.msg = "Internal error, try again later";
		}

		return s;
	}
}
