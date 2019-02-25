package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class CheckSlotRequest extends ClientPacket {
	// CheckSlot,s:userName,b:slot
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CheckSlot;
	}
	public String userName;
	public byte slot;
	public CheckSlotRequest(String userName,byte slot){
		this.userName = userName;
		this.slot = slot;
	}
};

