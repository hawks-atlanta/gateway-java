package gateway.controller;

import capyfile.rmi.IWorkerService;
import capyfile.rmi.UploadFileArgs;
import com.auth0.jwt.JWT;
import gateway.config.Config;
import gateway.services.ServiceAuth;
import gateway.services.ServiceMetadata;
import gateway.services.ServiceWorker;
import gateway.soap.request.*;
import gateway.soap.response.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.UUID;

public class CtrlFileUpload
{
	public static ResFileNew file_upload (ReqFileUpload args)
	{
		ResFileNew s = new ResFileNew ();
		String mimetype = "";
		UUID userUUID;

		// check size

		if (args.fileContent.length == 0) {
			s.code = 400;
			s.error = true;
			s.msg = "File is empty";
			return s;
		} else if (args.fileContent.length > Config.MAX_FILE_SIZE) {
			s.code = 413; // payload too large
			s.error = true;
			s.msg = "File is too large";
			return s;
		}

		ResStatus resAuth = ServiceAuth.authenticate (args.token);
		if (resAuth.error) {
			return (ResFileNew)resAuth;
		}

		userUUID = UUID.fromString (JWT.decode (args.token).getClaim ("uuid").asString ());

		// mimetype from bytes

		try {
			InputStream is = new ByteArrayInputStream (args.fileContent);
			mimetype = URLConnection.guessContentTypeFromStream (is);
		} catch (Exception e) {
			System.err.println ("Couldn't determine mimetype. Continuing");
		}

		ServiceMetadata.ResSaveFile resMeta = ServiceMetadata.saveFile (
			userUUID, args.location, mimetype, args.fileName, args.fileContent.length);
		if (resMeta.error) {
			return (ResFileNew)((ResStatus)resMeta);
		}

		s.fileUUID = resMeta.fileUUID;

		// send to worker for writting

		try {
			IWorkerService server = ServiceWorker.getServer ();
			UploadFileArgs queryUpload =
				new UploadFileArgs (s.fileUUID.toString (), args.fileContent);
			server.uploadFile (queryUpload);

			s.code = 201;
			s.error = false;
			s.msg = "Your file is being uploaded";
		} catch (Exception e) {
			e.printStackTrace ();
			s.code = 500;
			s.error = true;
			s.msg = "Internal error, try again later";
		}

		return s;
	}
}