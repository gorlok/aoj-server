package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CraftCarpenterRequest extends ClientPacket {
	// CraftCarpenter,i:item
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CraftCarpenter;
	}
	public short item;
	public CraftCarpenterRequest(short item){
		this.item = item;
	}
};

