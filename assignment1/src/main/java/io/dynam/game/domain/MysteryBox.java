package io.dynam.game.domain;

import java.util.Set;

import javax.persistence.*;

@Entity
public class MysteryBox extends Item {

	@ManyToMany
	@JoinTable(name = "CRATE_ITEM", joinColumns = @JoinColumn(name = "CRATE_ID", nullable = false), inverseJoinColumns = @JoinColumn(name = "ITEM_ID", nullable = false))
	private Set<Cosmetic> _contents;

	public MysteryBox(String name) {
		super(0, name);
	}

	public MysteryBox(int id, String name, Set<Cosmetic> contents) {
		super(id, name);
		_contents = contents;
	}
	
	public int getId() {
		return super.getId();
	}
	
	public void setId(int id) {
		super.setId(id);
	}
	
	public String getName() {
		return super.getName();
	}
	
	public void setName(String name) {
		super.setName(name);
	}
	
	public Set<Cosmetic> getContents() {
		return _contents;
	}
	
	public void setContents(Set<Cosmetic> contents) {
		_contents = contents;
	}
}
