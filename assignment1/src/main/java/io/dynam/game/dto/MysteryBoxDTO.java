package io.dynam.game.dto;

import io.dynam.game.domain.Cosmetic;
import io.dynam.game.services.CosmeticMapper;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@XmlRootElement(name="mystery")
@XmlAccessorType(XmlAccessType.FIELD)
public class MysteryBoxDTO {
	
	@XmlAttribute(name="id")
	private int _id;
	
	@XmlElement(name="name")
	private String _name;
	
	@XmlElement(name="contents")
	private Set<CosmeticDTO> _contents;
	
	protected MysteryBoxDTO() {}
	
	public MysteryBoxDTO(String name) {
		this(0, name, new HashSet<Cosmetic>());
	}
	
	public MysteryBoxDTO(String name, Set<Cosmetic> contents) {
		this(0, name, contents);
	}

	public MysteryBoxDTO(int id, String name, Set<Cosmetic> contents) {
		setId(id);
		setName(name);
		setContent(contents);
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
		_name = name.replace(' ','_');
	}
	
	public Set<CosmeticDTO> getContentDTO() {
		if (_contents == null) {
			setContentDTO(new HashSet<CosmeticDTO>());
		}
		return _contents;
	}
	
	public void setContentDTO(Set<CosmeticDTO> contents) {
		_contents = contents;
	}
	
	public Set<Cosmetic> getContent() {
		if (_contents == null) {
			setContentDTO(new HashSet<CosmeticDTO>());
		}
		Set<Cosmetic> content = new HashSet<Cosmetic>();
		for (CosmeticDTO dto : _contents) {
			content.add(CosmeticMapper.toDomainModel(dto));
		}
		return content;
	}
	
	public void setContent(Set<Cosmetic> content) {
		_contents = new HashSet<CosmeticDTO>();
		for (Cosmetic cos: content) {
			_contents.add(CosmeticMapper.toDto(cos));
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("MysteryBoxDTO: (");
		buffer.append(_id);
		buffer.append(", ");
		buffer.append(_name);
		buffer.append(", ");
		buffer.append(_contents.toArray());
		buffer.append(")");
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MysteryBoxDTO))
            return false;
        if (obj == this)
            return true;

        MysteryBoxDTO rhs = (MysteryBoxDTO) obj;
        return new EqualsBuilder().
            append(_id, rhs.getId()).
            append(_name, rhs.getName()).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_id).
	            append(_name).
	            toHashCode();
	}
}
