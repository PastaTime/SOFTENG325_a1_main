package io.dynam.game.domain;

import java.util.Set;

import javax.persistence.*;

@Entity
public class Server {
	@Id
	@GeneratedValue
	private int _id;
	private String _name;
	
	@OneToMany(mappedBy="_server")
	private Set<User> _online;

	protected Server() {}

	public Server(String name) {
		_name = name;
	}
	

}
