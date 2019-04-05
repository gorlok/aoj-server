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

public class FameResponse extends ServerPacket {
	// Fame,l:asesinoRep,l:bandidoRep,l:burguesRep,l:ladronRep,l:nobleRep,l:pebleRep,l:promedio
	@Override
	public ServerPacketID id() {
		return ServerPacketID.Fame;
	}
	public int asesinoRep;
	public int bandidoRep;
	public int burguesRep;
	public int ladronRep;
	public int nobleRep;
	public int pebleRep;
	public int promedio;
	public FameResponse(int asesinoRep,int bandidoRep,int burguesRep,int ladronRep,int nobleRep,int pebleRep,int promedio){
		this.asesinoRep = asesinoRep;
		this.bandidoRep = bandidoRep;
		this.burguesRep = burguesRep;
		this.ladronRep = ladronRep;
		this.nobleRep = nobleRep;
		this.pebleRep = pebleRep;
		this.promedio = promedio;
	}
	public static FameResponse decode(ByteBuf in) {    
		try {                                   
			int asesinoRep = readInt(in);
			int bandidoRep = readInt(in);
			int burguesRep = readInt(in);
			int ladronRep = readInt(in);
			int nobleRep = readInt(in);
			int pebleRep = readInt(in);
			int promedio = readInt(in);
			return new FameResponse(asesinoRep,bandidoRep,burguesRep,ladronRep,nobleRep,pebleRep,promedio);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeInt(out,asesinoRep);
		writeInt(out,bandidoRep);
		writeInt(out,burguesRep);
		writeInt(out,ladronRep);
		writeInt(out,nobleRep);
		writeInt(out,pebleRep);
		writeInt(out,promedio);
	}
};

