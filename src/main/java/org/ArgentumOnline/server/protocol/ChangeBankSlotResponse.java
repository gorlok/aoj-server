package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ChangeBankSlotResponse extends ServerPacket {
	// ChangeBankSlot,b:slot,i:objIndex,s:name,i:amount,i:grhIndex,b:objType,i:maxHIT,i:minHIT,i:def,l:valor
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ChangeBankSlot;
	}
	public byte slot;
	public short objIndex;
	public String name;
	public short amount;
	public short grhIndex;
	public byte objType;
	public short maxHIT;
	public short minHIT;
	public short def;
	public int valor;
	public ChangeBankSlotResponse(byte slot,short objIndex,String name,short amount,short grhIndex,byte objType,short maxHIT,short minHIT,short def,int valor){
		this.slot = slot;
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
	public static ChangeBankSlotResponse decode(ByteBuf in) {    
		try {                                   
			byte slot = readByte(in);
			short objIndex = readShort(in);
			String name = readStr(in);
			short amount = readShort(in);
			short grhIndex = readShort(in);
			byte objType = readByte(in);
			short maxHIT = readShort(in);
			short minHIT = readShort(in);
			short def = readShort(in);
			int valor = readInt(in);
			return new ChangeBankSlotResponse(slot,objIndex,name,amount,grhIndex,objType,maxHIT,minHIT,def,valor);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

