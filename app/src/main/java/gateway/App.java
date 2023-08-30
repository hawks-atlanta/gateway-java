package gateway;

import capyfile.rmi.interfaces.*;
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
		System.out.println ("Gateway: RMI demo");

		// consume RMI
		try {
			// get service

			Registry registry = LocateRegistry.getRegistry (1900);
			MessengerService server = (MessengerService)registry.lookup ("MessengerService");

			// send message

			Message query = new Message ("Hi");
			Message response = server.sendMessage (query);
			System.out.println (response.content);

		} catch (Exception e) {
			System.err.println (e);
		}

		System.out.println ("Gateway: Starting SOAP");

		// serve SOAP
		Endpoint.publish ("http://0.0.0.0:8080/sampleservice", new ServiceImp ());
	}
}
