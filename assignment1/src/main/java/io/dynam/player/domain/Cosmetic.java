package io.dynam.player.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Entity
public class Cosmetic extends Item {
	
	public static final Cosmetic DEFAULT = new Cosmetic(0, "DEFAULT_COSMETIC", "DEFAULT_COSMETIC_NAME");

	@Column(nullable = false, unique = true)
	private String _internalName; //Possibility of loading an enum derived from App properties
	
	@OneToMany(mappedBy = "_cosmetic")
	private Set<User> _inUse;
	
	protected Cosmetic() {}
	
	public Cosmetic(int id, String name, String assetName) {
		super(id, name);
		_internalName = assetName;
		_inUse  = new HashSet<User>();
	}

}
