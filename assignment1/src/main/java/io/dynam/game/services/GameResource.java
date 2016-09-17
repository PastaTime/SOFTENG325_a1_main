package io.dynam.game.services;

import io.dynam.game.dto.UserDTO;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import nz.ac.auckland.parolee.services.ParoleeResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/game")
public class GameResource {
	private static Logger _logger = LoggerFactory
			.getLogger(ParoleeResource.class);

	@GET
	@Path("/login")
	public Response register(@QueryParam("name") String username, @QueryParam("pass") String password) {
		if (username == null || password == null) {
			return Response.serverError().entity("ERROR").build();
		} else {
			//Query database and check user with password exists
			NewCookie userCookie = new NewCookie("user",username);
			return Response.ok().cookie(userCookie).build();
		}
	}
	
	@GET
	@Path("/user")
	public UserDTO getUser(@CookieParam("user") String username) {
		//Query Database for user of name
		return null;
	}

}
