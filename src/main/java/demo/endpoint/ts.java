package demo.endpoint;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

@Endpoint
public class ts {

	private static final String NAMESPACE_URI = "http://www.yourcompany.com/webservice";


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCountryRequest")                      
    public void handleHolidayRequest() throws Exception {
    	System.out.println("Hello boy");
    }

}
