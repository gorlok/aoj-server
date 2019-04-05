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

public class PlayWaveResponse extends ServerPacket {
	// PlayWave,b:wave,b:x,b:y
	@Override
	public ServerPacketID id() {
		return ServerPacketID.PlayWave;
	}
	public byte wave;
	public byte x;
	public byte y;
	public PlayWaveResponse(byte wave,byte x,byte y){
		this.wave = wave;
		this.x = x;
		this.y = y;
	}
	public static PlayWaveResponse decode(ByteBuf in) {    
		try {                                   
			byte wave = readByte(in);
			byte x = readByte(in);
			byte y = readByte(in);
			return new PlayWaveResponse(wave,x,y);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeByte(out,wave);
		writeByte(out,x);
		writeByte(out,y);
	}
};

