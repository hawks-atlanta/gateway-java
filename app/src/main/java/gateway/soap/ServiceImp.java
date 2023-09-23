package gateway.soap;

import capyfile.rmi.*;
import com.auth0.jwt.JWT;
import gateway.config.Config;
import gateway.soap.request.*;
import gateway.soap.response.*;
import gateway.utils.ManagerAuth;
import gateway.utils.ManagerMetadata;
import gateway.utils.ManagerRMI;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Exception;
import java.net.URI;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.UUID;
import javax.jws.WebMethod;
import javax.jws.WebService;
import org.json.JSONObject;

@WebService (endpointInterface = "gateway.soap.Service") public class ServiceImp implements Service
{
	// auth

	@WebMethod public ResSession auth_login (Credentials credentials)
	{
		// TODO: Replace me

		// Example handle JSON

		String string_res = "{\"succeed\":false,\"msg\":\"example message\"}";
		JSONObject json_res = new JSONObject (string_res);

		System.out.println ("---");
		System.out.println (json_res.getBoolean ("succeed"));
		System.out.println (json_res.getString ("msg"));

		// Example return type

		ResSession res = new ResSession ();
		res.auth = new Authorization ();
		res.auth.token = "sample-token-for-" + credentials.username;
		res.success = true;
		res.message = "Successfully logged in";

		return res;
	}

	@WebMethod public ResSession auth_refresh (Authorization auth) { return null; }

	// user register in auth service, returns an access token
	@WebMethod public ResSession account_register (Credentials credentials)
	{
		ResSession res = new ResSession ();

		String url = Config.getAuthBaseUrl () + "/register";

		// Request
		JSONObject requestBody = new JSONObject ();
		requestBody.put ("username", credentials.username);
		requestBody.put ("password", credentials.password);

		try {
			// Configs and make HTTP POST request to user register.
			HttpClient client = HttpClient.newHttpClient ();

			HttpRequest request = HttpRequest.newBuilder ()
									  .uri (URI.create (url))
									  .POST (BodyPublishers.ofString (requestBody.toString ()))
									  .uri (URI.create (url))
									  .header ("Content-Type", "application/json")
									  .build ();

			HttpResponse<String> response =
				client.send (request, HttpResponse.BodyHandlers.ofString ());

			// Response
			JSONObject jsonObject = new JSONObject (response.body ());
			int statusCode = response.statusCode ();

			if (statusCode == 201) {
				res.auth = new Authorization ();
				res.success = true;
				res.auth.token = jsonObject.getString ("jwt");
			} else {
				res.success = jsonObject.getBoolean ("succeed");
				res.message = jsonObject.getString ("msg");
			}

		} catch (IOException | InterruptedException e) {
			e.printStackTrace ();
		}

		return res;
	}

	@WebMethod public ResStatus account_password (ReqAccPassword parameters)
	{
		return new ResStatus ();
	}

	// file system

	@WebMethod public ResFileList file_list (ReqFileList args) { return null; }

	@WebMethod public ResFileNew file_upload (ReqFileUpload args)
	{
		ResFileNew s = new ResFileNew ();
		String mimetype = "";
		UUID userUUID;
		UUID fileUUID;

		ResStatus authRes = ManagerAuth.authenticate (args.token);
		if (!authRes.success) {
			return (ResFileNew)authRes;
		}

		userUUID = UUID.fromString (JWT.decode (args.token).getClaim ("uuid").asString ());

		// mimetype from bytes

		try {
			InputStream is = new ByteArrayInputStream (args.fileContent);
			mimetype = URLConnection.guessContentTypeFromStream (is);
		} catch (Exception e) {
			System.err.println ("Couldn't determine mimetype. Continuing");
		}

		fileUUID = ManagerMetadata.saveFile (
			s, args.token, userUUID, args.location, mimetype, args.fileName,
			args.fileContent.length);

		if (fileUUID == null) {
			s.success = false;
			return s;
		}

		// store file

		try {
			IWorkerService server = ManagerRMI.getServer ();
			UploadFileArgs queryUpload =
				new UploadFileArgs (fileUUID.toString (), args.fileContent);
			server.uploadFile (queryUpload);

			s.success = true;
			s.message = "Your file is being uploaded";
		} catch (Exception e) {
			e.printStackTrace ();
			s.success = false;
			s.message = "Internal error, try again later";
		}

		return s;
	}

	@WebMethod public ResFileNew file_new_dir (ReqFileNewDir args) { return null; }

	@WebMethod public ResFileCheck file_check (ReqFile args) { return null; }

	@WebMethod public ResStatus file_delete (ReqFileDelete args) { return new ResStatus (); }

	@WebMethod public ResFileDownload file_download (ReqFile args) { return null; }

	@WebMethod public ResStatus file_move (ReqFileMove args) { return new ResStatus (); }

	// sharing

	@WebMethod public ResStatus share_file (ReqShareFile args) { return new ResStatus (); }

	@WebMethod public ResStatus share_remove (ReqShareRemove args) { return new ResStatus (); }

	@WebMethod public ResShareList share_list (Authorization auth) { return null; }

	@WebMethod public ResShareListWithWho share_list_with_who (ReqFile args) { return null; }
}
