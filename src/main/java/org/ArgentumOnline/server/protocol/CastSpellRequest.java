package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CastSpellRequest extends ClientPacket {
	// CastSpell,b:spell
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CastSpell;
	}
	public byte spell;
	public CastSpellRequest(byte spell){
		this.spell = spell;
	}
};

