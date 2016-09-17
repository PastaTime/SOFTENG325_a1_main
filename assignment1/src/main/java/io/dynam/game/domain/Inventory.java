package io.dynam.game.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Embeddable
public class Inventory {
	
	private int _inventoryCapacity;
	
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(
			name = "INVENTORY_ITEM",
			joinColumns = @JoinColumn(name = "INVENTORY_ID", nullable = false),
			inverseJoinColumns = @JoinColumn(name = "ITEM_ID", nullable = false)
			)
	private Set<Item> _items;
	
	protected Inventory() {}
	
	public Inventory(int size) {
		_inventoryCapacity = size;
		_items = new HashSet<Item>();
	}
	
	public int getCapacity() {
		return _inventoryCapacity;
	}
	
	public void setCapacity(int capacity) {
		_inventoryCapacity = capacity;
	}
	
	public Set<Item> getItems() {
		return _items;
	}
	
	public void setItems(Set<Item> items) {
		_items = items;
	}
	
}
