package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class BlockedWithShieldOtherResponse extends ServerPacket {
	// BlockedWithShieldOther
	@Override
	public ServerPacketID id() {
		return ServerPacketID.BlockedWithShieldOther;
	}
	public BlockedWithShieldOtherResponse(){
	}
};

