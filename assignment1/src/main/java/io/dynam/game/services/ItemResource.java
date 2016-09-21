package io.dynam.game.services;


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
import io.dynam.game.utils.CosmeticMapper;
import io.dynam.game.utils.MysteryBoxMapper;
import io.dynam.game.utils.PersistenceManager;
import io.dynam.game.utils.UserMapper;

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
import javax.ws.rs.DELETE;
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
 * * Items
 * - GET	<base-uri>/item/cosmetic/{name}
 * 			retrieves a cosmetic item by its name
 * - GET	<base-uri>/item/mystery/{name}
 * 			retrieves a mystery box by its name
 * - POST	<base-uri>/item/cosmetic
 * 			posts a cosmetic item
 * - POST	<base-uri>/item/mystery
 * 			posts a mystery box
 * - POST 	<base-uri>/item/mysterybox/{name}
 * 			posts a cosmetic to a mysterybox
 * - GET	<base-uri>/item/mysterybox/{name}/contents
 * 			retrieves the contents of a mysterybox
 * - DEL	<base-uri>/item/mysterybox/{name}
 * 			Deletes a mystery box by name
 * - DEL	<base-uri>/item/cosmetic/{name
 * 			Deletes a cosmetic by name
 * - DEL	<base-uri>/item
 * 			Deletes all items
 * 
 */
@Path("/game")
public class ItemResource {
	//TODO: implement proper logging
	private static Logger _logger = LoggerFactory
			.getLogger(ItemResource.class);
	private static EntityManagerFactory _factory = PersistenceManager.getFactory();
	
	@DELETE
	@Path("/item")
	public Response deleteAllItems() {
		EntityManager em = _factory.createEntityManager();
		try {
			em.getTransaction().begin();
			em.createNativeQuery("DELETE FROM CRATE_ITEM c").executeUpdate();
			em.createNativeQuery("DELETE FROM Cosmetic c").executeUpdate();
			em.createNativeQuery("DELETE FROM MysteryBox m").executeUpdate();
			em.createNativeQuery("DELETE FROM INVENTORY_ITEM i").executeUpdate();
			em.createNativeQuery("DELETE FROM Item i").executeUpdate();
			em.getTransaction().commit();
			em.close();
			
		} catch (IllegalStateException | SecurityException e) {
		    e.printStackTrace();
		} 
		return Response.ok().build();
	}
	
	@GET
	@Path("/item/cosmetic/{name}")
	@Produces({"application/xml","application/json"})
	public Response getCosmeticItem(@PathParam("name") String itemName) {
		Cosmetic cosmetic = null;
		try {
			cosmetic = PersistenceManager.getCosmeticByName(itemName);
		} catch (WebApplicationException e) {
			return Response.noContent().build();
		}
		return Response.ok().entity(CosmeticMapper.toDto(cosmetic)).build();
	}
	
	@GET
	@Path("/item/mysterybox/{name}")
	@Produces({"application/xml","application/json"})
	public Response getMysteryBox(@PathParam("name") String boxName) {
		MysteryBox mysteryBox = null;
		try {
			mysteryBox = PersistenceManager.getMysteryBoxByName(boxName);
		} catch (WebApplicationException e) {
			return Response.noContent().build();
		}
		return Response.ok().entity(MysteryBoxMapper.toDto(mysteryBox)).build();
	}
	
	@POST
	@Path("/item/cosmetic")
	@Consumes({"application/xml","application/json"})
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
	
	@DELETE
	@Path("/item/cosmetic/{name}")
	public Response deleteAllCosmeticsByName(@PathParam("name") String itemName) {
		EntityManager em = _factory.createEntityManager();
		try {
			em.getTransaction().begin();
			TypedQuery<Cosmetic> query = em.createQuery("from Cosmetic c where c._name = :cname", Cosmetic.class)
					.setParameter("cname",itemName);
			Cosmetic cosmetic = query.getSingleResult();
			em.remove(cosmetic);
			em.getTransaction().commit();
			em.close();
			
		} catch (IllegalStateException | SecurityException e) {
		    e.printStackTrace();
		} 
		return Response.ok().build();
	}
	
	@DELETE
	@Path("/item/mysterybox/{name}")
	public Response deleteAllMysteryBoxs(@PathParam("name") String boxName) {
		EntityManager em = _factory.createEntityManager();
		try {
			em.getTransaction().begin();
			TypedQuery<MysteryBox> query = em.createQuery("from MysteryBox m where m._name = :mname", MysteryBox.class)
					.setParameter("mname",boxName);
			MysteryBox mysteryBox = query.getSingleResult();
			em.remove(mysteryBox);
			em.getTransaction().commit();
			em.close();
			
		} catch (IllegalStateException | SecurityException e) {
		    e.printStackTrace();
		} 
		return Response.ok().build();
	}
	
	@POST
	@Path("/item/mysterybox")
	@Consumes({"application/xml","application/json"})
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
	@Consumes({"application/xml","application/json"})
	public Response postCosmeticToMysteryBox(@PathParam("name") String boxName, CosmeticDTO dtoCosmetic) {
		
		EntityManager em = _factory.createEntityManager();
		
		em.getTransaction().begin();
		MysteryBox mysteryBox = em.find(MysteryBox.class, PersistenceManager.getMysteryBoxByName(boxName).getId());
		Cosmetic cosmetic = em.find(Cosmetic.class, PersistenceManager.getCosmeticByName(dtoCosmetic.getName()).getId());
		if (!mysteryBox.getContents().add(cosmetic)) {
			return Response.status(400).entity("Box already has Cosmetic").build();
		}
		em.persist(mysteryBox);
		em.getTransaction().commit();
		em.close();
		return Response.created(URI.create("/item/mysterybox/" + mysteryBox.getName())).build();
	}
	
	@GET
	@Path("/item/mysterybox/{name}/contents")
	@Produces({"application/xml","application/json"})
	public List<CosmeticDTO> getMysteryBoxContents(@PathParam("name") String boxName, @QueryParam("search") String optionalSearch) {		
		if (optionalSearch == null) {
			List<Cosmetic> content = PersistenceManager.getMysteryBoxContents(boxName);
			List<CosmeticDTO> contentDTO = CosmeticMapper.toDto(content);
			return contentDTO;
		} else {
			List<Cosmetic> content = PersistenceManager.getMysteryBoxContents(boxName);
			for (Cosmetic cos: content) {
				if (cos.getName().equals(optionalSearch)) {
					List<CosmeticDTO> contentDTO = new ArrayList<CosmeticDTO>();
					contentDTO.add(CosmeticMapper.toDto(cos));
					return contentDTO;
				}
			}
			throw new WebApplicationException(404);
		}
	}
}
