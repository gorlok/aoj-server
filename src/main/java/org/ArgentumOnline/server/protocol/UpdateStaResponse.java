package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UpdateStaResponse extends ServerPacket {
	// UpdateSta,i:minSta
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UpdateSta;
	}
	public short minSta;
	public UpdateStaResponse(short minSta){
		this.minSta = minSta;
	}
};

