package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ChangeNPCInventorySlotResponse extends ServerPacket {
	// ChangeNPCInventorySlot,b:slot,s:name,i:amount,f:price,i:grhIndex,i:objIndex,b:objType,i:maxHIT,i:minHIT,i:def
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ChangeNPCInventorySlot;
	}
	public byte slot;
	public String name;
	public short amount;
	public float price;
	public short grhIndex;
	public short objIndex;
	public byte objType;
	public short maxHIT;
	public short minHIT;
	public short def;
	public ChangeNPCInventorySlotResponse(byte slot,String name,short amount,float price,short grhIndex,short objIndex,byte objType,short maxHIT,short minHIT,short def){
		this.slot = slot;
		this.name = name;
		this.amount = amount;
		this.price = price;
		this.grhIndex = grhIndex;
		this.objIndex = objIndex;
		this.objType = objType;
		this.maxHIT = maxHIT;
		this.minHIT = minHIT;
		this.def = def;
	}
};

