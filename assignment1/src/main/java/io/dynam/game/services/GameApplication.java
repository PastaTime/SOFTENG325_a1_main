package io.dynam.game.services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class GameApplication extends Application {
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> classes = new HashSet<Class<?>>();

	public GameApplication() {
		singletons.add(new GameResource());
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
