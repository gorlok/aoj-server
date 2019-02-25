package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ChangeUserTradeSlotResponse extends ServerPacket {
	// ChangeUserTradeSlot,i:objIndex,s:name,l:amount,i:grhIndex,b:objType,i:maxHIT,i:minHIT,i:def,l:valor
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ChangeUserTradeSlot;
	}
	public short objIndex;
	public String name;
	public int amount;
	public short grhIndex;
	public byte objType;
	public short maxHIT;
	public short minHIT;
	public short def;
	public int valor;
	public ChangeUserTradeSlotResponse(short objIndex,String name,int amount,short grhIndex,byte objType,short maxHIT,short minHIT,short def,int valor){
		this.objIndex = objIndex;
		this.name = name;
		this.amount = amount;
		this.grhIndex = grhIndex;
		this.objType = objType;
		this.maxHIT = maxHIT;
		this.minHIT = minHIT;
		this.def = def;
		this.valor = valor;
	}
};

