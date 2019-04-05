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

public class ForceWAVEToMapRequest extends ClientPacket {
	// ForceWAVEToMap,b:waveId,i:map,b:x,b:y
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ForceWAVEToMap;
	}
	public byte waveId;
	public short map;
	public byte x;
	public byte y;
	public ForceWAVEToMapRequest(byte waveId,short map,byte x,byte y){
		this.waveId = waveId;
		this.map = map;
		this.x = x;
		this.y = y;
	}
	public static ForceWAVEToMapRequest decode(ByteBuf in) {    
		try {                                   
			byte waveId = readByte(in);
			short map = readShort(in);
			byte x = readByte(in);
			byte y = readByte(in);
			return new ForceWAVEToMapRequest(waveId,map,x,y);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

