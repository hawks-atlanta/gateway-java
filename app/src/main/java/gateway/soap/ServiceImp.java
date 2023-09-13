package gateway.soap;

import gateway.soap.request.*;
import gateway.soap.response.*;
import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService (endpointInterface = "gateway.soap.Service") public class ServiceImp implements Service
{
	// auth

	@WebMethod public SessionRes login (Credentials credentials)
	{
		// TODO: Replace me
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
		// TODO: Replace me
		System.out.println ("---");
		System.out.println (args);
		System.out.println (args.token);
		System.out.println (args.fileName);
		System.out.println ("---");
		StatusRes s = new StatusRes ();
		s.success = true;
		s.message = "File created successfully";
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
