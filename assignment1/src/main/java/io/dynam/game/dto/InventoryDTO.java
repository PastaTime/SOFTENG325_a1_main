package io.dynam.game.dto;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="inventory")
@XmlAccessorType(XmlAccessType.FIELD)
public class InventoryDTO {
	
	@XmlAttribute(name="capacity")
	private int _capacity;
	
	@XmlElement
	private Set<CosmeticDTO> _cosmetics;
	
	@XmlElement
	private Set<MysteryBoxDTO> _mystery;
	
	protected InventoryDTO() {}
	
	public InventoryDTO(int capacity) {
		this(10, new HashSet<CosmeticDTO>(), new HashSet<MysteryBoxDTO>());
	}
	
	public InventoryDTO(int capacity, Set<CosmeticDTO> cosmetic, Set<MysteryBoxDTO> mystery) {
		setCapacity(capacity);
		setCosmetic(cosmetic);
		setMysteryBoxs(mystery);
	}
	
	public int getCapacity() {
		return _capacity;
	}
	
	public void setCapacity(int capacity) {
		_capacity = capacity;
	}
	
	public Set<CosmeticDTO> getCosmetic() {
		return _cosmetics;
	}
	
	public void setCosmetic(Set<CosmeticDTO> cosmetics) {
		_cosmetics = cosmetics;
	}
	
	public Set<MysteryBoxDTO> getMysteryBoxs() {
		return _mystery;
	}
	
	public void setMysteryBoxs(Set<MysteryBoxDTO> mystery) {
		_mystery = mystery;
	}
}
