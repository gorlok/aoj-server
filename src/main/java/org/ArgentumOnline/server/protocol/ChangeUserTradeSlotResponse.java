package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static ChangeUserTradeSlotResponse decode(ByteBuf in) {    
		try {                                   
			short objIndex = readShort(in);
			String name = readStr(in);
			int amount = readInt(in);
			short grhIndex = readShort(in);
			byte objType = readByte(in);
			short maxHIT = readShort(in);
			short minHIT = readShort(in);
			short def = readShort(in);
			int valor = readInt(in);
			return new ChangeUserTradeSlotResponse(objIndex,name,amount,grhIndex,objType,maxHIT,minHIT,def,valor);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeShort(out,objIndex);
		writeStr(out,name);
		writeInt(out,amount);
		writeShort(out,grhIndex);
		writeByte(out,objType);
		writeShort(out,maxHIT);
		writeShort(out,minHIT);
		writeShort(out,def);
		writeInt(out,valor);
	}
};

