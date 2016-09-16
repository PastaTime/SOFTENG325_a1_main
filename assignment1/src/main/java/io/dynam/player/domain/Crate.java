package io.dynam.player.domain;

import java.util.Set;

import javax.persistence.*;

@Entity
public class Crate extends Item {
	
	@Id
	@GeneratedValue
	private int _id;
	
	@ManyToMany
	@JoinTable(
			name = "CRATE_ITEM",
			joinColumns = @JoinColumn(name = "CRATE_ID", nullable = false),
			inverseJoinColumns = @JoinColumn(name = "ITEM_ID", nullable = false)
			)
	private Set<Cosmetic> _contents;
	
	protected Crate() {}
}
