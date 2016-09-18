package io.dynam.game.services;

import java.util.HashSet;
import java.util.Set;

import io.dynam.game.domain.Cosmetic;
import io.dynam.game.domain.Inventory;
import io.dynam.game.domain.Item;
import io.dynam.game.domain.MysteryBox;
import io.dynam.game.dto.CosmeticDTO;
import io.dynam.game.dto.InventoryDTO;
import io.dynam.game.dto.MysteryBoxDTO;

public class InventoryMapper {
	public static Inventory toDomainModel(InventoryDTO dtoInventory) {
		Set<Item> items = new HashSet<Item>();
		for (CosmeticDTO dtoCosmetic : dtoInventory.getCosmetic()) {
			items.add(CosmeticMapper.toDomainModel(dtoCosmetic));
		}
		for (MysteryBoxDTO dtoMystery : dtoInventory.getMysteryBoxs()) {
			items.add(MysteryBoxMapper.toDomainModel(dtoMystery));
		}
		Inventory inventory = new Inventory(dtoInventory.getCapacity(), items);
		return inventory;
	}
	
	public static InventoryDTO toDto(Inventory inventory) {
		Set<CosmeticDTO> dtoCosmeticSet = new HashSet<CosmeticDTO>();
		Set<MysteryBoxDTO> dtoMysteryBoxSet = new HashSet<MysteryBoxDTO>();
		for (Item item : inventory.getItems()) {
			if (item instanceof Cosmetic) {
				dtoCosmeticSet.add(CosmeticMapper.toDto((Cosmetic)item));
			} else if (item instanceof MysteryBox) {
				dtoMysteryBoxSet.add(MysteryBoxMapper.toDto((MysteryBox)item));
			}
		}
		InventoryDTO dtoInventory = new InventoryDTO(inventory.getCapacity(), dtoCosmeticSet, dtoMysteryBoxSet);
		return dtoInventory;
	}
}
