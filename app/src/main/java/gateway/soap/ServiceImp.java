package gateway.soap;

import capyfile.rmi.interfaces.*;
import gateway.config.Config;
import gateway.rmiclient.ManagerRMI;
import gateway.soap.request.*;
import gateway.soap.response.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import javax.jws.WebMethod;
import javax.jws.WebService;
import org.json.JSONObject;

@WebService (endpointInterface = "gateway.soap.Service") public class ServiceImp implements Service
{
	// auth

	@WebMethod public SessionRes login (Credentials credentials)
	{
		// TODO: Replace me

		// Example handle JSON

		String string_res = "{\"succeed\":false,\"msg\":\"example message\"}";
		JSONObject json_res = new JSONObject (string_res);

		System.out.println ("---");
		System.out.println (json_res.getBoolean ("succeed"));
		System.out.println (json_res.getString ("msg"));

		// Example return type

		SessionRes res = new SessionRes ();
		res.auth = new Authorization ();
		res.auth.token = "sample-token-for-" + credentials.username;
		res.success = true;
		res.message = "Successfully logged in";

		return res;
	}

	// user register in auth service, returns an access token
	@WebMethod public SessionRes register(Credentials credentials)
	{
		SessionRes res = new SessionRes ();

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

	@WebMethod public StatusRes updatePassword (UpdatePasswordReq parameters)
	{
		return new StatusRes ();
	}

	// file system

	@WebMethod public StatusRes createFile (CreateFileReq args)
	{
		StatusRes s = new StatusRes ();

		// TODO: authenticate

		String uuid = java.util.UUID.randomUUID ().toString ();

		try {

			// TODO: Merge dev branch
			IWorkerService server = ManagerRMI.getServer ();

			UploadFileArgs queryUpload = new UploadFileArgs (uuid, args.fileContent);
			server.uploadFile (queryUpload);

			s.success = true;
			s.message = "Your file is being uploaded";
		} catch (Exception e) {
			System.err.println (e);

			s.success = false;
			s.message = "Internal error, try again later";
		}

		return s;
	}

	@WebMethod public StatusRes createDirectory (CreateDirectoryReq args)
	{
		return new StatusRes ();
	}

	@WebMethod public StatusRes deleteFile (DeleteFileReq args) { return new StatusRes (); }

	@WebMethod public ListFileRes listFiles (ListFileReq args) { return null; }

	@WebMethod public DownloadFileRes downloadFile (DownloadFileReq args) { return null; }

	@WebMethod public StatusRes moveFile (MoveFileReq args) { return new StatusRes (); }

	// sharing

	@WebMethod public StatusRes shareWith (ShareWithReq args) { return new StatusRes (); }

	@WebMethod public StatusRes unShareWith (UnShareWithReq args) { return new StatusRes (); }

	@WebMethod public SharedWithWhoRes sharedWithWho (SharedWithWhoReq args) { return null; }

	@WebMethod public ListSharedWithMeRes listSharedWithMe (Authorization auth) { return null; }
}
