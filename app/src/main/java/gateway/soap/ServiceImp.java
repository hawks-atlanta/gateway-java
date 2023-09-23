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
		// Create a new ResSession object to hold the response data
		ResSession res = new ResSession ();

		// Define the URL for the authentication request
		String url = Config.getAuthBaseUrl () + "/login";

		// Create a JSONObject to hold the request body data.
		JSONObject requestBody = new JSONObject ();

		// Add the username and password fields obtained from the credentials object to the JSON
		// request body
		requestBody.put ("username", credentials.username);
		requestBody.put ("password", credentials.password);

		try {
			HttpResponse<String> response = HttpClient.newHttpClient ().send (
				HttpRequest.newBuilder ()
					.uri (URI.create (url))
					.POST (BodyPublishers.ofString (requestBody.toString ()))
					.uri (URI.create (url))
					.header ("Content-Type", "application/json")
					.build (),
				HttpResponse.BodyHandlers.ofString ());

			// Create a JSONObject to hold the response body received from the HTTP request.
			JSONObject jsonObject = new JSONObject (response.body ());

			// Get the HTTP status code from the response.
			int statusCode = response.statusCode ();

			// Check if the response status code is 201 (Login succeed)
			if (statusCode == 201) {
				// If the status code is 201, indicating login succeed, initialize an Authorization
				// object in the response and extract the JWT token.
				res.auth = new Authorization ();
				res.error = false;
				res.auth.token = jsonObject.getString ("jwt");
			} else {
				// If the status code is different from 201, indicating an error response, extract
				// success status and message from the JSON object.
				res.error = true;
				res.msg = jsonObject.getString ("msg");
			}

		} catch (IOException | InterruptedException e) {
			// Handle exceptions such as IOException and InterruptedException, if they occur.
			e.printStackTrace ();
		}

		// Return the res object containing the response data.
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

			HttpResponse<String> response = HttpClient.newHttpClient ().send (
				HttpRequest.newBuilder ()
					.uri (URI.create (url))
					.POST (BodyPublishers.ofString (requestBody.toString ()))
					.uri (URI.create (url))
					.header ("Content-Type", "application/json")
					.build (),
				HttpResponse.BodyHandlers.ofString ());

			// Response
			JSONObject jsonObject = new JSONObject (response.body ());
			int statusCode = response.statusCode ();

			if (statusCode == 201) {
				res.auth = new Authorization ();
				res.error = false;
				res.auth.token = jsonObject.getString ("jwt");
			} else {
				res.error = true;
				res.msg = jsonObject.getString ("msg");
			}

		} catch (IOException | InterruptedException e) {
			e.printStackTrace ();
		}

		return res;
	}

	@WebMethod public ResStatus account_password (ReqAccPassword parameters) { return null; }

	// file system

	@WebMethod public ResFileList file_list (ReqFileList args) { return null; }

	@WebMethod public ResFileNew file_upload (ReqFileUpload args)
	{
		ResFileNew s = new ResFileNew ();
		String mimetype = "";
		UUID userUUID;
		UUID fileUUID;

		// check size

		if (args.fileContent.length == 0) {
			s.error = true;
			s.msg = "File is empty";
			return s;
		} else if (args.fileContent.length > Config.MAX_FILE_SIZE) {
			s.error = true;
			s.msg = "File is too large";
			return s;
		}

		ResStatus authRes = ManagerAuth.authenticate (args.token);
		if (authRes.error) {
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
			s.error = true;
			// msg is set in fileUUID
			return s;
		}
		s.fileUUID = fileUUID;

		// store file

		try {
			IWorkerService server = ManagerRMI.getServer ();
			UploadFileArgs queryUpload =
				new UploadFileArgs (fileUUID.toString (), args.fileContent);
			server.uploadFile (queryUpload);

			s.error = false;
			s.msg = "Your file is being uploaded";
		} catch (Exception e) {
			e.printStackTrace ();
			s.error = true;
			s.msg = "Internal error, try again later";
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
