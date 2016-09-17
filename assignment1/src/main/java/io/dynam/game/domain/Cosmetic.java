package io.dynam.game.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Entity
public class Cosmetic extends Item {

	@Column(nullable = false, unique = true)
	private String _internalName; //Possibility of loading an enum derived from App properties
	
	@OneToMany(mappedBy = "_cosmetic")
	private Set<User> _inUse;
	
	protected Cosmetic() {}
	
	public Cosmetic(String name, String assetName) {
		this(0, name, assetName);
	}
	
	
	public Cosmetic(int id, String name, String assetName) {
		super(id, name);
		_internalName = assetName;
		_inUse  = new HashSet<User>();
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
	
	public String getInternalName() {
		return _internalName;
	}
	
	public void setInternalName(String internalName) {
		_internalName = internalName;
	}
	
}
