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

public class CharacterCreateResponse extends ServerPacket {
	// CharacterCreate,i:charIndex,i:body,i:head,b:heading,b:x,b:y,i:weapon,i:shield,i:helmet,i:fx,i:fxLoops,s:name,b:criminal,b:privileges
	@Override
	public ServerPacketID id() {
		return ServerPacketID.CharacterCreate;
	}
	public short charIndex;
	public short body;
	public short head;
	public byte heading;
	public byte x;
	public byte y;
	public short weapon;
	public short shield;
	public short helmet;
	public short fx;
	public short fxLoops;
	public String name;
	public byte criminal;
	public byte privileges;
	public CharacterCreateResponse(short charIndex,short body,short head,byte heading,byte x,byte y,short weapon,short shield,short helmet,short fx,short fxLoops,String name,byte criminal,byte privileges){
		this.charIndex = charIndex;
		this.body = body;
		this.head = head;
		this.heading = heading;
		this.x = x;
		this.y = y;
		this.weapon = weapon;
		this.shield = shield;
		this.helmet = helmet;
		this.fx = fx;
		this.fxLoops = fxLoops;
		this.name = name;
		this.criminal = criminal;
		this.privileges = privileges;
	}
	public static CharacterCreateResponse decode(ByteBuf in) {    
		try {                                   
			short charIndex = readShort(in);
			short body = readShort(in);
			short head = readShort(in);
			byte heading = readByte(in);
			byte x = readByte(in);
			byte y = readByte(in);
			short weapon = readShort(in);
			short shield = readShort(in);
			short helmet = readShort(in);
			short fx = readShort(in);
			short fxLoops = readShort(in);
			String name = readStr(in);
			byte criminal = readByte(in);
			byte privileges = readByte(in);
			return new CharacterCreateResponse(charIndex,body,head,heading,x,y,weapon,shield,helmet,fx,fxLoops,name,criminal,privileges);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeShort(out,charIndex);
		writeShort(out,body);
		writeShort(out,head);
		writeByte(out,heading);
		writeByte(out,x);
		writeByte(out,y);
		writeShort(out,weapon);
		writeShort(out,shield);
		writeShort(out,helmet);
		writeShort(out,fx);
		writeShort(out,fxLoops);
		writeStr(out,name);
		writeByte(out,criminal);
		writeByte(out,privileges);
	}
};

