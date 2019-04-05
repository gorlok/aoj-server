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

public class UpdateHungerAndThirstResponse extends ServerPacket {
	// UpdateHungerAndThirst,b:maxAGU,b:minAGU,b:maxHAM,b:minHAM
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UpdateHungerAndThirst;
	}
	public byte maxAGU;
	public byte minAGU;
	public byte maxHAM;
	public byte minHAM;
	public UpdateHungerAndThirstResponse(byte maxAGU,byte minAGU,byte maxHAM,byte minHAM){
		this.maxAGU = maxAGU;
		this.minAGU = minAGU;
		this.maxHAM = maxHAM;
		this.minHAM = minHAM;
	}
	public static UpdateHungerAndThirstResponse decode(ByteBuf in) {    
		try {                                   
			byte maxAGU = readByte(in);
			byte minAGU = readByte(in);
			byte maxHAM = readByte(in);
			byte minHAM = readByte(in);
			return new UpdateHungerAndThirstResponse(maxAGU,minAGU,maxHAM,minHAM);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeByte(out,maxAGU);
		writeByte(out,minAGU);
		writeByte(out,maxHAM);
		writeByte(out,minHAM);
	}
};

