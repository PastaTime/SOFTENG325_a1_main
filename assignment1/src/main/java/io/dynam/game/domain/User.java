package io.dynam.game.domain;

import javax.persistence.*;

@Entity
public class User {

	@Id
	@GeneratedValue
	private int _id;
	
	@Column(name="NAME",nullable = false, unique = true)
	private String _name;
	
	@Column(name="PASS", nullable = false)
	private String _password;
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinTable(
			name = "USER_SERVER",
			joinColumns = @JoinColumn(unique = true))
	private Server _server;
	
	@ManyToOne(fetch = FetchType.LAZY,
			cascade = CascadeType.PERSIST)
	@JoinColumn
	private Cosmetic _cosmetic;
	
	@Embedded
	private Inventory _invent;
	
	protected User() {}
	
	public User(int id, String name, String pass) {
		setId(id);
		setName(name);
		setPassword(pass);
		_server = null;
		_cosmetic = null;
		_invent = new Inventory(10);
	}

	public int getId() {
		return _id;
	}

	public void setId(int _id) {
		this._id = _id;
	}

	public String getName() {
		return _name;
	}

	public void setName(String _name) {
		this._name = _name;
	}

	public String getPassword() {
		return _password;
	}

	public void setPassword(String _password) {
		this._password = _password;
	}
	
	public Inventory getInventory() {
		return _invent;
	}
	
	public void setInventory(Inventory invent) {
		_invent = invent;
	}
	
}
