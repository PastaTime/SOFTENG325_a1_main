package io.dynam.game.services;


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
import io.dynam.game.utils.CosmeticMapper;
import io.dynam.game.utils.InventoryMapper;
import io.dynam.game.utils.MysteryBoxMapper;
import io.dynam.game.utils.PersistenceManager;
import io.dynam.game.utils.UserMapper;

import javax.annotation.Resource;
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
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
 * * Base
 * 
 * - DEL	<base-uri>
 * 			Deletes all entitys stored in the database
 * 
 ** User
 *
 * - POST 	<base-uri>/user
 * 			Creates a user in the database
 * - GET 	<base-uri>/user/{name}
 * 			retrieves a user DTO object from the data base.
 * 			TODO: requires authentication
 * - GET	<base-uri>/users
 * 			retrieves all users in the database
 * 			TODO: only show 10 and HATEOS to link others
 * 			TODO: requires Admin
 * - PUT    <base-uri>/user/{name}
 * 			TODO: updates a users password (XML containing new and old password?)
 * - DEL	<base-uri>/user/{name}
 * 			Deletes a user by name
 * - DEL	<base-uri>/users
 * 			Deletes all users
 * 
 * * Inventory
 * 
 * - POST	<base-uri>/user/{name}/inventory
 * 			Adds a cosmetic item to the users inventory (via CosmeticDTO payload)
 * - POST	<base-uri>/user/{name}/inventory
 * 			Adds a myster box to the users inventory (via MysteryBoxDTO payload)
 * - GET	<base-uri>/user/{name}/inventory/{item}
 * 			retrieves an item from a users inventory by name
 * - GET	<base-uri>/user/{name}/inventory
 * 			retrieves a users inventory
 * - DEL	<base-uri>/user/{name}/inventory
 * 			Deletes an item from the users inventory
 * - DEL	<base-uri>/user/{name}/inventory/{item}
 * 			Deletes a specific item from the users inventory
 */
@Path("/game")
public class UserResource {
	private static Logger _logger = LoggerFactory
			.getLogger(UserResource.class);
	private static EntityManagerFactory _factory = PersistenceManager.getFactory();
	
	/**
	 * A universal delete operation.
	 * Deletes all persisted entitys
	 * @return
	 */
	@DELETE
	public Response deleteAll() {
		EntityManager em = _factory.createEntityManager();
		_logger.info("Cleaning Database............");
		try {
			em.getTransaction().begin();
			em.createNativeQuery("DELETE FROM User u").executeUpdate();
			em.createNativeQuery("DELETE FROM USER_SERVER u").executeUpdate();
			em.createNativeQuery("DELETE FROM Server s").executeUpdate();
			em.createNativeQuery("DELETE FROM MysteryBox m").executeUpdate();
			em.createNativeQuery("DELETE FROM CRATE_ITEM c").executeUpdate();
			em.createNativeQuery("DELETE FROM Cosmetic c").executeUpdate();
			em.createNativeQuery("DELETE FROM CRATE_ITEM c").executeUpdate();
			em.createNativeQuery("DELETE FROM Item i").executeUpdate();
			em.createNativeQuery("DELETE FROM INVENTORY_ITEM i").executeUpdate();
			em.getTransaction().commit();
			em.close();
			
		} catch (IllegalStateException | SecurityException e) {
		    e.printStackTrace();
		} 
		_logger.info("Database cleaned");	
		return Response.ok().build();
	}
	
	/**
	 * Creates a user on the database.
	 * @param dtoUser - a DTO containing a skeleton for the new user entity
	 * @return
	 */
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
	
	/**
	 * Retrieves a UserDTO from its name.
	 * @param username - the users name
	 * @return
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
	
	/**
	 * Updates a users password as specified by a UserDTO payload.
	 * @param username - The users name
	 * @param dtoUser - A DTO containing the users new password
	 * @return
	 */
	@PUT
	@Path("/user/{name}")
	@Consumes("application/xml")
	@Produces("application/xml")
	public Response updateUserPassword(@PathParam("name") String username, UserDTO dtoUser) {
		User user = PersistenceManager.getUserByName(username);
		if (!user.getName().equals(dtoUser.getName())) {
			return Response.status(400).build();
		} else if (user.getPassword().equals(dtoUser.getPassword())) {
			return Response.ok().entity(user).build();
		} else {
			EntityManager em = _factory.createEntityManager();
			em.getTransaction().begin();
			user.setPassword(dtoUser.getPassword());
			em.merge(user);
			em.getTransaction().commit();
			em.close();
			return Response.ok().entity(user).build();
		}
	}
	
	/**
	 * Retrieves all users registered in the database.
	 * @return
	 */
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
	
	/**
	 * Deletes all users from the database
	 * @return
	 */
	@DELETE
	@Path("/users")
	public Response deleteAllUsers() {
		_logger.info("Deleting all Users...");
		EntityManager em = _factory.createEntityManager();
		em.createNativeQuery("DELETE FROM User u").executeUpdate();
		return Response.ok().build();
	}
	
	/**
	 * Deletes a user from the database as identified by its name.
	 * @param username
	 * @return
	 */
	@DELETE
	@Path("/user/{name}")
	public Response deleteUserByName(@PathParam("name") String username) {
		EntityManager em = _factory.createEntityManager();
		em.getTransaction().begin();
		em.createQuery("DELETE FROM User u where u._name = :uname")
				.setParameter("uname", username)
				.executeUpdate();
		em.getTransaction().commit();
		em.close();
		return Response.ok().build();
	}

	/**
	 * Posts a cosmetic item to a users inventory
	 * @param username - A path parameters for the owner of the inventory
	 * @param dtoCosmetic - The cosmetic item to be given
	 * @return
	 */
	@POST
	@Path("/user/{name}/inventory")
	@Consumes("application/xml")
	public Response giveUserCosmetic(@PathParam("name") String username, CosmeticDTO dtoCosmetic) {
		User user = PersistenceManager.getUserByName(username);
		Cosmetic cosmetic = CosmeticMapper.toDomainModel(dtoCosmetic);
		Set<Item> newItems = user.getInventory().getItems();
		if (!newItems.add(cosmetic)) {
			return Response.status(202).entity("User already has Item").build();
		}
		EntityManager em = _factory.createEntityManager();
		em.getTransaction().begin();
		em.merge(user);
		em.getTransaction().commit();
		em.close();
		return Response.created(URI.create("/user/" + user.getName() + "/inventory")).build();
	}
	
	/**
	 * Posts a mystery box to a users inventory.
	 * NOTE: MysteryBoxs must be posted on the /item/mysterybox before being linked to a user here.
	 * @param username - A path parameter for the owner of the inventory
	 * @param dtoMysteryBox - A DTO for the mysterybox to be posted.
	 * @return
	 */
	@POST
	@Path("/user/{name}/inventory")
	@Consumes("application/xml")
	public Response giveUserMysteryBox(@PathParam("name") String username, MysteryBoxDTO dtoMysteryBox) {
		User user = PersistenceManager.getUserByName(username);
		MysteryBox mysteryBox = PersistenceManager.getMysteryBoxByName(dtoMysteryBox.getName());
		Set<Item> newItems = user.getInventory().getItems();
		if (!newItems.add(mysteryBox)) {
			return Response.status(202).entity("User already has Item").build();
		}
		EntityManager em = _factory.createEntityManager();
		em.getTransaction().begin();
		em.merge(user);
		em.getTransaction().commit();
		em.close();
		return Response.created(URI.create("/user/" + user.getName() + "/inventory")).build();
	}
	
	/**
	 * Retrieves a users whole inventory
	 * @param username - A path parameters for the owner of the inventory
	 * @return
	 */
	@GET
	@Path("/user/{name}/inventory")
	@Produces("application/xml")
	public InventoryDTO getUserInventory(@PathParam("name") String username) {
		Inventory invent = PersistenceManager.getUserInventory(username);
		return InventoryMapper.toDto(invent);
	}
	
	/**
	 * Retrieves a users item.
	 * @param username - A path parameters for the owner of the item
	 * @param itemName - A path parameters for the name of the item to be retrieved
	 * @return
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

	/**
	 * Deletes all the items in a users inventory.
	 * @param username - The owner of the inventory
	 * @return
	 */
	@DELETE
	@Path("/user/{name}/inventory")
	@Consumes("application/xml")
	public Response deleteUserInventory(@PathParam("name") String username) {
		EntityManager em = _factory.createEntityManager();
		em.getTransaction().begin();
		Inventory invent = PersistenceManager.getUserInventory(username);
		invent.getItems().clear();
		em.merge(invent);
		em.getTransaction().commit();
		em.close();
		return Response.ok().build();
	}
	
	/**
	 * Delete a specific item in a users inventory
	 * @param username - The owner of the inventory
	 * @param itemName - The name of the item to be deleted
	 * @return
	 */
	@DELETE
	@Path("/user/{name}/inventory/{item}")
	@Consumes("application/xml")
	public Response deleteUserInventoryItem(@PathParam("name") String username, @PathParam("item") String itemName) {
		EntityManager em = _factory.createEntityManager();
		em.getTransaction().begin();
		Inventory invent = PersistenceManager.getUserInventory(username);
		for (Item item : invent.getItems()) {
			if (item.getName().equals(itemName)) {
				invent.getItems().remove(item);
				break;
			}
		}
		em.merge(invent);
		em.getTransaction().commit();
		em.close();
		return Response.ok().build();
	}
	
	
	
	
}
