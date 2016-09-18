package io.dynam.game.services;

import io.dynam.game.services.item.ItemResource;
import io.dynam.game.services.user.UserResource;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/services")
public class GameApplication extends Application {
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> classes = new HashSet<Class<?>>();

	public GameApplication() {
		singletons.add(new UserResource());
		singletons.add(new ItemResource());
//		classes.add(GameResolver.class);
		//Set up default values
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}
}
