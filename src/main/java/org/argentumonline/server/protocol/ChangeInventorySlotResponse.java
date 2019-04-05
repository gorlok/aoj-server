/*******************************************************************************
 *     Gorlok AO, an implementation of Argentum Online using Java.
 *     Copyright (C) 2019 Pablo Fernando Lillia «gorlok» 
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.argentumonline.server.protocol;

import org.argentumonline.server.net.*;

import io.netty.buffer.ByteBuf;

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
	public static ChangeInventorySlotResponse decode(ByteBuf in) {    
		try {                                   
			byte slot = readByte(in);
			short objIndex = readShort(in);
			String name = readStr(in);
			short amount = readShort(in);
			byte equiped = readByte(in);
			short grhIndex = readShort(in);
			byte objType = readByte(in);
			short maxHIT = readShort(in);
			short minHIT = readShort(in);
			short def = readShort(in);
			float valor = readFloat(in);
			return new ChangeInventorySlotResponse(slot,objIndex,name,amount,equiped,grhIndex,objType,maxHIT,minHIT,def,valor);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeByte(out,slot);
		writeShort(out,objIndex);
		writeStr(out,name);
		writeShort(out,amount);
		writeByte(out,equiped);
		writeShort(out,grhIndex);
		writeByte(out,objType);
		writeShort(out,maxHIT);
		writeShort(out,minHIT);
		writeShort(out,def);
		writeFloat(out,valor);
	}
};

