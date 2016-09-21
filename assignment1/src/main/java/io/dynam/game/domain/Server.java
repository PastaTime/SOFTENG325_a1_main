package io.dynam.game.domain;

import java.util.Set;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
public class Server {
	@Id
	@GeneratedValue
	private int _id;
	private String _name;
	private int _capacity;
	
	@OneToMany(mappedBy="_server")
	private Set<User> _online;

	protected Server() {}

	public Server(String name, int capacity) {
		_id = 0;
		_name = name;
		_capacity = capacity;
	}
	
	public Server(int id, String name, int capacity) {
		_id = id;
		_name = name;
		_capacity = capacity;
	}
	
	public String getName() {
		return _name;
	}
	
	public void setName(String name) {
		_name = name;
	}
	
	public int getCapacity() {
		return _capacity;
	}
	
	public void setCapacity(int capacity) {
		_capacity = capacity;
	}
	
	public int getId() {
		return _id;
	}
	
	public void setId(int id) {
		_id = id;
	}
	
	public Set<User> getOnlineUsers() {
		return _online;
	}

}
