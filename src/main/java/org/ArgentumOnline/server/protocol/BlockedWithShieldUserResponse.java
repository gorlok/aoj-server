package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class BlockedWithShieldUserResponse extends ServerPacket {
	// BlockedWithShieldUser
	@Override
	public ServerPacketID id() {
		return ServerPacketID.BlockedWithShieldUser;
	}
	public BlockedWithShieldUserResponse(){
	}
};

