package io.dynam.player.domain;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Item {
	@Id
	@GeneratedValue
	private int _id;
	
	@Column(nullable = false, unique = true)
	private String _name;

	protected Item() {}
	
	public Item(int id, String name) {
		_id = id;
		_name = name;
	}
	
	protected int getId() {
		return _id;
	}
	
	protected void setId(int id) {
		_id = id;
	}
	
	protected String getName() {
		return _name;
	}
	
	protected void setName(String name) {
		_name = name;
	}
}
