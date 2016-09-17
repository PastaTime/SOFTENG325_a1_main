package io.dynam.game.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="user")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserDTO {
	
	@XmlAttribute(name="id")
	private int _id;
	
	@XmlElement(name="username")
	private String _name;
	
	@XmlElement(name="pass")
	private String _password;
	
	public UserDTO(String username, String password) {
		this(0, username, password);
	}
	
	public UserDTO(int id, String username, String password) {
		setId(id);
		setName(username);
		setPassword(password);
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
	
	
	

}
