package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CharacterChangeResponse extends ServerPacket {
	// CharacterChange,i:charIndex,i:body,i:head,b:heading,i:weapon,i:shield,i:helmet,i:fx,i:fxLoops
	@Override
	public ServerPacketID id() {
		return ServerPacketID.CharacterChange;
	}
	public short charIndex;
	public short body;
	public short head;
	public byte heading;
	public short weapon;
	public short shield;
	public short helmet;
	public short fx;
	public short fxLoops;
	public CharacterChangeResponse(short charIndex,short body,short head,byte heading,short weapon,short shield,short helmet,short fx,short fxLoops){
		this.charIndex = charIndex;
		this.body = body;
		this.head = head;
		this.heading = heading;
		this.weapon = weapon;
		this.shield = shield;
		this.helmet = helmet;
		this.fx = fx;
		this.fxLoops = fxLoops;
	}
};

