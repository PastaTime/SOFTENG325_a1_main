package io.dynam.game.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@XmlRootElement(name="user")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserDTO {
	
	@XmlAttribute(name="id")
	private int _id;
	
	@XmlElement(name="username")
	private String _name;
	
	@XmlElement(name="pass")
	private String _password;
	
	protected UserDTO() {}
	
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
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("UserDTO: (");
		buffer.append(_id);
		buffer.append(", ");
		buffer.append(_name);
		buffer.append(", ");
		buffer.append(_password);
		buffer.append(")");
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UserDTO))
            return false;
        if (obj == this)
            return true;

        UserDTO rhs = (UserDTO) obj;
        return new EqualsBuilder().
            append(_id, rhs._id).
            append(_name, rhs._name).
            append(_password, rhs._password).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_id).
	            append(_name).
	            append(_password).
	            toHashCode();
	}
	
	

}
