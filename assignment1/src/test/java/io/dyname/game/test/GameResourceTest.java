package io.dyname.game.test;

import static org.junit.Assert.fail;
import io.dynam.game.dto.CosmeticDTO;
import io.dynam.game.dto.MysteryBoxDTO;
import io.dynam.game.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.junit.Before;
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
	
	private static boolean setup = true;

	private Logger _logger = LoggerFactory.getLogger(GameResourceTest.class);
	
	@Test
	public void testGameResource() {
		// Use ClientBuilder to create a new client that can be used to create
		// connections to the Web service.
		Client client = ClientBuilder.newClient();

		try {

			_logger.info("Posting Users.....");

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
				if (status != 201) {
					_logger.error("Failed to create User; Web service responded with: "
							+ status);
					fail();
				}
				_logger.info("Successfully posted User: " + user.toString());
				response.close();
			}

			_logger.info("Getting Users......");

			for (UserDTO user : userList) {
				UserDTO foundUser = client.target(WEB_SERVICE_URI + "/user/" + user.getName())
						.request().get(UserDTO.class);
				user.setId(foundUser.getId());
				if (!foundUser.equals(user)) {
					_logger.error("Failed to return the expected User. Expected: "
							+ user.toString()
							+ ", Actual: "
							+ foundUser.toString());
					fail();
				}
			}
			
			_logger.info("Requesting all Users.....");
			
			if (true) {
				Response response = client.target(WEB_SERVICE_URI + "/users").request().get();
				_logger.info("Got all users" + response.readEntity(String.class));
			}
			
			_logger.info("Posting cosmetic items....");
			
			List<CosmeticDTO> cosmeticList = new ArrayList<CosmeticDTO>();
			cosmeticList.add(new CosmeticDTO("Mad Skills","skill_boost"));
			cosmeticList.add(new CosmeticDTO("Spitting Fire","fire_animator"));
			cosmeticList.add(new CosmeticDTO("Bars","unreal_music"));
			cosmeticList.add(new CosmeticDTO("Savage Banter","witty_humour"));
			cosmeticList.add(new CosmeticDTO("Shade","best_thrown"));
			for (CosmeticDTO cosmetic : cosmeticList) {
				_logger.info("Starting cosmetic post");
				Response response = client.target(WEB_SERVICE_URI + "/item/cosmetic")
						.request().post(Entity.xml(cosmetic));
				
				_logger.info("cosmetic posted");
				int status = response.getStatus();
				if (status != 201) {
					_logger.error("Failed to create User; Web service responded with: "
							+ status);
					fail();
				}
				_logger.info("Successfully posted Item: " + cosmetic.toString());
				response.close();
			}
			
			
			_logger.info("Requesting Cosmetic items...");
			for (CosmeticDTO cosmetic : cosmeticList) {
				CosmeticDTO foundCosmetic = client.target(WEB_SERVICE_URI + "/item/cosmetic/" + cosmetic.getName())
						.request().get(CosmeticDTO.class);
				cosmetic.setId(foundCosmetic.getId());
				if (!foundCosmetic.equals(cosmetic)) {
					_logger.error("Failed to return the expected User. Expected: "
							+ cosmetic.toString()
							+ ", Actual: "
							+ foundCosmetic.toString());
					fail();
				}
			}
			
			
			_logger.info("Posting mystery box.....");
			
			List<MysteryBoxDTO> mysteryBoxList = new ArrayList<MysteryBoxDTO>();
			mysteryBoxList.add(new MysteryBoxDTO("Meme Box 2k16"));
			mysteryBoxList.add(new MysteryBoxDTO("Team Rocket Starter Pack"));
			mysteryBoxList.add(new MysteryBoxDTO("What u kno bout it"));
			
			for (MysteryBoxDTO mysteryBox: mysteryBoxList) {
				Response response = client.target(WEB_SERVICE_URI + "/item/mysterybox")
						.request().post(Entity.xml(mysteryBox));
				int status = response.getStatus();
				if (status != 201) {
					_logger.error("Failed to create User; Web service responded with: "
							+ status);
					fail();
				}
				_logger.info("Successfully posted Mystery Box: " + mysteryBox.toString());
				response.close();
			}
			
			_logger.info("Requesting Mystery Boxes items...");
			
			for (MysteryBoxDTO mysteryBox : mysteryBoxList) {
				MysteryBoxDTO foundMysteryBox = client.target(WEB_SERVICE_URI + "/item/mysterybox/" + mysteryBox.getName())
						.request().get(MysteryBoxDTO.class);
				mysteryBox.setId(foundMysteryBox.getId());
				if (!foundMysteryBox.equals(mysteryBox)) {
					_logger.error("Failed to return the expected User. Expected: "
							+ mysteryBox.toString()
							+ ", Actual: "
							+ foundMysteryBox.toString());
					fail();
				}
			}
			
			_logger.info("Posting cosmetics to Mystery Boxes.....");
			for (CosmeticDTO dtoCosmetic: cosmeticList) {
				Response response = client.target(WEB_SERVICE_URI + "/item/mysterybox/" + mysteryBoxList.get(0).getName())
						.request().post(Entity.xml(dtoCosmetic));
				int status = response.getStatus();
				if (status != 201) {
					_logger.error("Failed to create User; Web service responded with: "
							+ status);
					fail();
				}
				_logger.info("Successfully posted Cosmetic: " + dtoCosmetic.toString() + " to MysteryBox: " + mysteryBoxList.get(0).toString());
				response.close();
			}
			
			_logger.info("Getting contents of MysteryBox......");
			if (true) {
				Response response = client.target(WEB_SERVICE_URI + "/item/mysterybox/" + mysteryBoxList.get(0).getName() + "/contents").request().get();
				_logger.info("Got all contents in MysteryBox" + response.readEntity(String.class));
			}
			
			_logger.info("Getting Single contents of MysteryBox......");
			if (true) {
				Response response = client.target(WEB_SERVICE_URI + "/item/mysterybox/" + mysteryBoxList.get(0).getName() + "/contents?search=Mad_Skills").request().get();
				_logger.info("Got Single contents in MysteryBox" + response.readEntity(String.class));
			}
			
			
			_logger.info("Posting items to User .... ");
			for (CosmeticDTO dtoCosmetic: cosmeticList) {
				Response response = client.target(WEB_SERVICE_URI + "/user/" + userList.get(0).getName() + "/inventory")
						.request().post(Entity.xml(dtoCosmetic));
				int status = response.getStatus();
				if (status != 201) {
					_logger.error("Failed to create User; Web service responded with: "
							+ status);
					fail();
				}
				_logger.info("Successfully posted Cosmetic: " + dtoCosmetic.toString() + " to user: " + userList.get(0).getName());
				response.close();
			}
			
			_logger.info("Getting User inventory .... ");
			
			if (true) {
				Response response = client.target(WEB_SERVICE_URI + "/user/" + userList.get(0).getName() + "/inventory").request().get();
				_logger.info("Got the users inventory" + response.readEntity(String.class));
			}
			

		} finally {
			// Release any connection resources.
			client.close();
		}
	}
}
