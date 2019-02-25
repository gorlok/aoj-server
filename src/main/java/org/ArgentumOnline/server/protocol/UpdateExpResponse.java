package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UpdateExpResponse extends ServerPacket {
	// UpdateExp,l:exp
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UpdateExp;
	}
	public int exp;
	public UpdateExpResponse(int exp){
		this.exp = exp;
	}
};

