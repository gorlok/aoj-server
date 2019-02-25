package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UpdateManaResponse extends ServerPacket {
	// UpdateMana,i:minMan
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UpdateMana;
	}
	public short minMan;
	public UpdateManaResponse(short minMan){
		this.minMan = minMan;
	}
};

