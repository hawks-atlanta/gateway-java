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

		ResStatus authRes = ServiceAuth.authenticate (args.token);
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

		fileUUID = ServiceMetadata.saveFile (
			s, args.token, userUUID, args.location, mimetype, args.fileName,
			args.fileContent.length);

		if (fileUUID == null) {
			s.error = true;
			// s.msg is set in ServiceMetadata.saveFile
			return s;
		}
		s.fileUUID = fileUUID;

		// store file

		try {
			IWorkerService server = ServiceWorker.getServer ();
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
}
