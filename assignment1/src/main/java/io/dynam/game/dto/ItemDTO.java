package io.dynam.game.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="item")
public class ItemDTO {
	
	@XmlElement
	private CosmeticDTO cosmetic = null;
	
	@XmlElement
	private MysteryBoxDTO mystery = null;
	
	protected ItemDTO() {}
	
	
	public ItemDTO(CosmeticDTO dtoCosmetic) {
		setCosmetic(cosmetic);
	}
	
	public ItemDTO(MysteryBoxDTO dtoMysteryBox) {
		setMysteryBox(dtoMysteryBox);
	}
	
	public CosmeticDTO getCosmetic() {
		return cosmetic;
	}
	
	private void setCosmetic(CosmeticDTO dtoCosmetic) {
		cosmetic = dtoCosmetic;
	}
	
	public MysteryBoxDTO getMysteryBox() {
		return mystery;
	}
	
	private void setMysteryBox(MysteryBoxDTO dtoMysteryBox) {
		mystery = dtoMysteryBox;
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
