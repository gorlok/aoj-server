package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class EquipItemRequest extends ClientPacket {
	// EquipItem,b:itemSlot
	@Override
	public ClientPacketID id() {
		return ClientPacketID.EquipItem;
	}
	public byte itemSlot;
	public EquipItemRequest(byte itemSlot){
		this.itemSlot = itemSlot;
	}
};

