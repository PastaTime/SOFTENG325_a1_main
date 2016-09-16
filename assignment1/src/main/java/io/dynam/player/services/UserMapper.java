package io.dynam.player.services;

import io.dynam.player.domain.User;
import io.dynam.player.dto.UserDTO;

public class UserMapper {

	//do we need password in DTO?
	static User toDomainModel(UserDTO dtoUser) {
		User fullUser = new User(dtoUser.getId(), dtoUser.getName(),
				dtoUser.getPassword());

		return fullUser;
	}

	static UserDTO toDto(User user) {
		UserDTO dtoUser = new UserDTO(user.getId(), user.getName(),
				user.getPassword());
		return dtoUser;
	}

}
