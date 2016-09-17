package io.dyname.game.test;

import static org.junit.Assert.fail;
import io.dynam.game.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

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
public class GameResourceTest {

	private static String WEB_SERVICE_URI = "http://localhost:10000/services/game";

	private Logger _logger = LoggerFactory.getLogger(GameResourceTest.class);

	@Test
	public void testGameResource() {
		// Use ClientBuilder to create a new client that can be used to create
		// connections to the Web service.
		Client client = ClientBuilder.newClient();

		try {

			_logger.info("Posting Users");

			List<UserDTO> userList = new ArrayList<UserDTO>();
			userList.add(new UserDTO("Arran", "pass1"));
			userList.add(new UserDTO("Asheer", "pass1"));
			userList.add(new UserDTO("Ben", "rekruined"));
			userList.add(new UserDTO("Michael", "pls"));
			userList.add(new UserDTO("Harsh", "temroket"));

			for (UserDTO user : userList) {
				Response response = client.target(WEB_SERVICE_URI + "/user")
						.request().post(Entity.xml(user));
				int status = response.getStatus();
				if (status != 200) {
					_logger.error("Failed to create User; Web service responded with: "
							+ status);
					fail();
				}
				_logger.info("Successfully posted User: " + user.toString());
				response.close();
			}

			_logger.info("Getting Users");

			for (UserDTO user : userList) {
				NewCookie cookie = new NewCookie("user", user.getName());
				UserDTO foundUser = client.target(WEB_SERVICE_URI + "/user")
						.request().cookie(cookie).get(UserDTO.class);
				user.setId(foundUser.getId());
				if (!foundUser.equals(user)) {
					_logger.error("Failed to return the expected User. Expected: "
							+ user.toString()
							+ ", Actual: "
							+ foundUser.toString());
					fail();
				}
			}
		} finally {
			// Release any connection resources.
			client.close();
		}
	}
}
