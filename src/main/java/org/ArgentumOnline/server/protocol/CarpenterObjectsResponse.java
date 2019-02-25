package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CarpenterObjectsResponse extends ServerPacket {
	// CarpenterObjects,i:count,(s:name,i:madera,i:index)[.]:objects
	@Override
	public ServerPacketID id() {
		return ServerPacketID.CarpenterObjects;
	}
	public short count;
	public CarpenterObjects_DATA[] objects;
	public CarpenterObjectsResponse(short count,CarpenterObjects_DATA[] objects){
		this.count = count;
		this.objects = objects;
	}
};

