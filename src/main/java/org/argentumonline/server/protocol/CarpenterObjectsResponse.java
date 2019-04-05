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

public class CarpenterObjectsResponse extends ServerPacket {
	// CarpenterObjects,i:count,(s:name,i:madera,i:index)[.]:objects
	@Override
	public ServerPacketID id() {
		return ServerPacketID.CarpenterObjects;
	}
	public short count;
	public CarpenterObjects_DATA[] objects;
	public CarpenterObjectsResponse(short count,CarpenterObjects_DATA[] objects){
		this.count = count;
		this.objects = objects;
	}
	public static CarpenterObjectsResponse decode(ByteBuf in) {    
		try {                                   
			short count = readShort(in);
			
			CarpenterObjects_DATA[] objects = new CarpenterObjects_DATA[count]; 
			for (int i = 0; i < count; i++) {
				objects[i].name = readStr(in);
				objects[i].madera = readShort(in);
				objects[i].index = readShort(in);
			}
			
			return new CarpenterObjectsResponse(count,objects);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeShort(out,count);
		
		for (int i = 0; i < count; i++) {
			writeStr(out, objects[i].name);
			writeShort(out, objects[i].madera);
			writeShort(out, objects[i].index);
		}
	}
};

