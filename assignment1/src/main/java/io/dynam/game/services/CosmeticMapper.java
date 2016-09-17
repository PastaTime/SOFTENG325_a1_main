package io.dynam.game.services;

import io.dynam.game.domain.Cosmetic;
import io.dynam.game.domain.User;
import io.dynam.game.dto.CosmeticDTO;
import io.dynam.game.dto.UserDTO;

public class CosmeticMapper {

	//do we need password in DTO?
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

}
