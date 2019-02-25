package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ChangeMapResponse extends ServerPacket {
	// ChangeMap,i:map,i:version
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ChangeMap;
	}
	public short map;
	public short version;
	public ChangeMapResponse(short map,short version){
		this.map = map;
		this.version = version;
	}
};

