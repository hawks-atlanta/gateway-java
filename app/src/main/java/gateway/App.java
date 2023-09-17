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

		// serve SOAP
		System.out.println ("Gateway: Starting SOAP");
		Endpoint.publish ("http://0.0.0.0:8080/service", new ServiceImp ());
	}
}
