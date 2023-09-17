package gateway.soap;

import gateway.soap.request.*;
import gateway.soap.response.*;
import capyfile.rmi.interfaces.*;
import gateway.rmiclient.ManagerRMI;
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

	@WebMethod public SessionRes register(Credentials credentials) { return new SessionRes (); }

	@WebMethod public StatusRes updatePassword (UpdatePasswordReq parameters)
	{
		return new StatusRes ();
	}

	// file system

	@WebMethod public StatusRes createFile (CreateFileReq args)
	{
		StatusRes s = new StatusRes ();

		// TODO: authenticate

		String uuid = java.util.UUID.randomUUID().toString();

		try {
		
			// TODO: Merge dev branch
			IWorkerService server = ManagerRMI.getServer();

			UploadFileArgs queryUpload = new UploadFileArgs (uuid, args.fileContent);
			server.uploadFile (queryUpload);

			s.success = true;
			s.message = "Your file is being uploaded";
		}
		catch (Exception e)
		{
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
