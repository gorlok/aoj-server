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

public class BlacksmithWeaponsResponse extends ServerPacket {
	// BlacksmithWeapons,i:count,(s:name,i:lingH,i:lingP,i:lingO,i:index)[.]:weapons
	@Override
	public ServerPacketID id() {
		return ServerPacketID.BlacksmithWeapons;
	}
	public short count;
	public BlacksmithWeapons_DATA[] weapons;
	public BlacksmithWeaponsResponse(short count,BlacksmithWeapons_DATA[] weapons){
		this.count = count;
		this.weapons = weapons;
	}
	public static BlacksmithWeaponsResponse decode(ByteBuf in) {    
		try {                                   
			short count = readShort(in);

			BlacksmithWeapons_DATA[] weapons = new BlacksmithWeapons_DATA[count];
			for (int i = 0; i < count; i++) {
				weapons[i].name = readStr(in);
				weapons[i].lingH = readShort(in);
				weapons[i].lingP = readShort(in);
				weapons[i].lingO = readShort(in);
				weapons[i].index = readShort(in);
			}
			
			return new BlacksmithWeaponsResponse(count,weapons);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeShort(out,count);
		
		for (int i = 0; i < count; i++) {
			writeStr(out, weapons[i].name);
			writeShort(out, weapons[i].lingH);
			writeShort(out, weapons[i].lingP);
			writeShort(out, weapons[i].lingO);
			writeShort(out, weapons[i].index);
		}
	}
};

