package io.dyname.game.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import io.dynam.game.domain.Cosmetic;
import io.dynam.game.domain.MysteryBox;
import io.dynam.game.dto.CosmeticDTO;
import io.dynam.game.dto.ItemDTO;
import io.dynam.game.dto.MysteryBoxDTO;
import io.dynam.game.dto.UserDTO;
import io.dynam.game.utils.PersistenceManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ItemResourceTest {
	/**
	 * Base-URI: http//localhost:1000/services/game
	 * 
	 * * Items
	 * - POST 	<base-uri>/item/mysterybox/{name}
	 * 			posts a cosmetic to a mysterybox
	 * - GET	<base-uri>/item/mysterybox/{name}/contents
	 * 			retrieves the contents of a mysterybox
	 * - DEL	<base-uri>/item/mysterybox/{name}
	 * 			Deletes a mystery box by name
	 * - DEL	<base-uri>/item/mystery
	 * 			Deletes all mystery boxs
	 * - DEL	<base-uri>/item/cosmetic/{name
	 * 			Deletes a cosmetic by name
	 * - DEL	<base-uri>/item/cosmetic
	 * 			Deletes all cosmetics
	 * - DEL	<base-uri>/item
	 * 			Deletes all items
	 * 
	 */

	private static String WEB_SERVICE_URI = "http://localhost:10000/services/game";
	
	private Logger _logger = LoggerFactory.getLogger(ItemResourceTest.class);
	
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
	public void testCosmeticSinglePostAndGet() {
		_logger.info("Testing single cosmetic post and get....");
		_logger.info("[1] Posting...");
		CosmeticDTO dtoCosmetic = new CosmeticDTO("Item","Asset_Name");
		Response response = _client.target(WEB_SERVICE_URI + "/item/cosmetic").request().post(Entity.xml(dtoCosmetic));
		int status = response.getStatus();
		if (status != 201) {
			_logger.error("Failed to post Cosmetic; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		_logger.info("[2] Getting...");
		CosmeticDTO foundCosmetic = _client.target(WEB_SERVICE_URI + "/item/cosmetic/" + dtoCosmetic.getName())
				.request().get(CosmeticDTO.class);
		
		assertEquals(dtoCosmetic.getName(),foundCosmetic.getName());
		assertEquals(dtoCosmetic.getInternalName(),foundCosmetic.getInternalName());
	}
	
	@Test
	public void testMysteryBoxSinglePostAndGet() {
		_logger.info("Testing single MysteryBox post and get....");
		_logger.info("[1] Posting...");
		MysteryBoxDTO dtoMysteryBox = new MysteryBoxDTO("MysteryBox");
		Response response = _client.target(WEB_SERVICE_URI + "/item/mysterybox").request().post(Entity.xml(dtoMysteryBox));
		int status = response.getStatus();
		if (status != 201) {
			_logger.error("Failed to post MysteryBox; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		_logger.info("[2] Getting...");
		MysteryBoxDTO foundMysteryBox = _client.target(WEB_SERVICE_URI + "/item/mysterybox/" + dtoMysteryBox.getName())
				.request().get(MysteryBoxDTO.class);
		
		assertEquals(dtoMysteryBox.getName(),foundMysteryBox.getName());
	}
	
	@Test
	public void testMysteryBoxWithContentsPostAndGet() {
		_logger.info("Testing single MysteryBox post and get....");
		_logger.info("[1] Posting MysteryBox...");
		MysteryBoxDTO dtoMysteryBox = new MysteryBoxDTO("MysteryBox");
		Response response = _client.target(WEB_SERVICE_URI + "/item/mysterybox").request().post(Entity.xml(dtoMysteryBox));
		int status = response.getStatus();
		if (status != 201) {
			_logger.error("Failed to post MysteryBox; Web service responded with: "
					+ status);
			fail();
		}
		
		_logger.info("[2] Posting Item...");
		CosmeticDTO dtoCosmetic = new CosmeticDTO("Item","Asset_Name");
		response = _client.target(WEB_SERVICE_URI + "/item/cosmetic").request().post(Entity.xml(dtoCosmetic));
		status = response.getStatus();
		if (status != 201) {
			_logger.error("Failed to post Cosmetic; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		
		_logger.info("[3] Posting item to MysteryBox...");
		response = _client.target(WEB_SERVICE_URI + "/item/mysterybox/" + dtoMysteryBox.getName()).request().post(Entity.xml(dtoCosmetic));
		status = response.getStatus();
		if (status != 201) {
			_logger.error("Failed to post Cosmetic; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		_logger.info("[4] Getting MysteryBox Contents...");
		MysteryBoxDTO foundMysteryBox = _client.target(WEB_SERVICE_URI + "/item/mysterybox/" + dtoMysteryBox.getName() + "/contents")
				.request().get(GenericType<List<CosmeticDTO>>());
		
		
		assertEquals(dtoMysteryBox.getName(),foundMysteryBox.getName());
	}
}
