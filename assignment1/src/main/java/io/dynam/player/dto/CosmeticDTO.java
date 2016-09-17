package io.dynam.player.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="cosmetic")
@XmlAccessorType(XmlAccessType.FIELD)
public class CosmeticDTO {
	
	@XmlAttribute(name="id")
	private int _id;
	
	@XmlElement(name="name")
	private String _name;
	
	@XmlElement(name="internalName")
	private String _internalName;
	
	protected CosmeticDTO() {}
	
	public CosmeticDTO(String name, String internalName) {
		this(0,name,internalName);
	}
	
	public CosmeticDTO(int id, String name, String internalName) {
		_id = id;
		_name = name;
		_internalName = internalName;
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
	
	public String getInternalName() {
		return _internalName;
	}
	
	public void setInternalName(String internalName) {
		_internalName = internalName;
	}
}	
