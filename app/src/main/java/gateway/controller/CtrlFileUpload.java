package gateway.controller;

import capyfile.rmi.IWorkerService;
import capyfile.rmi.UploadFileArgs;
import gateway.config.Config;
import gateway.services.ServiceAuth;
import gateway.services.ServiceMetadata;
import gateway.services.ServiceWorker;
import gateway.services.UtilValidator;
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

		// validations

		ResStatus resValidate = UtilValidator.validate (args);
		if (resValidate.error) {
			return ResStatus.downCast (ResFileNew.class, resValidate);
		}

		if (args.fileContent.length == 0) {
			s.code = 400;
			s.error = true;
			s.msg = "{\"fileContent: must not be empty\"}";
			return s;
		} else if (args.fileContent.length > Config.MAX_FILE_SIZE) {
			s.code = 413; // payload too large
			s.error = true;
			s.msg = "File is too large";
			return s;
		}

		ResSession resAuth = ServiceAuth.authenticate (args.token);
		if (resAuth.error) {
			return ResStatus.downCast (ResFileNew.class, resAuth);
		}
		userUUID = UUID.fromString (ServiceAuth.tokenGetClaim (args.token, "uuid"));

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
			return ResStatus.downCast (ResFileNew.class, (ResStatus)resMeta);
		}
		s.fileUUID = resMeta.fileUUID;

		// send to worker for writting

		try {
			IWorkerService server = ServiceWorker.getServer ();
			UploadFileArgs queryUpload = new UploadFileArgs (s.fileUUID, args.fileContent);
			server.uploadFile (queryUpload);

			s.code = 201;
			s.error = false;
			s.msg = "Your file is being uploaded";
		} catch (Exception e) {
			System.err.println ("Can't connect to RMI");
			System.err.println (e);

			s.code = 500;
			s.error = true;
			s.msg = "Internal error, try again later";
		}

		return s;
	}
}
