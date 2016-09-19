package io.dynam.game.utils;

import io.dynam.game.domain.Cosmetic;
import io.dynam.game.domain.Item;
import io.dynam.game.domain.MysteryBox;
import io.dynam.game.dto.ItemDTO;

public class ItemMapper {

	public static Item toDomainModel(ItemDTO dtoItem) {
		if (dtoItem.getCosmetic() != null) {
			return CosmeticMapper.toDomainModel(dtoItem.getCosmetic());
		} else {
			return MysteryBoxMapper.toDomainModel(dtoItem.getMysteryBox());
		}
	}
	
	public static ItemDTO toDto(Cosmetic cosmetic) {
		return new ItemDTO(CosmeticMapper.toDto(cosmetic));
	}
	
	public static ItemDTO toDto(MysteryBox mysteryBox) {
		return new ItemDTO(MysteryBoxMapper.toDto(mysteryBox));
	}
	public static ItemDTO toDto(Item item) {
		if (item instanceof Cosmetic) {
			return toDto((Cosmetic) item);
		} else if (item instanceof MysteryBox) {
			return toDto((MysteryBox) item);
		} else {
			return null;
		}
	}
}
