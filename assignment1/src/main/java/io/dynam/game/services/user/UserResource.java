package io.dynam.game.services.user;


import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import io.dynam.game.domain.Cosmetic;
import io.dynam.game.domain.Inventory;
import io.dynam.game.domain.Item;
import io.dynam.game.domain.MysteryBox;
import io.dynam.game.domain.User;
import io.dynam.game.dto.CosmeticDTO;
import io.dynam.game.dto.InventoryDTO;
import io.dynam.game.dto.MysteryBoxDTO;
import io.dynam.game.dto.UserDTO;
import io.dynam.game.services.CosmeticMapper;
import io.dynam.game.services.InventoryMapper;
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
 ** User
 * - GET 	<base-uri>/user/{name}
 * 			retrieves a user DTO object from the data base.
 * 			TODO: requires authentication
 * - GET	<base-uri>/users
 * 			retrieves all users in the database
 * 			TODO: only show 10 and HATEOS to link others
 * 			TODO: requires Admin
 * - POST 	<base-uri>/user
 * 			posts a user DTO to the database.
 * 			TODO: Requires Admin
 * 		TODO:
 * - PUT    <base-uri>/user/{name}
 * 			TODO: updates a users password (XML containing new and old password?)
 * * Inventory
 * - GET	<base-uri>/user/{name}/inventory/{item}
 * 			retrieves an item from a users inventory
 * - GET	<base-uri>/user/{name}/inventory
 * 			retrieves a users inventory
 * - POST	<base-uri>/user/{name}/inventory
 * 			Adds an item to the users inventory
 * 		TODO:
 * - DEL	<base-uri>/user/{name}/inventory/{item}
 * 			Deletes an item from the users inventory
 * 
 */
@Path("/game")
public class UserResource {
	//TODO: implement proper logging
	private static Logger _logger = LoggerFactory
			.getLogger(UserResource.class);
	private static EntityManagerFactory _factory = PersistenceManager.getFactory();
	
	@GET
	@Path("/login")
	@Produces("application/xml")
	public Response register(@QueryParam("name") String username,
			@QueryParam("pass") String password) {
		if (username == null || password == null) {
			return Response.serverError().entity("ERROR").build();
		} else {
			// Query database and check user with password exists
			EntityManager em = _factory.createEntityManager();
			TypedQuery<User> query = em
					.createQuery(
							"from User u where u.name like :uname and u.password like :upass",
							User.class).setParameter("uname", username)
					.setParameter("upass", password);
			User user = query.getSingleResult();
			// NonUniqueResultException and NoResultException
			NewCookie userCookie = new NewCookie("user", username);
			return Response.ok().cookie(userCookie).build();
		}
	}
	
	/*
	 * 
	 * USER Implementation
	 * 
	 */
	@GET
	@Path("/user/{name}")
	@Produces("application/xml")
	public UserDTO getUser(@PathParam("name") String username) {
		//TODO: Implement user login security
		User user = PersistenceManager.getUserByName(username);
		UserDTO dtoUser = UserMapper.toDto(user);

		return dtoUser;
	}
	
	@GET
	@Path("/users")
	@Produces("application/xml")
	public Response getAllUsers() {
		EntityManager em = _factory.createEntityManager();
		TypedQuery<User> query = em.createQuery(
				"from User u", User.class);
		List<User> user = query.getResultList();
		List<UserDTO> dtoUsers = new ArrayList<UserDTO>();
		for (User u : user) {
			dtoUsers.add(UserMapper.toDto(u));
		}
		GenericEntity<List<UserDTO>> entity = new GenericEntity<List<UserDTO>>(dtoUsers){};
		return Response.ok().entity(entity).build();
	}
	
	@POST
	@Path("/user")
	@Consumes("application/xml")
	public Response createUser(UserDTO dtoUser) {
		_logger.info("creating User....");
		User user = null;
		try {
			user = UserMapper.toDomainModel(dtoUser);
			EntityManager em = _factory.createEntityManager();
			em.getTransaction().begin();
			em.persist(user);
			em.getTransaction().commit();
			em.close();
		} catch (PersistenceException e) {
			return Response.status(400).entity(e.getMessage()).build();
		}
		return Response.created(URI.create("/user/" + user.getName())).build();
	}
	
	/*
	 * 
	 * ITEM implementation
	 * 
	  * * Inventory
 * - GET	<base-uri>/user/{name}/inventory?item
 * 			retrieves an item from a users inventory
 * - GET	<base-uri>/user/{name}/inventory
 * 			retrieves all items from a users inventory
 * - POST	<base-uri>/user/{name}/inventory
 * 			Adds an item to the users inventory
 * - DEL	<base-uri>/user/{name}/inventory?item
 * 			Deletes an item from the users inventory
 */
	
	@GET
	@Path("/user/{name}/inventory/{item}")
	@Produces("application/xml")
	public Response getUserItem(@PathParam("name") String username, @PathParam("item") String itemName) {
		EntityManager em = _factory.createEntityManager();
		Item item = null;
		try {
			TypedQuery<Item> query = em.createQuery(
					"select i from User u " +
			"inner join u._invent._items i " +
			"where u._name = :uname and i._name = :iname", Item.class)
					.setParameter("uname", username)
					.setParameter("iname",itemName);
			item = query.getSingleResult();
		} catch (NoResultException e) {
			throw new WebApplicationException(404);
		}
		em.close();
		if (item instanceof Cosmetic) {
			CosmeticDTO dtoCosmetic = CosmeticMapper.toDto((Cosmetic)item);
			return Response.ok().entity(dtoCosmetic).build();
		} else if (item instanceof MysteryBox) {
			MysteryBoxDTO dtoMystery = MysteryBoxMapper.toDto((MysteryBox) item);
			return Response.ok().entity(dtoMystery).build();
		} else {
			throw new WebApplicationException(404);
		}
	}
	
	@GET
	@Path("/user/{name}/inventory")
	@Produces("application/xml")
	public InventoryDTO getUserInventory(@PathParam("name") String username) {
		Inventory invent = PersistenceManager.getUserInventory(username);
		return InventoryMapper.toDto(invent);
	}
	
	@POST
	@Path("/user/{name}/inventory")
	@Produces("application/xml")
	public Response giveUserCosmetic(@PathParam("name") String username, CosmeticDTO dtoCosmetic) {
		User user = PersistenceManager.getUserByName(username);
		Cosmetic cosmetic = CosmeticMapper.toDomainModel(dtoCosmetic);
		Set<Item> newItems = user.getInventory().getItems();
		if (!newItems.add(cosmetic)) {
			return Response.status(400).entity("User already has Item").build();
		}
		EntityManager em = _factory.createEntityManager();
		em.getTransaction().begin();
		em.merge(user);
		em.getTransaction().commit();
		em.close();
		return Response.created(URI.create("/user/" + user.getName() + "/inventory")).build();
	}
}
