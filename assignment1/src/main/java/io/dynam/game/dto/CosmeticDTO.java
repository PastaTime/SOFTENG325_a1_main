package io.dynam.game.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
		this(0,name.replace(' ','_'),internalName.replace(' ','_'));
	}
	
	public CosmeticDTO(int id, String name, String internalName) {
		_id = id;
		_name = name.replace(' ','_');
		_internalName = internalName.replace(' ','_');
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
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("CosmeticDTO: (");
		buffer.append(_id);
		buffer.append(", ");
		buffer.append(_name);
		buffer.append(", ");
		buffer.append(_internalName);
		buffer.append(")");
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CosmeticDTO))
            return false;
        if (obj == this)
            return true;

        CosmeticDTO rhs = (CosmeticDTO) obj;
        return new EqualsBuilder().
            append(_id, rhs.getId()).
            append(_name, rhs.getName()).
            append(_internalName, rhs.getInternalName()).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_id).
	            append(_name).
	            append(_internalName).
	            toHashCode();
	}
}	
