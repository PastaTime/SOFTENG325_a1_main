package io.dynam.game.services;

import java.util.ArrayList;
import java.util.List;

import io.dynam.game.domain.Cosmetic;
import io.dynam.game.domain.User;
import io.dynam.game.dto.CosmeticDTO;
import io.dynam.game.dto.UserDTO;

public class CosmeticMapper {

	public static Cosmetic toDomainModel(CosmeticDTO dtoCosmetic) {
		Cosmetic fullCosmetic = new Cosmetic(dtoCosmetic.getId(), dtoCosmetic.getName(),
				dtoCosmetic.getInternalName());

		return fullCosmetic;
	}
	
	public static CosmeticDTO toDto(Cosmetic cosmetic) {
		CosmeticDTO dtoCosmetic = new CosmeticDTO(cosmetic.getId(), cosmetic.getName(),
				cosmetic.getInternalName());
		return dtoCosmetic;
	}

	public static List<Cosmetic> toDomainModel(List<CosmeticDTO> dtoCosmeticList) {
		List<Cosmetic> cosmeticList = new ArrayList<Cosmetic>();
		for (CosmeticDTO dto: dtoCosmeticList) {
			cosmeticList.add(toDomainModel(dto));
		}
		return cosmeticList;
	}

	public static List<CosmeticDTO> toDto(List<Cosmetic> cosmeticList) {
		List<CosmeticDTO> dtoCosmeticList = new ArrayList<CosmeticDTO>();
		for (Cosmetic cosmetic: cosmeticList) {
			dtoCosmeticList.add(toDto(cosmetic));
		}
		return dtoCosmeticList;
	}
}
