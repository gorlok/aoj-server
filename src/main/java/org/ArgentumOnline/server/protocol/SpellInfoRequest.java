package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class SpellInfoRequest extends ClientPacket {
	// SpellInfo,b:spellSlot
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SpellInfo;
	}
	public byte spellSlot;
	public SpellInfoRequest(byte spellSlot){
		this.spellSlot = spellSlot;
	}
};

