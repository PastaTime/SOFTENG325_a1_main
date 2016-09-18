package io.dynam.game.services;

import io.dynam.game.domain.User;
import io.dynam.game.dto.UserDTO;

public class UserMapper {

	//do we need password in DTO?
	public static User toDomainModel(UserDTO dtoUser) {
		User fullUser = new User(dtoUser.getId(), dtoUser.getName(),
				dtoUser.getPassword());

		return fullUser;
	}

	public static UserDTO toDto(User user) {
		UserDTO dtoUser = new UserDTO(user.getId(), user.getName(),
				user.getPassword());
		return dtoUser;
	}

}
