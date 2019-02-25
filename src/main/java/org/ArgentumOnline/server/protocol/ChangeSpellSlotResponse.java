package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ChangeSpellSlotResponse extends ServerPacket {
	// ChangeSpellSlot,b:slot,i:spell,s:name
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ChangeSpellSlot;
	}
	public byte slot;
	public short spell;
	public String name;
	public ChangeSpellSlotResponse(byte slot,short spell,String name){
		this.slot = slot;
		this.spell = spell;
		this.name = name;
	}
};

