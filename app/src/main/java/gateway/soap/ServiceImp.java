package gateway.soap;

import capyfile.rmi.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
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

@WebService(endpointInterface = "gateway.soap.Service")
public class ServiceImp implements Service {
	// auth

	@WebMethod
	public SessionRes login(Credentials credentials) {
		// TODO: Replace me

		// Example handle JSON

		String string_res = "{\"succeed\":false,\"msg\":\"example message\"}";
		JSONObject json_res = new JSONObject(string_res);

		System.out.println("---");
		System.out.println(json_res.getBoolean("succeed"));
		System.out.println(json_res.getString("msg"));

		// Example return type

		SessionRes res = new SessionRes();
		res.auth = new Authorization();
		res.auth.token = "sample-token-for-" + credentials.username;
		res.success = true;
		res.message = "Successfully logged in";

		return res;
	}

	// user register in auth service, returns an access token
	@WebMethod
	public SessionRes register(Credentials credentials) {
		SessionRes res = new SessionRes();

		String url = Config.getAuthBaseUrl() + "/register";

		// Request
		JSONObject requestBody = new JSONObject();
		requestBody.put("username", credentials.username);
		requestBody.put("password", credentials.password);

		try {
			// Configs and make HTTP POST request to user register.
			HttpClient client = HttpClient.newHttpClient();

			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.POST(BodyPublishers.ofString(requestBody.toString()))
					.uri(URI.create(url))
					.header("Content-Type", "application/json")
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			// Response
			JSONObject jsonObject = new JSONObject(response.body());
			int statusCode = response.statusCode();

			if (statusCode == 201) {
				res.auth = new Authorization();
				res.success = true;
				res.auth.token = jsonObject.getString("jwt");
			} else {
				res.success = jsonObject.getBoolean("succeed");
				res.message = jsonObject.getString("msg");
			}

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return res;
	}

	@WebMethod
	public StatusRes updatePassword(UpdatePasswordReq parameters) {
		return new StatusRes();
	}

	// file system

	@WebMethod
	public StatusRes createFile(CreateFileReq args) {
		StatusRes s = new StatusRes();
		String mimetype = "";
		UUID userUUID;
		UUID fileUUID;

		StatusRes authRes = ManagerAuth.authenticate(args.token);
		if (!authRes.success) {
			return authRes;
		}

		userUUID = UUID.fromString(JWT.decode(args.token).getClaim("uuid").asString());

		// mimetype from bytes

		try {
			InputStream is = new ByteArrayInputStream(args.fileContent);
			mimetype = URLConnection.guessContentTypeFromStream(is);
		} catch (Exception e) {
			System.err.println("Couldn't determine mimetype. Continuing");
		}

		fileUUID = ManagerMetadata.saveFile(
				s, args.token, userUUID, args.location, mimetype, args.fileName,
				args.fileContent.length);

		if (fileUUID == null) {
			s.success = false;
			return s;
		}

		// store file

		try {
			IWorkerService server = ManagerRMI.getServer();
			UploadFileArgs queryUpload = new UploadFileArgs(fileUUID.toString(), args.fileContent);
			server.uploadFile(queryUpload);

			s.success = true;
			s.message = "Your file is being uploaded";
		} catch (Exception e) {
			e.printStackTrace();
			s.success = false;
			s.message = "Internal error, try again later";
		}

		return s;
	}

	@WebMethod
	public StatusRes createDirectory(CreateDirectoryReq args) {
		return new StatusRes();
	}

	@WebMethod
	public StatusRes deleteFile(DeleteFileReq args) {
		return new StatusRes();
	}

	@WebMethod
	public ListFileRes listFiles(ListFileReq args) {
		return null;
	}

	@WebMethod
	public DownloadFileRes downloadFile(DownloadFileReq args) {
		return null;
	}

	@WebMethod
	public StatusRes moveFile(MoveFileReq args) {
		return new StatusRes();
	}

	// sharing

	@WebMethod
	public StatusRes shareWith(ShareWithReq args) {
		StatusRes statusRes = new StatusRes();

		try {
			// authenticate user
			StatusRes authRes = ManagerAuth.authenticate(args.token);
			if (!authRes.success) {
				return authRes;
			}

			// obtain uuid from user and otheruser
			UUID userUUID = UUID.fromString(JWT.decode(args.token).getClaim("uuid").asString());

			UUID otherUserUUID = ManagerAuth.getUserUUID(args.token, args.otherUsername);

			JSONObject requestBody = new JSONObject();
			requestBody.put("otherUserUUID", otherUserUUID);

			// request to share file with otheruser
			String url = Config.getMetadataBaseUrl() + "/api/v1/files/share/" + userUUID + "/" +
					args.fileUUID;
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.POST(BodyPublishers.ofString(requestBody.toString()))
					.uri(URI.create(url))
					.header("Content-Type", "application/json")
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			// Response
			JSONObject jsonObject = new JSONObject(response.body());
			int statusCode = response.statusCode();

			if (statusCode == 204) {
				statusRes.success = true;
				statusRes.message = "The file have been shared";
			} else {
				System.out.println(statusCode);
				statusRes.success = !jsonObject.getBoolean("error");
				statusRes.message = jsonObject.getString("message");
			}
		} catch (Exception e) {
			System.err.println(e);
			statusRes.success = false;
			statusRes.message = "Internal error, try again later";
			return statusRes;
		}

		return statusRes;
	}

	@WebMethod
	public StatusRes unShareWith(UnShareWithReq args) {
		return new StatusRes();
	}

	@WebMethod
	public SharedWithWhoRes sharedWithWho(SharedWithWhoReq args) {
		return null;
	}

	@WebMethod
	public ListSharedWithMeRes listSharedWithMe(Authorization auth) {
		return null;
	}
}
