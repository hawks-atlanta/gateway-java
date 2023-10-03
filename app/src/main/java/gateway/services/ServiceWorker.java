package gateway.services;

import capyfile.rmi.DownloadFileArgs;
import capyfile.rmi.DownloadFileRes;
import capyfile.rmi.IWorkerService;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import gateway.config.Config;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

public class ServiceWorker
{
	public static IWorkerService getServer () throws Exception
	{
		Registry registry =
			LocateRegistry.getRegistry (Config.getWorkerHost (), Config.getWorkerPort ());
		IWorkerService server = (IWorkerService)registry.lookup ("WorkerService");

		return server;
	}

	public static byte[] downloadFile (IWorkerService worker, UUID fileUUID, int volume)
		throws Exception
	{
		InputStream istream = null;

		try {
			// get data stream

			DownloadFileRes res = worker.downloadFile (new DownloadFileArgs (fileUUID, volume));
			istream = RemoteInputStreamClient.wrap (res.stream);
			byte[] bytes = new byte[(int)res.size];

			// copy data from stream by chunks

			byte[] buf = new byte[102400]; // 100KB
			int bytesPos = 0;
			int bytesRead = 0;

			while ((bytesRead = istream.read (buf)) >= 0) {
				System.arraycopy (buf, 0, bytes, bytesPos, bytesRead);
				bytesPos += bytesRead;
			}

			return bytes;
		} finally {
			if (istream != null) {
				istream.close ();
			}
		}
	}
}
