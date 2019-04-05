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

