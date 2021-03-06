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
import io.dynam.game.domain.Server;
import io.dynam.game.domain.User;
import io.dynam.game.dto.CosmeticDTO;
import io.dynam.game.dto.InventoryDTO;
import io.dynam.game.dto.ItemDTO;
import io.dynam.game.dto.MysteryBoxDTO;
import io.dynam.game.dto.ServerDTO;
import io.dynam.game.dto.UserDTO;
import io.dynam.game.utils.CosmeticMapper;
import io.dynam.game.utils.InventoryMapper;
import io.dynam.game.utils.ItemMapper;
import io.dynam.game.utils.MysteryBoxMapper;
import io.dynam.game.utils.PersistenceManager;
import io.dynam.game.utils.ServerMapper;
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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
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
 * - DEL	<base-uri>
 * 			Deletes all entitys stored in the database
 * 
 ** User
 * - POST 	<base-uri>/user
 * 			Creates a user in the database
 * - GET 	<base-uri>/user/{name}
 * 			retrieves a user DTO object from the data base.
 * - GET	<base-uri>/users
 * 			retrieves all users in the database
 * - PUT    <base-uri>/user/{name}
 * 			updates a users password
 * - DEL	<base-uri>/user/{name}
 * 			Deletes a user by name
 * - DEL	<base-uri>/users
 * 			Deletes all users
 * 
 * * Inventory
 * - POST	<base-uri>/user/{name}/inventory
 * 			Adds a cosmetic item to the users inventory
 * - GET	<base-uri>/user/{name}/inventory/{item}
 * 			retrieves an item from a users inventory by name
 * - GET	<base-uri>/user/{name}/inventory
 * 			retrieves all items from a users inventory
 * - DEL	<base-uri>/user/{name}/inventory
 * 			Deletes an item from the users inventory
 * - DEL	<base-uri>/user/{name}/inventory/{item}
 * 			Deletes a specific item from the users inventory
 * 
 * * Server
 * - POST	<base-uri>/server
 * 			Creates a new server (password is optional as null or non-null)
 * - GET	<base-uri>/server/{name}
 * 			Gets a servers details by its name
 * - PUT	<base-uri>/user/{name}/connect
 * 			Attempts to connect the user to a given server
 * 			An attached cookie parameter will contain a password if the server requires one
 * - GET 	<base-uri>/connect
 * 			An asynchronus service which notifies the called when a server is available
 * - DEL	<base-uri>/user/{name}/connect
 * 			Disconnects the users from its connected server.
 */
@Path("/game")
public class UserResource {
	private static Logger _logger = LoggerFactory
			.getLogger(UserResource.class);
	private static EntityManagerFactory _factory = PersistenceManager.getFactory();
	
	protected List<AsyncResponse> responses = new ArrayList<AsyncResponse>();
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
			em.createNativeQuery("DELETE FROM CRATE_ITEM c").executeUpdate();
			em.createNativeQuery("DELETE FROM Cosmetic c").executeUpdate();
			em.createNativeQuery("DELETE FROM MysteryBox m").executeUpdate();
			em.createNativeQuery("DELETE FROM INVENTORY_ITEM i").executeUpdate();
			em.createNativeQuery("DELETE FROM Item i").executeUpdate();
			em.createNativeQuery("DELETE FROM USER_SERVER u").executeUpdate();
			em.createNativeQuery("DELETE FROM Server s").executeUpdate();
			em.createNativeQuery("DELETE FROM User u").executeUpdate();
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
	@Consumes({"application/xml","application/json"})
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
	@Produces({"application/xml","application/json"})
	public Response getUser(@PathParam("name") String username) {
		User user = null;
		try {
		user = PersistenceManager.getUserByName(username);
		} catch (WebApplicationException e) {
			return Response.noContent().build();
		}
		UserDTO dtoUser = UserMapper.toDto(user);
		return Response.ok().entity(dtoUser).build();
	}
	
	/**
	 * Updates a users password as specified by a UserDTO payload.
	 * @param username - The users name
	 * @param dtoUser - A DTO containing the users new password
	 * @return
	 */
	@PUT
	@Path("/user/{name}")
	@Consumes({"application/xml","application/json"})
	@Produces({"application/xml","application/json"})
	public Response updateUserPassword(@PathParam("name") String username, UserDTO dtoUser) {
		User user = PersistenceManager.getUserByName(username);
		if (!user.getName().equals(dtoUser.getName())) {
			return Response.status(400).build();
		} else if (user.getPassword().equals(dtoUser.getPassword())) {
			return Response.ok().entity(user).build();
		} else {
			EntityManager em = _factory.createEntityManager();
			em.getTransaction().begin();
			user = em.find(User.class, user.getId());
			user.setPassword(dtoUser.getPassword());
			em.persist(user);
			em.getTransaction().commit();
			em.close();
			return Response.ok().entity(UserMapper.toDto(user)).build();
		}
	}
	
	/**
	 * Retrieves all users registered in the database.
	 * @return
	 */
	@GET
	@Path("/users")
	@Produces({"application/xml","application/json"})
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
		em.getTransaction().begin();
		em.createNativeQuery("DELETE FROM User u").executeUpdate();
		em.getTransaction().commit();
		em.close();
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
	 * Posts an item to the users inventory. An item can be either a Cosmetic item or
	 * a mystery box item. An item must be posted seperately to the /item/ uri before being
	 * associated with a user.
	 * @param username - A path parameters for the owner of the inventory
	 * @param dtoCosmetic - The cosmetic item to be given
	 * @return
	 */
	@POST
	@Path("/user/{name}/inventory")
	@Consumes({"application/xml","application/json"})
	public Response giveUserItem(@PathParam("name") String username, ItemDTO dtoItem) {
		if (dtoItem.getCosmetic() != null) {
			return giveUserCosmetic(username, dtoItem.getCosmetic());
		} else if (dtoItem.getMysteryBox() != null) {
			return giveUserMysteryBox(username, dtoItem.getMysteryBox());
		} else {
			_logger.error("BAD input item");
			_logger.error(dtoItem.toString());
			return Response.serverError().build();
		}
	}
	
	private Response giveUserCosmetic(String username, CosmeticDTO dtoCosmetic) {
		_logger.warn("Giving user Cosmetic...");
		User user = PersistenceManager.getUserByName(username);
		Cosmetic cosmetic = PersistenceManager.getCosmeticByName(dtoCosmetic.getName());
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
	
	private Response giveUserMysteryBox(String username, MysteryBoxDTO dtoMysteryBox) {
		_logger.warn("Giving user Mysterybox...");
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
	@Produces({"application/xml","application/json"})
	public List<ItemDTO> getUserInventory(@PathParam("name") String username) {
		List<ItemDTO> dtoItemList = new ArrayList<ItemDTO>();
		EntityManager em = _factory.createEntityManager();
		User user = em.find(User.class,PersistenceManager.getUserByName(username).getId());
		for (Item item : user.getInventory().getItems()) {
			dtoItemList.add(ItemMapper.toDto(item));
		}
		return dtoItemList;
	}
	
	/**
	 * Retrieves a users item.
	 * @param username - A path parameters for the owner of the item
	 * @param itemName - A path parameters for the name of the item to be retrieved
	 * @return
	 */
	@GET
	@Path("/user/{name}/inventory/{item}")
	@Produces({"application/xml","application/json"})
	public Response getUserItem(@PathParam("name") String username, @PathParam("item") String itemName) {
		Inventory invent = PersistenceManager.getUserInventory(username);
		for (Item item : invent.getItems()) {
			if (item.getName().equals(itemName)) {
				return Response.ok().entity(ItemMapper.toDto(item)).build();
			}
		}
		return Response.noContent().build();
	}

	/**
	 * Deletes all the items in a users inventory.
	 * @param username - The owner of the inventory
	 * @return
	 */
	@DELETE
	@Path("/user/{name}/inventory")
	@Consumes({"application/xml","application/json"})
	public Response deleteUserInventory(@PathParam("name") String username) {
		EntityManager em = _factory.createEntityManager();
		em.getTransaction().begin();
		User user = PersistenceManager.getUserByName(username);
		User syncedUser = em.find(User.class, user.getId());
		syncedUser.clearInventory();
		em.persist(syncedUser);
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
	@Consumes({"application/xml","application/json"})
	public Response deleteUserInventoryItem(@PathParam("name") String username, @PathParam("item") String itemName) {
		EntityManager em = _factory.createEntityManager();
		em.getTransaction().begin();
		User user = PersistenceManager.getUserByName(username);
		User syncedUser = em.find(User.class, user.getId());
		Inventory invent = syncedUser.getInventory();
		int itemId = 0;
		for (Item item : invent.getItems()) {
			if (item.getName().equals(itemName)) {
				itemId = item.getId();
				break;
			}
		}
		Item item = em.find(Item.class, itemId);
		syncedUser.removeFromInventory(item);
		em.persist(syncedUser);
		em.getTransaction().commit();
		em.close();
		return Response.ok().build();
	}
	
	
	/**
	 * Creates a server with supplied details.
	 * @param dtoServer
	 * @return
	 */
	@POST
	@Path("/server")
	@Consumes({"application/xml","application/json"})
	public Response createServer(ServerDTO dtoServer) {
		Server server = null;
		try {
			server = ServerMapper.toDomainModel(dtoServer);
			EntityManager em = _factory.createEntityManager();
			em.getTransaction().begin();
			em.persist(server);
			em.getTransaction().commit();
			em.close();
		} catch (PersistenceException e) {
			return Response.status(400).entity(e.getMessage()).build();
		}
		for (AsyncResponse response: responses) {
			response.resume(dtoServer);
		}
		responses.clear();
		return Response.created(URI.create("/server/" + server.getName())).build();
	}
	
	/**
	 * retrieves a servers details
	 * @param serverName
	 * @return
	 */
	@GET
	@Path("/server/{name}")
	@Produces({"application/xml","application/json"})
	public ServerDTO getServer(@PathParam("name") String serverName) {
		Server server = PersistenceManager.getServerByName(serverName);
		ServerDTO dtoServer = ServerMapper.toDto(server);
		return dtoServer;
	}
	
	/**
	 * An asynchronous method which notifies the caller when a server is available
	 * @param response
	 */
	@GET
	@Path("/connect")
	@Produces({"application/xml","application/json"})
	public void waitForAvailableServer(@Suspended AsyncResponse response) {
		responses.add(response);
	}
	
	
	/**
	 * Connects a user to a given server.
	 * An attached cookie contains any server passwords that are required.
	 * @param username
	 * @param password
	 * @param dtoServer
	 * @return
	 */
	@PUT
	@Path("/user/{name}/connect")
	@Produces({"application/xml","application/json"})
	public Response connectToServer(@PathParam("name") String username, @CookieParam("serverPassword") String password, ServerDTO dtoServer) {
		EntityManager em = _factory.createEntityManager();
		Server server = em.find(Server.class,PersistenceManager.getServerByName(dtoServer.getName()).getId());
		if (server.getPassword() != null) {
			if (!server.getPassword().equals(password)) {
				return Response.status(401).build();
			}
		}
		if (server.getOnlineUsers().size() < server.getCapacity()) {
			em.getTransaction().begin();
			User user = em.find(User.class, PersistenceManager.getUserByName(username).getId());
			user.setServer(server);
			em.persist(user);
			em.getTransaction().commit();
			em.close();
			return Response.ok().build();
		} else {
			return Response.seeOther(URI.create("/connect")).build();
		}
	}
	
	/**
	 * Disconnects a user from its server
	 * @param username
	 */
	@DELETE
	@Path("/user/{name}/connect")
	@Consumes({"application/xml","application/json"})
	public void disconnectFromServer(@PathParam("name") String username) {
		EntityManager em = _factory.createEntityManager();
		User user = em.find(User.class, PersistenceManager.getUserByName(username).getId());
		Server server = user.getServer();
		if (server == null) {
			throw new WebApplicationException(404);
		} else {
			em.getTransaction().begin();
			user.setServer(null);
			em.persist(user);
			em.getTransaction().commit();
			em.close();
			for (AsyncResponse response: responses) {
				response.resume(ServerMapper.toDto(server));
			}
			responses.clear();
		}
	}
	
}
