package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ChangeInventorySlotResponse extends ServerPacket {
	// ChangeInventorySlot,b:slot,i:objIndex,s:name,i:amount,b:equiped,i:grhIndex,b:objType,i:maxHIT,i:minHIT,i:def,f:valor
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ChangeInventorySlot;
	}
	public byte slot;
	public short objIndex;
	public String name;
	public short amount;
	public byte equiped;
	public short grhIndex;
	public byte objType;
	public short maxHIT;
	public short minHIT;
	public short def;
	public float valor;
	public ChangeInventorySlotResponse(byte slot,short objIndex,String name,short amount,byte equiped,short grhIndex,byte objType,short maxHIT,short minHIT,short def,float valor){
		this.slot = slot;
		this.objIndex = objIndex;
		this.name = name;
		this.amount = amount;
		this.equiped = equiped;
		this.grhIndex = grhIndex;
		this.objType = objType;
		this.maxHIT = maxHIT;
		this.minHIT = minHIT;
		this.def = def;
		this.valor = valor;
	}
};

