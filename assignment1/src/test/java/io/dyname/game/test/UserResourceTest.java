package io.dyname.game.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import io.dynam.game.domain.Cosmetic;
import io.dynam.game.dto.CosmeticDTO;
import io.dynam.game.dto.ItemDTO;
import io.dynam.game.dto.MysteryBoxDTO;
import io.dynam.game.dto.ServerDTO;
import io.dynam.game.dto.UserDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
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
public class UserResourceTest {

	private static String WEB_SERVICE_URI = "http://localhost:10000/services/game";
	
	private Logger _logger = LoggerFactory.getLogger(UserResourceTest.class);
	
	private static Client _client = ClientBuilder.newClient();
	
	@Before
	public void cleanDatabase() {
		_client = ClientBuilder.newClient();
		Response response = _client.target(WEB_SERVICE_URI).request().delete();
		int status = response.getStatus();
		if (status != 200) {
			_logger.error("Failed to clean database; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		_client.close();
		_client = ClientBuilder.newClient();
	}
	
	@After
	public void closeConnection() {
		_client.close();
	}
	
	@Test
	public void testSingleInventoryPostAndDelete() {
		_logger.info("Testing single post and get to user inventory...");
		_logger.info("[1] Posting User...");
		UserDTO dtoUser = new UserDTO("Arran","Password");
		Response response = _client.target(WEB_SERVICE_URI + "/user").request().post(Entity.xml(dtoUser));
		int status = response.getStatus();
		if (status != 201) {
			_logger.error("Failed to post User; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		_logger.info("[2] Posting Item...");
		CosmeticDTO dtoCosmetic = new CosmeticDTO("Test", "Test_asset_name");
		response = _client.target(WEB_SERVICE_URI + "/item/cosmetic").request().post(Entity.xml(dtoCosmetic));
		status = response.getStatus();
		if (status != 201) {
			_logger.error("Failed to post Item; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		
		_logger.info("[3] Posting Item to Inventory....");
		ItemDTO dtoItem = new ItemDTO(dtoCosmetic);
		response = _client.target(WEB_SERVICE_URI + "/user/" + dtoUser.getName() + "/inventory").request().post(Entity.xml(dtoItem));
		status = response.getStatus();
		if (status != 201) {
			_logger.error("Failed to post Item to inventory; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		//<base-uri>/user/{name}/inventory
		_logger.info("[4] Deleting Item from Inventory....");
		response = _client.target(WEB_SERVICE_URI + "/user/" + dtoUser.getName() +"/inventory/" + dtoCosmetic.getName())
				.request().delete();
		status = response.getStatus();
		if (status != 200) {
			_logger.error("Failed to delete Item from inventory; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		
		
		_logger.info("[5] Geting Item from Inventory...");
		ItemDTO item = null;
		try {
			item = _client.target(WEB_SERVICE_URI + "/user/" + dtoUser.getName() +"/inventory/" + dtoCosmetic.getName())
					.request().get(ItemDTO.class);
		} catch (WebApplicationException e) {
			assertEquals(404,e.getResponse().getStatus());
		}
		if (item != null) {
			fail();
		}
	}
	
	@Test
	public void testInventoryMultiPostAndDeleteAll() {
		_logger.info("Testing mutli post and delete all to user inventory...");
		_logger.info("[1] Posting User...");
		UserDTO dtoUser = new UserDTO("Arran","Password");
		Response response = _client.target(WEB_SERVICE_URI + "/user").request().post(Entity.xml(dtoUser));
		int status = response.getStatus();
		if (status != 201) {
			_logger.error("Failed to post User; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		_logger.info("[2] Posting Items...");
		List<CosmeticDTO> dtoCosmeticList = new ArrayList<CosmeticDTO>();
		dtoCosmeticList.add( new CosmeticDTO("Test", "Test_asset_name"));
		dtoCosmeticList.add( new CosmeticDTO("Test2", "Test_asset_name2"));
		dtoCosmeticList.add( new CosmeticDTO("Test3", "Test_asset_name3"));
		for (CosmeticDTO dtoCosmetic: dtoCosmeticList) {
			response = _client.target(WEB_SERVICE_URI + "/item/cosmetic").request().post(Entity.xml(dtoCosmetic));
			status = response.getStatus();
			if (status != 201) {
				_logger.error("Failed to post Item; Web service responded with: "
						+ status);
				fail();
			}
			response.close();
		}
		_logger.info("[3] Posting Item to Inventory....");
		for (CosmeticDTO dtoCosmetic: dtoCosmeticList) {
			ItemDTO dtoItem = new ItemDTO(dtoCosmetic);
			response = _client.target(WEB_SERVICE_URI + "/user/" + dtoUser.getName() + "/inventory").request().post(Entity.xml(dtoItem));
			status = response.getStatus();
			if (status != 201) {
				_logger.error("Failed to post Item to inventory; Web service responded with: "
						+ status);
				fail();
			}
			response.close();
		}
		
		_logger.info("[4] Delete all from Inventory....");
		response = _client.target(WEB_SERVICE_URI + "/user/" + dtoUser.getName() +"/inventory")
				.request().delete();
		status = response.getStatus();
		if (status != 200) {
			_logger.error("Failed to delete all items from inventory; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		
		
		_logger.info("[5] Geting Items from Inventory...");
		for (CosmeticDTO dtoCosmetic : dtoCosmeticList) {
			ItemDTO item = null;
			try {
				item = _client.target(WEB_SERVICE_URI + "/user/" + dtoUser.getName() +"/inventory/" + dtoCosmetic.getName())
						.request().get(ItemDTO.class);
			} catch (WebApplicationException e) {
				assertEquals(404,e.getResponse().getStatus());
			}
			if (item != null) {
				fail();
			}
		}
	}
	
	@Test
	public void testSingleInventoryPost() {
		_logger.info("Testing single post and get to user inventory...");
		_logger.info("[1] Posting User...");
		UserDTO dtoUser = new UserDTO("Arran","Password");
		Response response = _client.target(WEB_SERVICE_URI + "/user").request().post(Entity.json(dtoUser));
		int status = response.getStatus();
		if (status != 201) {
			_logger.error("Failed to post User; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		_logger.info("[2] Posting Item...");
		CosmeticDTO dtoCosmetic = new CosmeticDTO("Test", "Test_asset_name");
		_logger.info(">>>" + dtoCosmetic.toString());
		response = _client.target(WEB_SERVICE_URI + "/item/cosmetic").request().post(Entity.json(dtoCosmetic));
		status = response.getStatus();
		if (status != 201) {
			_logger.error("Failed to post Item; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		_logger.info("[3] Posting Item to Inventory....");
		_logger.info(">>>" + dtoCosmetic.toString());
		ItemDTO dtoItem = new ItemDTO(dtoCosmetic);
		_logger.info(">>>" + dtoItem.toString());
		response =_client.target(WEB_SERVICE_URI + "/user/" + dtoUser.getName() + "/inventory").request().post(Entity.xml(dtoItem));
		status = response.getStatus();
		if (status != 201) {
			_logger.error("Failed to post Item to inventory; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		
		_logger.info("[4] Geting Item from Inventory...");
		ItemDTO foundItem = _client.target(WEB_SERVICE_URI + "/user/" + dtoUser.getName() +"/inventory/" + dtoCosmetic.getName())
				.request().get(ItemDTO.class);
		assertEquals(null, foundItem.getMysteryBox());
		CosmeticDTO foundCosmetic = foundItem.getCosmetic();
		assertEquals(dtoCosmetic.getName(),foundCosmetic.getName());
		assertEquals(dtoCosmetic.getInternalName(),foundCosmetic.getInternalName());
	}
	
	@Test
	public void testPostMysteryBoxToInventory() {
		_logger.info("Testing post and get of mysterybox to inventory...");
		_logger.info("[1] Posting User...");
		UserDTO dtoUser = new UserDTO("Arran","Password");
		Response response = _client.target(WEB_SERVICE_URI + "/user").request().post(Entity.xml(dtoUser));
		int status = response.getStatus();
		if (status != 201) {
			_logger.error("Failed to post User; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		
		_logger.info("[2] Posting MysteryBox...");
		MysteryBoxDTO dtoMysteryBox = new MysteryBoxDTO("Test",new HashSet<Cosmetic>());
	
		response = _client.target(WEB_SERVICE_URI + "/item/mysterybox").request().post(Entity.xml(dtoMysteryBox));
		status = response.getStatus();
		if (status != 201) {
			_logger.error("Failed to post MysteryBox; Web service responded with: "
					+ status); 
			fail();
		}
		response.close();
		
		_logger.info("[3] Posting MysteryBox to Inventory....");
		_logger.info(">>>" + dtoMysteryBox.toString());
		ItemDTO dtoItem = new ItemDTO(dtoMysteryBox);
		response =_client.target(WEB_SERVICE_URI + "/user/" + dtoUser.getName() + "/inventory").request().post(Entity.xml(dtoItem));
		status = response.getStatus();
		if (status != 201) {
			_logger.error("Failed to post MysteryBox to inventory; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		
		_logger.info("[4] Geting Item from Inventory...");
		ItemDTO foundItem = _client.target(WEB_SERVICE_URI + "/user/" + dtoUser.getName() +"/inventory/" + dtoMysteryBox.getName())
				.request().get(ItemDTO.class);
		assertEquals(null, foundItem.getCosmetic());
		MysteryBoxDTO foundMysteryBox = foundItem.getMysteryBox();
		assertEquals(dtoMysteryBox.getName(),foundMysteryBox.getName());
		assertEquals(dtoMysteryBox.getContentDTO(),foundMysteryBox.getContentDTO());	
	}
	
	@Test
	public void testUserSinglePostGet() {
		_logger.info("Testing single user post and get....");
		_logger.info("[1] Posting...");
		UserDTO dtoUser = new UserDTO("Arran","Password");
		Response response = _client.target(WEB_SERVICE_URI + "/user").request().post(Entity.xml(dtoUser));
		int status = response.getStatus();
		if (status != 201) {
			_logger.error("Failed to post User; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		_logger.info("[2] Getting...");
		UserDTO foundUser = _client.target(WEB_SERVICE_URI + "/user/Arran")
				.request().get(UserDTO.class);
		
		assertEquals(dtoUser.getName(),foundUser.getName());
		assertEquals(dtoUser.getPassword(),foundUser.getPassword());
	}

	@Test
	public void testUserMultiGet() {
		_logger.info("Testing Multiple user post and get....");
		_logger.info("[1] Posting...");
		List<UserDTO> dtoUserList = new ArrayList<UserDTO>();
		dtoUserList.add(new UserDTO("Arran","Password"));
		dtoUserList.add(new UserDTO("Jeff","Password"));
		dtoUserList.add(new UserDTO("Bill","nopassword"));
		for (UserDTO dtoUser : dtoUserList) {
			Response response = _client.target(WEB_SERVICE_URI + "/user").request().post(Entity.xml(dtoUser));
			int status = response.getStatus();
			if (status != 201) {
				_logger.error("Failed to post User; Web service responded with: "
						+ status);
				fail();
			}
			response.close();
		}
		_logger.info("[2] Getting...");
		List<UserDTO> foundUserList = _client.target(WEB_SERVICE_URI + "/users")
				.request().get(new GenericType<List<UserDTO>>(){});
		for (int i = 0; i < dtoUserList.size(); i++) {
			UserDTO expected = dtoUserList.get(i);
			UserDTO actual = foundUserList.get(i);
			assertEquals(expected.getName(),actual.getName());
			assertEquals(expected.getPassword(),actual.getPassword());
		}
	}
	
	@Test
	public void testSingleUserPost() {
		_logger.info("Testing Single user post, put and get....");
		_logger.info("[1] Posting...");
		UserDTO dtoUser = new UserDTO("Arran","Password");
		Response response = _client.target(WEB_SERVICE_URI + "/user").request().post(Entity.xml(dtoUser));
		int status = response.getStatus();
		if (status != 201) {
			_logger.error("Failed to post User; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		_logger.info("[2] Putting...");
		dtoUser.setPassword("NewPassword");
		response = _client.target(WEB_SERVICE_URI + "/user/Arran").request().put(Entity.xml(dtoUser));
		status = response.getStatus();
		if (status != 200) {
			_logger.error("Failed to update User; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		
		_logger.info("[3] Getting...");
		UserDTO foundUser = _client.target(WEB_SERVICE_URI + "/user/Arran")
				.request().get(UserDTO.class);
		assertEquals(dtoUser.getName(),foundUser.getName());
		assertEquals(dtoUser.getPassword(),foundUser.getPassword());
	}
	
	@Test
	public void testSingleUserDelete() {
		_logger.info("Testing Multiple user post,delete and get....");
		_logger.info("[1] Posting...");
		List<UserDTO> dtoUserList = new ArrayList<UserDTO>();
		dtoUserList.add(new UserDTO("Arran","Password"));
		dtoUserList.add(new UserDTO("Jeff","Password"));
		dtoUserList.add(new UserDTO("Bill","nopassword"));
		for (UserDTO dtoUser : dtoUserList) {
			Response response = _client.target(WEB_SERVICE_URI + "/user").request().post(Entity.xml(dtoUser));
			int status = response.getStatus();
			if (status != 201) {
				_logger.error("Failed to post User; Web service responded with: "
						+ status);
				fail();
			}
			response.close();
		}
		_logger.info("[2] Deleting...");
		Response response = _client.target(WEB_SERVICE_URI + "/user/Arran").request().delete();
		int status = response.getStatus();
		if (status != 200) {
			_logger.error("Failed to delete User; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		_logger.info("[3] Getting...");
		UserDTO user = null;
		try {
			user = _client.target(WEB_SERVICE_URI + "/user/Arran").request().get(UserDTO.class);
		} catch (WebApplicationException e) {
			assertEquals(404,e.getResponse().getStatus());
		}
		if (user != null) {
			fail();
		}
	}
	
	@Test
	public void testDeleteAllUsers() {
		_logger.info("Testing Multiple user post,delete all and get....");
		_logger.info("[1] Posting...");
		List<UserDTO> dtoUserList = new ArrayList<UserDTO>();
		dtoUserList.add(new UserDTO("Arran","Password"));
		dtoUserList.add(new UserDTO("Jeff","Password"));
		dtoUserList.add(new UserDTO("Bill","nopassword"));
		for (UserDTO dtoUser : dtoUserList) {
			Response response = _client.target(WEB_SERVICE_URI + "/user").request().post(Entity.xml(dtoUser));
			int status = response.getStatus();
			if (status != 201) {
				_logger.error("Failed to post User; Web service responded with: "
						+ status);
				fail();
			}
			response.close();
		}
		_logger.info("[2] Deleting All...");
		Response response = _client.target(WEB_SERVICE_URI + "/users").request().delete();
		int status = response.getStatus();
		if (status != 200) {
			_logger.error("Failed to delete Users; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		_logger.info("[3] Getting...");
		for (UserDTO dtoUser : dtoUserList) {
			try {
			_client.target(WEB_SERVICE_URI + "/user/" + dtoUser.getName()).request().get(UserDTO.class);
			} catch (NotFoundException e) {
				assertEquals(404,e.getResponse().getStatus());
			}
		}
	}
	
	@Test
	public void testServerConnection() {
		_logger.info("[1] Posting...");
		List<UserDTO> dtoUserList = new ArrayList<UserDTO>();
		dtoUserList.add(new UserDTO("Arran","Password"));
		dtoUserList.add(new UserDTO("Jeff","Password"));
		dtoUserList.add(new UserDTO("Bill","nopassword"));
		for (UserDTO dtoUser : dtoUserList) {
			Response response = _client.target(WEB_SERVICE_URI + "/user").request().post(Entity.xml(dtoUser));
			int status = response.getStatus();
			if (status != 201) {
				_logger.error("Failed to post User; Web service responded with: "
						+ status);
				fail();
			}
			response.close();
		}
		
		_logger.info("[2] Posting server....");
		
		ServerDTO dtoServer = new ServerDTO("MainServer",1);
		
		Response response =  _client.target(WEB_SERVICE_URI + "/server").request().post(Entity.xml(dtoServer));
		int status = response.getStatus();
		if (status != 201) {
			_logger.error("Failed to post Server; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		
		_logger.info("[3] Attempting to connect to server...");
		
		for (UserDTO dtoUser : dtoUserList) {
			response =  _client.target(WEB_SERVICE_URI + "/user/" + dtoUser.getName() + "/connect").request().put(Entity.xml(dtoServer));
			status = response.getStatus();
			if (status == 200) {
				_logger.info("Successfully connected User: " + dtoUser.getName() + " to Server: " + dtoServer.getName());
			} else if (status == 303) {
				_logger.info("Servers are full");
				_logger.info("[4] Waiting for server availablity...");
				Client futureClient = ClientBuilder.newClient();
				Future<ServerDTO> futureResponse = futureClient.target(WEB_SERVICE_URI + "/connect").request().async().get(new InvocationCallback<ServerDTO>() {
					@Override
					public void completed(ServerDTO dtoServer) {
						_logger.info("Received aysnc Server: " + dtoServer.getName());
						_logger.info("[4] Calling server post test..");
						testPostToServer(dtoServer, dtoUser);
						futureClient.close();
					}

					@Override
					public void failed(Throwable e) {
						_logger.error("Failed");
						e.printStackTrace();
					}
				});
			} else {
				_logger.error("Failed to connect to Server; Web service responded with: "
						+ status);
				fail();
			}
			response.close();
			
		}
		
		_logger.info("[5] Posting new server....");
		
		ServerDTO dtoServerNew = new ServerDTO("AdditionalServer",3);
		
		response =  _client.target(WEB_SERVICE_URI + "/server").request().post(Entity.xml(dtoServerNew));
		status = response.getStatus();
		if (status != 201) {
			_logger.error("Failed to post Server; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	private void testPostToServer(ServerDTO serverDTO, UserDTO userDTO) {
		_logger.info("<1> Attempting to connect to server..");
		Client client = ClientBuilder.newClient();
		Response response =  client.target(WEB_SERVICE_URI + "/user/" + userDTO.getName() + "/connect").request().put(Entity.xml(serverDTO));
		int status = response.getStatus();
		if (status != 200) {
			_logger.error("User: " + userDTO.toString() + "Was denied entry to server.");
		} else  {
			_logger.info("Successfully connected User: " + userDTO.getName() + " to Server: " + serverDTO.getName());
		}
		response.close();
		client.close();
	}
	
}
