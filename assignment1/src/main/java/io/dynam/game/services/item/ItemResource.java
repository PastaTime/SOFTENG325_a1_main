package io.dynam.game.services.item;


import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import io.dynam.game.domain.Cosmetic;
import io.dynam.game.domain.Item;
import io.dynam.game.domain.MysteryBox;
import io.dynam.game.domain.User;
import io.dynam.game.dto.CosmeticDTO;
import io.dynam.game.dto.MysteryBoxDTO;
import io.dynam.game.dto.UserDTO;
import io.dynam.game.services.CosmeticMapper;
import io.dynam.game.services.MysteryBoxMapper;
import io.dynam.game.services.PersistenceManager;
import io.dynam.game.services.UserMapper;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Base-URI: http//localhost:1000/services/game
 * 
 * * Inventory
 * 		TODO:
 * - GET	<base-uri>/user/{name}/inventory/{item}
 * 			retrieves an item from a users inventory
 * 		TODO:	
 * - GET	<base-uri>/user/{name}/inventory
 * 			retrieves all items from a users inventory
 * 		TODO:
 * - POST	<base-uri>/user/{name}/inventory
 * 			Adds an item to the users inventory
 * 		TODO:
 * - DEL	<base-uri>/user/{name}/inventory/{item}
 * 			Deletes an item from the users inventory
 * * Items
 * 		TODO:
 * - GET	<base-uri>/item/cosmetic/{name}
 * 			retrieves a cosmetic item by its name
 * 		TODO:
 * - GET	<base-uri>/item/mystery/{name}
 * 			retrieves a myster box by its name
 * 		TODO:
 * - POST	<base-uri>/item/cosmetic
 * 			posts a cosmetic item
 * 		TODO:
 * - POST	<base-uri>/item/mystery
 * 			posts a mystery box
 * 
 */
@Path("/game")
public class ItemResource {
	//TODO: implement proper logging
	private static Logger _logger = LoggerFactory
			.getLogger(ItemResource.class);
	private static EntityManagerFactory _factory = PersistenceManager.getFactory();
	
	 /* * Items
	 * - GET	<base-uri>/item/cosmetic/{name}
	 * 			retrieves a cosmetic item by its name
	 * - GET	<base-uri>/item/mysterybox/{name}
	 * 			retrieves a mystery box by its name
	 * - POST	<base-uri>/item/cosmetic
	 * 			posts a cosmetic item
	 * - POST	<base-uri>/item/mysterybox
	 * 			posts a mystery box (empty)
	 * 
	 ** Mystery Box
	 * - GET 	<base-uri>/item/mysterybox/{name}/contents?optional search
	 * 			Gets all items in the mysterybox
	 * - POST	<base-uri>/items/mysterybox/{name}
	 * 			posts a cosmetic item to the mysterybox
	 */
	
	@GET
	@Path("/item/cosmetic/{name}")
	@Produces("application/xml")
	public CosmeticDTO getCosmeticItem(@PathParam("name") String itemName) {
		Cosmetic cosmetic = PersistenceManager.getCosmeticByName(itemName);
		return CosmeticMapper.toDto(cosmetic);
	}
	
	@GET
	@Path("/item/mysterybox/{name}")
	@Produces("application/xml")
	public MysteryBoxDTO getMysteryBox(@PathParam("name") String boxName) {
		MysteryBox mysteryBox = PersistenceManager.getMysteryBoxByName(boxName);
		return MysteryBoxMapper.toDto(mysteryBox);
	}
	
	@POST
	@Path("/item/cosmetic")
	@Consumes("application/xml")
	public Response createCosmeticItem(CosmeticDTO dtoCosmetic) {
		EntityManager em = _factory.createEntityManager();
		Cosmetic cosmetic = CosmeticMapper.toDomainModel(dtoCosmetic);
		try {
			em.getTransaction().begin();
			em.persist(cosmetic);
			em.getTransaction().commit();
			em.close();
		} catch (PersistenceException e) {
			return Response.status(400).entity(e.getMessage()).build();
		}
		return Response.created(URI.create("/item/cosmetic/" + cosmetic.getName())).build();
	}
	
	@POST
	@Path("/item/mysterybox")
	@Consumes("application/xml")
	public Response createMysteryBox(MysteryBoxDTO dtoMysteryBox) {
		
		EntityManager em = _factory.createEntityManager();
		MysteryBox mysteryBox = MysteryBoxMapper.toDomainModel(dtoMysteryBox);
		try {
			em.getTransaction().begin();
			em.persist(mysteryBox);
			em.getTransaction().commit();
			em.close();
		} catch (PersistenceException e) {
			return Response.status(400).entity(e.getMessage()).build();
		}
		return Response.created(URI.create("/item/mystery/" + mysteryBox.getName())).build();
	}
	
	@POST
	@Path("/item/mysterybox/{name}")
	@Consumes("application/xml")
	public Response postCosmeticToMysteryBox(@PathParam("name") String boxName, CosmeticDTO dtoCosmetic) {
		MysteryBox mysteryBox = PersistenceManager.getMysteryBoxByName(boxName);
		Cosmetic cosmetic = CosmeticMapper.toDomainModel(dtoCosmetic);
		if (!mysteryBox.getContents().add(cosmetic)) {
			return Response.status(400).entity("Box already has Cosmetic").build();
		}
		EntityManager em = _factory.createEntityManager();
		em.getTransaction().begin();
		em.merge(mysteryBox);
		em.getTransaction().commit();
		em.close();
		return Response.created(URI.create("/item/mysterybox/" + mysteryBox.getName())).build();
	}
	
	@GET
	@Path("/item/mysterybox/{name}/contents")
	@Produces("application/xml")
	public Response getMysteryBoxContents(@PathParam("name") String boxName, @QueryParam("search") String optionalSearch) {		
		if (optionalSearch == null) {
			List<Cosmetic> content = PersistenceManager.getMysteryBoxContents(boxName);
			List<CosmeticDTO> contentDTO = CosmeticMapper.toDto(content);
			GenericEntity<List<CosmeticDTO>> entity = new GenericEntity<List<CosmeticDTO>>(contentDTO){};
			return Response.ok().entity(entity).build();
		} else {
			List<Cosmetic> content = PersistenceManager.getMysteryBoxContents(boxName);
			for (Cosmetic cos: content) {
				if (cos.getName().equals(optionalSearch)) {
					return Response.ok().entity(CosmeticMapper.toDto(cos)).build();
				}
			}
			throw new WebApplicationException(404);
		}
	}
}
