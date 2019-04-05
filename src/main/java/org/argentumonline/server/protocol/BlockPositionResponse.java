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

public class BlockPositionResponse extends ServerPacket {
	// BlockPosition,b:x,b:y,b:blocked
	@Override
	public ServerPacketID id() {
		return ServerPacketID.BlockPosition;
	}
	public byte x;
	public byte y;
	public byte blocked;
	public BlockPositionResponse(byte x,byte y,byte blocked){
		this.x = x;
		this.y = y;
		this.blocked = blocked;
	}
	public static BlockPositionResponse decode(ByteBuf in) {    
		try {                                   
			byte x = readByte(in);
			byte y = readByte(in);
			byte blocked = readByte(in);
			return new BlockPositionResponse(x,y,blocked);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeByte(out,x);
		writeByte(out,y);
		writeByte(out,blocked);
	}
};

