package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CraftBlacksmithRequest extends ClientPacket {
	// CraftBlacksmith,i:item
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CraftBlacksmith;
	}
	public short item;
	public CraftBlacksmithRequest(short item){
		this.item = item;
	}
};

