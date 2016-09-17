package nz.ac.auckland.parolee.test;

import static org.junit.Assert.fail;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple JUnit test to test the behaviour of the Parolee Web service.
 * 
 * The test basically uses the Web service to create new parolees, to query a
 * parolee, to query a range of parolees, to update a parolee and to delete a
 * parolee.
 * 
 * The test is implemented using the JAX-RS client API.
 * 
 * @author Ian Warren
 *
 */
public class ParoleeResourceTest {
	
	private static String WEB_SERVICE_URI = "http://localhost:10000/services/parolees";

	private Logger _logger = LoggerFactory.getLogger(ParoleeResourceTest.class);

	@Test
	@Ignore
	public void testParoleeResource() {
		// Use ClientBuilder to create a new client that can be used to create
		// connections to the Web service.
		Client client = ClientBuilder.newClient();
		
		try {
			// **************
			// *** CREATE ***
			// **************
			_logger.info("Creating Parolees ...");

			// An array of XML strings, describing parolees.
			String[] xmlPayloads = {
					"<parolee>" + "<first-name>Al</first-name>"
							+ "<last-name>Capone</last-name>"
							+ "<gender>Male</gender>"
							+ "<date-of-birth>17/01/1899</date-of-birth>"
							+ "</parolee>",

					"<parolee>" + "<first-name>John</first-name>"
							+ "<last-name>Gotti</last-name>"
							+ "<gender>Male</gender>"
							+ "<date-of-birth>27/10/1940</date-of-birth>"
							+ "</parolee>",

					"<parolee>" + "<first-name>Pablo</first-name>"
							+ "<last-name>Escobar</last-name>"
							+ "<gender>Male</gender>"
							+ "<date-of-birth>01/12/1949</date-of-birth>"
							+ "</parolee>",

					"<parolee>" + "<first-name>Carlos</first-name>"
							+ "<last-name>Marcello</last-name>"
							+ "<gender>Male</gender>"
							+ "<date-of-birth>6/2/1910</date-of-birth>"
							+ "</parolee>" };

			// Send HTTP POST messages, each with a message body containing the
			// XML payload, to the Web service.
			String location = null;

			for (String payload : xmlPayloads) {
				Response response = client.target(WEB_SERVICE_URI).request()
						.post(Entity.xml(payload));

				// Expect a HTTP 201 "Created" response from the Web service.
				int status = response.getStatus();
				if (status != 201) {
					_logger.error("Failed to create Parolee; Web service responded with: "
							+ status);
					fail();
				}

				// Extract location header from the HTTP response message. This
				// should specify the URI for the newly created parolee.
				location = response.getLocation().toString();
				_logger.info("URI for new Parolee: " + location);

				// Close the response, freeing resources associated with it. 
				response.close();
			}

			// ****************
			// *** RETRIEVE ***
			// ****************
			_logger.info("Retrieving Parolees ...");

			// Query the Web service for the last created parolee. Send a HTTP
			// GET request, and expect an XML String in the response.
			String paroleeAsXML = client.target(location).request()
					.get(String.class);
			_logger.info("Retrieved Parolee:\n" + paroleeAsXML);

			// Query the Web service for Parolees with IDs in the range 1..3.
			// Send a HTTP GET request, and expect an XML String describing the
			// parolees in the response.
			String paroleesAsXML = client
					.target(WEB_SERVICE_URI + "?start=1&size=3").request()
					.get(String.class);
			_logger.info("Retrieved Parolees:\n" + paroleesAsXML);

			// **************
			// *** Update ***
			// **************
			_logger.info("Updating parolee ...");

			// Create a XML representation of the first parolee, changing Al
			// Capone's gender.
			String updateParolee = "<parolee>" + "<first-name>Al</first-name>"
					+ "<last-name>Capone</last-name>"
					+ "<gender>Female</gender>"
					+ "<date-of-birth>17/01/1899</date-of-birth>"
					+ "</parolee>";

			// Send a HTTP PUT request to the Web service.
			Response response = client.target(WEB_SERVICE_URI + "/1").request()
					.put(Entity.xml(updateParolee));

			// Expect a HTTP 204 "No content" response from the Web service.
			int status = response.getStatus();
			if (status != 204) {
				_logger.error("Failed to update Parolee; Web service responded with: "
						+ status);
				fail();
			}
			response.close();

			// Finally, re-query the Parolee. The date-of-birth should have been
			// updated.
			_logger.info("Querying the updated Parolee ...");
			paroleeAsXML = client.target(WEB_SERVICE_URI + "/1").request()
					.get(String.class);
			_logger.info("Retrieved Parolee:\n" + paroleeAsXML);

			// **************
			// *** DELETE ***
			// **************
			_logger.info("Deleting parolee ...");
			// Send a HTTP DELETE request to the Web service.
			response = client.target(WEB_SERVICE_URI + "/1").request().delete();

			// Expect a HTTP 204 "No content" response from the Web service.
			status = response.getStatus();
			if (status != 204) {
				_logger.error("Failed to delete Parolee; Web service responded with: "
						+ status);
				fail();
			}

			// Finally, re-query the Web service for the deleted Parolee. The
			// response should be 404.
			response = client.target(WEB_SERVICE_URI + "/1").request().get();
			status = response.getStatus();
			if (status != 404) {
				_logger.error("Expecting a status code of 404 for querying a non-existent Parolee; Web service responded with: "
						+ status);
				fail();
			}

		} finally {
			// Release any connection resources.
			client.close();
		}
	}
}