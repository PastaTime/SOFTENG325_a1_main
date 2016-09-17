package io.dynam.player.services;

import io.dynam.player.domain.MysteryBox;
import io.dynam.player.dto.MysteryBoxDTO;

public class MysteryBoxMapper {
	//do we need password in DTO?
		public static MysteryBox toDomainModel(MysteryBoxDTO dtoMysteryBox) {
			MysteryBox fullMysteryBox = new MysteryBox(dtoMysteryBox.getId(), dtoMysteryBox.getName(),
					dtoMysteryBox.getContent());

			return fullMysteryBox;
		}

		public static MysteryBoxDTO toDto(MysteryBox mysteryBox) {
			MysteryBoxDTO dtoMysteryBox = new MysteryBoxDTO(mysteryBox.getId(), mysteryBox.getName(),
					mysteryBox.getContents());
			return dtoMysteryBox;
		}
}
