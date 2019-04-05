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

public class BlacksmithArmorsResponse extends ServerPacket {
	// BlacksmithArmors,i:count,(s:name,i:lingH,i:lingP,i:lingO,i:index)[.]:armors
	@Override
	public ServerPacketID id() {
		return ServerPacketID.BlacksmithArmors;
	}
	public short count;
	public BlacksmithArmors_DATA[] armors;
	public BlacksmithArmorsResponse(short count,BlacksmithArmors_DATA[] armors){
		this.count = count;
		this.armors = armors;
	}
	public static BlacksmithArmorsResponse decode(ByteBuf in) {    
		try {                                   
			short count = readShort(in);
			
			BlacksmithArmors_DATA[] armors = new BlacksmithArmors_DATA[count];
			for (int i = 0; i < count; i++) {
				armors[i].name = readStr(in);
				armors[i].lingH = readShort(in);
				armors[i].lingP = readShort(in);
				armors[i].lingO = readShort(in);
				armors[i].index = readShort(in);
			}
			
			return new BlacksmithArmorsResponse(count,armors);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeShort(out,count);
		
		for (int i = 0; i < count; i++) {
			writeStr(out, armors[i].name);
			writeShort(out, armors[i].lingH);
			writeShort(out, armors[i].lingP);
			writeShort(out, armors[i].lingO);
			writeShort(out, armors[i].index);
		}
	}
};

