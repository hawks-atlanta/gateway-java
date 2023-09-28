package gateway.controller;

import capyfile.rmi.IWorkerService;
import gateway.services.ServiceAuth;
import gateway.services.ServiceMetadata;
import gateway.services.ServiceWorker;
import gateway.services.UtilValidator;
import gateway.soap.request.ReqFile;
import gateway.soap.response.ResFileDownload;
import gateway.soap.response.ResStatus;
import java.util.UUID;

public class CtrlFileDownload
{
	public static ResFileDownload file_download (ReqFile args)
	{
		ResFileDownload s = new ResFileDownload ();
		UUID userUUID;

		System.err.println (args.fileUUID);

		ResStatus resValidate = UtilValidator.validate (args);
		if (resValidate.error) {
			return ResStatus.downCast (ResFileDownload.class, resValidate);
		}
		System.err.println ("resValidate");

		// auth

		ResStatus resAuth = ServiceAuth.authenticate (args.token);
		if (resAuth.error) {
			return ResStatus.downCast (ResFileDownload.class, resAuth);
		}
		userUUID = UUID.fromString (ServiceAuth.tokenGetClaim (args.token, "uuid"));
		System.err.println ("resAuth");

		// file exists & user has access

		ResStatus resRead = ServiceMetadata.canRead (userUUID, args.fileUUID);
		if (resRead.error) {
			return ResStatus.downCast (ResFileDownload.class, resRead);
		}
		System.err.println ("resRead");

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
			System.err.println ("Can't connect to RMI");
			e.printStackTrace ();

			s.code = 500;
			s.error = true;
			s.msg = "Internal error, try again later";
		}

		return s;
	}
}
