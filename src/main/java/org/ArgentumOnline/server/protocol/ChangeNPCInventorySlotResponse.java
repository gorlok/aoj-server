package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static ChangeNPCInventorySlotResponse decode(ByteBuf in) {    
		try {                                   
			byte slot = readByte(in);
			String name = readStr(in);
			short amount = readShort(in);
			float price = readFloat(in);
			short grhIndex = readShort(in);
			short objIndex = readShort(in);
			byte objType = readByte(in);
			short maxHIT = readShort(in);
			short minHIT = readShort(in);
			short def = readShort(in);
			return new ChangeNPCInventorySlotResponse(slot,name,amount,price,grhIndex,objIndex,objType,maxHIT,minHIT,def);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeByte(out,slot);
		writeStr(out,name);
		writeShort(out,amount);
		writeFloat(out,price);
		writeShort(out,grhIndex);
		writeShort(out,objIndex);
		writeByte(out,objType);
		writeShort(out,maxHIT);
		writeShort(out,minHIT);
		writeShort(out,def);
	}
};

