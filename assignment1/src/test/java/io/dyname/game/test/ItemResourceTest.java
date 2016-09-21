package io.dyname.game.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
	
	@Ignore
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
	
	@Ignore
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
	
	@Ignore
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
		response.close();
		
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
		List<CosmeticDTO> foundMysteryBoxContents = _client.target(WEB_SERVICE_URI + "/item/mysterybox/" + dtoMysteryBox.getName() + "/contents")
				.request().get(new GenericType<List<CosmeticDTO>>(){});
		
		assertEquals(1, foundMysteryBoxContents.size());
		assertEquals(dtoCosmetic.getName(), foundMysteryBoxContents.get(0).getName());
		assertEquals(dtoCosmetic.getInternalName(), foundMysteryBoxContents.get(0).getInternalName());
	}
	
	@Ignore
	@Test
	public void deleteAllItems() {
		_logger.info("Testing delete all items....");
		_logger.info("[1] Posting MysteryBox...");
		MysteryBoxDTO dtoMysteryBox = new MysteryBoxDTO("MysteryBox");
		Response response = _client.target(WEB_SERVICE_URI + "/item/mysterybox").request().post(Entity.xml(dtoMysteryBox));
		int status = response.getStatus();
		if (status != 201) {
			_logger.error("Failed to post MysteryBox; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		
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
		
		_logger.info("[3] Deleting all Items...");
		response = _client.target(WEB_SERVICE_URI + "/item").request().delete();
		status = response.getStatus();
		if (status != 200) {
			_logger.error("Failed to delete items; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		
		_logger.info("[4] Getting MysteryBox...");
		MysteryBoxDTO foundMysteryBox = null;
		try {
			foundMysteryBox = _client.target(WEB_SERVICE_URI + "/item/mysterybox/" + dtoMysteryBox.getName())
					.request().get(MysteryBoxDTO.class);
			
		}catch (WebApplicationException e) {
			assertEquals(404,e.getResponse().getStatus());
		}
		assertNull(foundMysteryBox);
		
		_logger.info("[5] Getting Item...");
		CosmeticDTO foundCosmetic = null;
		try {
			foundCosmetic = _client.target(WEB_SERVICE_URI + "/item/cosmetic/" + dtoCosmetic.getName())
					.request().get(CosmeticDTO.class);
		} catch (WebApplicationException e) {
			assertEquals(404,e.getResponse().getStatus());
		}
		assertNull(foundCosmetic);
	}
	
	
	@Test
	public void testDeleteCosmetic() {
		_logger.info("Testing delete all cosmetic....");
		
		_logger.info("[1] Posting Items....");
		List<CosmeticDTO> dtoCosmeticList = new ArrayList<CosmeticDTO>();
		dtoCosmeticList.add(new CosmeticDTO("Item","Item_asset"));
		for (CosmeticDTO dtoCosmetic : dtoCosmeticList) {
			Response response = _client.target(WEB_SERVICE_URI + "/item/cosmetic").request().post(Entity.xml(dtoCosmetic));
			int status = response.getStatus();
			if (status != 201) {
				_logger.error("Failed to post Cosmetic; Web service responded with: "
						+ status);
				fail();
			}
			response.close();
		}
		
		_logger.info("[2] Deleting Cosmetics...");
		Response response = _client.target(WEB_SERVICE_URI + "/item/cosmetic/" + dtoCosmeticList.get(0).getName()).request().delete();
		int status = response.getStatus();
		if (status != 200) {
			_logger.error("Failed to delete cosmetics; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		
		_logger.info("[3] Getting Items...");
		for (CosmeticDTO dtoCosmetic : dtoCosmeticList) {
			CosmeticDTO foundCosmetic = null;
			try {
				foundCosmetic = _client.target(WEB_SERVICE_URI + "/item/cosmetic/" + dtoCosmetic.getName())
						.request().get(CosmeticDTO.class);
			} catch (WebApplicationException e) {
				assertEquals(404,e.getResponse().getStatus());
			}
			assertNull(foundCosmetic);
		}
	}
	
	@Test
	public void testDeleteMysteryBox() {
		_logger.info("Testing delete all cosmetic....");
		
		_logger.info("[1] Posting Items....");
		List<MysteryBoxDTO> dtoMysteryBoxList = new ArrayList<MysteryBoxDTO>();
		dtoMysteryBoxList.add(new MysteryBoxDTO("Box"));
		for (MysteryBoxDTO dtoCosmetic : dtoMysteryBoxList) {
			Response response = _client.target(WEB_SERVICE_URI + "/item/mysterybox").request().post(Entity.xml(dtoCosmetic));
			int status = response.getStatus();
			if (status != 201) {
				_logger.error("Failed to post MysteryBox; Web service responded with: "
						+ status);
				fail();
			}
			response.close();
		}
		
		_logger.info("[2] Deleting MysteryBox...");
		Response response = _client.target(WEB_SERVICE_URI + "/item/mysterybox/" + dtoMysteryBoxList.get(0).getName()).request().delete();
		int status = response.getStatus();
		if (status != 200) {
			_logger.error("Failed to delete MysteryBox; Web service responded with: "
					+ status);
			fail();
		}
		response.close();
		
		_logger.info("[3] Getting Items...");
		for (MysteryBoxDTO dtoMystery : dtoMysteryBoxList) {
			MysteryBoxDTO foundMysteryBox = null;
			try {
				foundMysteryBox = _client.target(WEB_SERVICE_URI + "/item/cosmetic/" + dtoMystery.getName())
						.request().get(MysteryBoxDTO.class);
			} catch (WebApplicationException e) {
				assertEquals(404,e.getResponse().getStatus());
			}
			assertNull(foundMysteryBox);
		}
	}
}
