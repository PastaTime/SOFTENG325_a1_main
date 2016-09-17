package io.dynam.game.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Embeddable
public class Inventory {
	
	public static final Inventory DEFAULT = new Inventory(10);
	
	private int _maxCapacity;
	
	@ManyToMany
	@JoinTable(
			name = "INVENTORY_ITEM",
			joinColumns = @JoinColumn(name = "INVENTORY_ID", nullable = false),
			inverseJoinColumns = @JoinColumn(name = "ITEM_ID", nullable = false)
			)
	private Set<Item> _items;
	
	protected Inventory() {}
	
	public Inventory(int size) {
		_maxCapacity = size;
		_items = new HashSet<Item>();
	}
	
}
