package io.dynam.game.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="server")
public class ServerDTO {
	
	@XmlAttribute(name="id")
	private int _id;
	
	@XmlElement(name="name")
	private String _name;
	
	@XmlElement(name="capacity")
	private int _capacity;
	
	@XmlElement(name="pass")
	private String _password;
	
	protected ServerDTO() {}
	
	public ServerDTO(String name, String password) {
		this(name,10,password);
	}
	
	public ServerDTO(String name, int capacity) {
		this(name,capacity,null);
	}
	
	public ServerDTO(String name) {
		this(name,10,null);
	}
	
	public ServerDTO(String name, int capacity, String password) {
		_name =  name;
		_capacity = capacity;
		_password = password;
	}
	
	public int getId() {
		return _id;
	}
	
	public void setId(int id) {
		_id = id;
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
	
	public String getPassword() {
		return _password;
	}
	
	public void setPassword(String password) {
		_password = password;
	}
	

}
