package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class MoveSpellRequest extends ClientPacket {
	// MoveSpell,b:dir,b:spell
	@Override
	public ClientPacketID id() {
		return ClientPacketID.MoveSpell;
	}
	public byte dir;
	public byte spell;
	public MoveSpellRequest(byte dir,byte spell){
		this.dir = dir;
		this.spell = spell;
	}
};

