package io.dynam.game.services;

import io.dynam.game.domain.User;
import io.dynam.game.dto.UserDTO;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
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
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/game")
public class GameResource {
	//TODO: implement proper logging
	private static Logger _logger = LoggerFactory
			.getLogger(GameResource.class);
	private static EntityManagerFactory _factory = Persistence
			.createEntityManagerFactory("io.dynam.game");

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
	 * Base-URI: /game
	 * 
	 * - GET 	<base-uri>/user
	 * 			retrieves a user DTO object from the data base.
	 * 			Requires correct login cookie obtained.
	 * - POST 	<base-uri>/user
	 * 			posts a user DTO to the database.
	 * 			TODO: Requires Admin
	 * - PUT    <base-uri>/user/password
	 * 			TODO: updates a users password (XML containing new and old password?)
	 */
	@GET
	@Path("/user")
	@Produces("application/xml")
	public UserDTO getUser(@CookieParam("user") String username) {
		// Query Database for user of name
		if (username == null) {
			_logger.error("Incoming query has no username attribute on cookie");
			throw new WebApplicationException("Invalid Cookie");
		}
		EntityManager em = _factory.createEntityManager();
		TypedQuery<User> query = em.createQuery(
				"from User u where u._name = :uname", User.class)
				.setParameter("uname", username);
		User user = query.getSingleResult();
		// NonUniqueResultException and NoResultException
		if (user == null) {
			// Response User Not found
		}
		UserDTO dtoUser = UserMapper.toDto(user);

		return dtoUser;
	}
	
	@POST
	@Path("/user")
	@Consumes("application/xml")
	public Response createUser(UserDTO dtoUser) {
		_logger.info("creating User....");
		User user = UserMapper.toDomainModel(dtoUser);
		EntityManager em = _factory.createEntityManager();
		em.getTransaction().begin();
		em.persist(user);
		em.getTransaction().commit();
		em.close();
		return Response.ok().build();
	}
}
