package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UserIndexInServerResponse extends ServerPacket {
	// UserIndexInServer,i:userIndex
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UserIndexInServer;
	}
	public short userIndex;
	public UserIndexInServerResponse(short userIndex){
		this.userIndex = userIndex;
	}
};

