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

public class UserHittedUserResponse extends ServerPacket {
	// UserHittedUser,i:attackedChar,b:target,i:damage
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UserHittedUser;
	}
	public short attackedChar;
	public byte target;
	public short damage;
	public UserHittedUserResponse(short attackedChar,byte target,short damage){
		this.attackedChar = attackedChar;
		this.target = target;
		this.damage = damage;
	}
	public static UserHittedUserResponse decode(ByteBuf in) {    
		try {                                   
			short attackedChar = readShort(in);
			byte target = readByte(in);
			short damage = readShort(in);
			return new UserHittedUserResponse(attackedChar,target,damage);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeShort(out,attackedChar);
		writeByte(out,target);
		writeShort(out,damage);
	}
};

