package io.dynam.game.domain;

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
}
