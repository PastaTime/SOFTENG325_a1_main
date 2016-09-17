package io.dynam.player.dto;

import io.dynam.player.domain.Cosmetic;
import io.dynam.player.services.CosmeticMapper;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
		_name = name;
	}
	
	public Set<CosmeticDTO> getContentDTO() {
		return _contents;
	}
	
	public void setContentDTO(Set<CosmeticDTO> contents) {
		_contents = contents;
	}
	
	public Set<Cosmetic> getContent() {
		Set<Cosmetic> content = new HashSet<Cosmetic>();
		for (CosmeticDTO dto : _contents) {
			content.add(CosmeticMapper.toDomainModel(dto));
		}
		return content;
	}
	
	public void setContent(Set<Cosmetic> content) {
		_contents.clear();
		for (Cosmetic cos: content) {
			_contents.add(CosmeticMapper.toDto(cos));
		}
	}
}
