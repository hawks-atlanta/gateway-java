package gateway;

import capyfile.rmi.interfaces.*;
import gateway.config.Config;
import gateway.soap.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.xml.ws.Endpoint;

public class App
{
	public static void main (String[] args)
	{
		// init config from env vars
		Config.initializeFromEnv ();

		// consume RMI
		try {
			// get service

			System.out.println ("Gateway: RMI demo");
			Registry registry =
				LocateRegistry.getRegistry (Config.getWorkerHost (), Config.getWorkerPort ());
			IWorkerService server = (IWorkerService)registry.lookup ("WorkerService");

			// TODO: Replace me
			// example

			File queryUpload = new File ("----", null);
			server.uploadFile (queryUpload);

			FileDownload queryDownload = new FileDownload ("----");
			File resultFile = server.downloadFile (queryDownload);

			System.out.println ("Successfully communicated with worker RMI");

		} catch (Exception e) {
			System.err.println (e);
		}

		System.out.println ("Gateway: Starting SOAP");

		// serve SOAP
		Endpoint.publish ("http://0.0.0.0:8080/service", new ServiceImp ());
	}
}
