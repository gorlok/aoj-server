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

public class ChangeMapResponse extends ServerPacket {
	// ChangeMap,i:map,i:version
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ChangeMap;
	}
	public short map;
	public short version;
	public ChangeMapResponse(short map,short version){
		this.map = map;
		this.version = version;
	}
	public static ChangeMapResponse decode(ByteBuf in) {    
		try {                                   
			short map = readShort(in);
			short version = readShort(in);
			return new ChangeMapResponse(map,version);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeShort(out,map);
		writeShort(out,version);
	}
};

