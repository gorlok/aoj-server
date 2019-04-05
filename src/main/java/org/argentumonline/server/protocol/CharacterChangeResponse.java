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

public class CharacterChangeResponse extends ServerPacket {
	// CharacterChange,i:charIndex,i:body,i:head,b:heading,i:weapon,i:shield,i:helmet,i:fx,i:fxLoops
	@Override
	public ServerPacketID id() {
		return ServerPacketID.CharacterChange;
	}
	public short charIndex;
	public short body;
	public short head;
	public byte heading;
	public short weapon;
	public short shield;
	public short helmet;
	public short fx;
	public short fxLoops;
	public CharacterChangeResponse(short charIndex,short body,short head,byte heading,short weapon,short shield,short helmet,short fx,short fxLoops){
		this.charIndex = charIndex;
		this.body = body;
		this.head = head;
		this.heading = heading;
		this.weapon = weapon;
		this.shield = shield;
		this.helmet = helmet;
		this.fx = fx;
		this.fxLoops = fxLoops;
	}
	public static CharacterChangeResponse decode(ByteBuf in) {    
		try {                                   
			short charIndex = readShort(in);
			short body = readShort(in);
			short head = readShort(in);
			byte heading = readByte(in);
			short weapon = readShort(in);
			short shield = readShort(in);
			short helmet = readShort(in);
			short fx = readShort(in);
			short fxLoops = readShort(in);
			return new CharacterChangeResponse(charIndex,body,head,heading,weapon,shield,helmet,fx,fxLoops);                  
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
		writeShort(out,weapon);
		writeShort(out,shield);
		writeShort(out,helmet);
		writeShort(out,fx);
		writeShort(out,fxLoops);
	}
};

