package gateway.controller;

import capyfile.rmi.IWorkerService;
import gateway.services.ServiceAuth;
import gateway.services.ServiceMetadata;
import gateway.services.ServiceWorker;
import gateway.services.UtilValidator;
import gateway.soap.request.ReqFile;
import gateway.soap.response.ResFileDownload;
import gateway.soap.response.ResSession;
import gateway.soap.response.ResStatus;
import java.io.FileNotFoundException;
import java.util.UUID;

public class CtrlFileDownload
{
	public static ResFileDownload file_download (ReqFile args)
	{
		ResFileDownload s = new ResFileDownload ();
		UUID userUUID;

		ResStatus resValidate = UtilValidator.validate (args);
		if (resValidate.error) {
			return ResStatus.downCast (ResFileDownload.class, resValidate);
		}

		// auth

		ResSession resAuth = ServiceAuth.authenticate (args.token);
		if (resAuth.error) {
			return ResStatus.downCast (ResFileDownload.class, resAuth);
		}
		userUUID = UUID.fromString (ServiceAuth.tokenGetClaimStr (args.token, "uuid"));

		// file exists & user has access

		ResStatus resRead = ServiceMetadata.canRead (userUUID, args.fileUUID);
		if (resRead.error) {
			return ResStatus.downCast (ResFileDownload.class, resRead);
		}

		// which volume

		ServiceMetadata.ResFileMetadata resMeta = ServiceMetadata.getFileMetadata (args.fileUUID);
		if (resMeta.error) {
			return ResStatus.downCast (ResFileDownload.class, resMeta);
		}

		// try download it from worker

		try {
			IWorkerService server = ServiceWorker.getServer ();
			s.fileContent = ServiceWorker.downloadFile (server, args.fileUUID, resMeta.volume);
			s.fileName = resMeta.name;
			s.fileUUID = args.fileUUID;

			s.code = 200;
			s.error = false;
			s.msg = "";
		} catch (Exception e) {

			System.err.println (e);
			if (e instanceof FileNotFoundException) {
				System.err.println ("File not found");
				s.code = 404;
				s.msg = "File not found";

				// NOTE: At this point the metadata service claims a file
				// exists in this volume. But it wasn't found.
			} else {
				System.err.println ("Can't connect to RMI");
				s.code = 500;
				s.msg = "Internal error, try again later";
			}

			s.error = true;
		}

		return s;
	}
}
