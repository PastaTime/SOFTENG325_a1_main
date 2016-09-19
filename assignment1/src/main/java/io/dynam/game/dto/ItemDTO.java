package io.dynam.game.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="item")
public class ItemDTO {
	
	@XmlElement(name = "cosmeticSlot")
	private CosmeticDTO cosmetic = null;
	
	@XmlElement(name = "mysterySlot")
	private MysteryBoxDTO mystery = null;
	
	protected ItemDTO() {}
	
	
	public ItemDTO(CosmeticDTO dtoCosmetic) {
		cosmetic = dtoCosmetic;
	}
	
	public ItemDTO(MysteryBoxDTO dtoMysteryBox) {
		mystery = dtoMysteryBox;
	}
	
	public CosmeticDTO getCosmetic() {
		return cosmetic;
	}
	
	public MysteryBoxDTO getMysteryBox() {
		return mystery;
	}
	
	@Override
	public String toString() {
		if (this.getCosmetic() != null) {
			return "Item(As Cosmetic): {" + this.getCosmetic().toString() + "}";
		} else if (this.getMysteryBox() != null) {
			return "Item(As MysteryBox): {" + this.getMysteryBox().toString() + "}";
		} else {
			return "{BAD ITEM}";
		}
	}
	

}
