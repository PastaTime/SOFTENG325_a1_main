package io.dynam.game.utils;

import io.dynam.game.domain.Cosmetic;
import io.dynam.game.domain.Server;
import io.dynam.game.dto.CosmeticDTO;
import io.dynam.game.dto.ServerDTO;

public class ServerMapper {
	public static Server toDomainModel(ServerDTO dtoServer) {
		Server fullServer = new Server(dtoServer.getId(), dtoServer.getName(),
				dtoServer.getCapacity(), dtoServer.getPassword());

		return fullServer;
	}
	
	public static ServerDTO toDto(Server server) {
		ServerDTO dtoServer = new ServerDTO(server.getName(),
				server.getCapacity(), server.getPassword());
		return dtoServer;
	}
}
