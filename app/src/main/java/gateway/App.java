package gateway;

import javax.xml.ws.Endpoint;

public class App
{
	public String getGreeting () { return "Starting gateway..."; }

	public static void main (String[] args)
	{
		System.out.println (new App ().getGreeting ());

		Endpoint.publish (
			"http://0.0.0.0:8080/sampleservice", new SampleServiceImp ());
	}
}
