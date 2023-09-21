package gateway.utils;

import capyfile.rmi.IWorkerService;
import gateway.config.Config;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ManagerRMI
{
	// TODO: Use worker pool
	public static IWorkerService getServer () throws Exception
	{
		try {
			Registry registry =
				LocateRegistry.getRegistry (Config.getWorkerHost (), Config.getWorkerPort ());
			IWorkerService server = (IWorkerService)registry.lookup ("WorkerService");

			return server;
		} catch (Exception e) {
			throw e;
		}
	}
}
