package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class BlacksmithWeaponsResponse extends ServerPacket {
	// BlacksmithWeapons,i:count,(s:name,i:lingH,i:lingP,i:lingO,i:index)[.]:weapons
	@Override
	public ServerPacketID id() {
		return ServerPacketID.BlacksmithWeapons;
	}
	public short count;
	public BlacksmithWeapons_DATA[] weapons;
	public BlacksmithWeaponsResponse(short count,BlacksmithWeapons_DATA[] weapons){
		this.count = count;
		this.weapons = weapons;
	}
};

