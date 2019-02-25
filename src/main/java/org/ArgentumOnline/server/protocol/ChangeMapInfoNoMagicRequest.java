package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ChangeMapInfoNoMagicRequest extends ClientPacket {
	// ChangeMapInfoNoMagic,b:noMagic
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeMapInfoNoMagic;
	}
	public byte noMagic;
	public ChangeMapInfoNoMagicRequest(byte noMagic){
		this.noMagic = noMagic;
	}
};

