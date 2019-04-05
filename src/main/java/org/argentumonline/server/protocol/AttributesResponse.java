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

public class AttributesResponse extends ServerPacket {
	// Attributes,b:fuerza,b:agilidad,b:inteligencia,b:carisma,b:constitucion
	@Override
	public ServerPacketID id() {
		return ServerPacketID.Attributes;
	}
	public byte fuerza;
	public byte agilidad;
	public byte inteligencia;
	public byte carisma;
	public byte constitucion;
	public AttributesResponse(byte fuerza,byte agilidad,byte inteligencia,byte carisma,byte constitucion){
		this.fuerza = fuerza;
		this.agilidad = agilidad;
		this.inteligencia = inteligencia;
		this.carisma = carisma;
		this.constitucion = constitucion;
	}
	public static AttributesResponse decode(ByteBuf in) {    
		try {                                   
			byte fuerza = readByte(in);
			byte agilidad = readByte(in);
			byte inteligencia = readByte(in);
			byte carisma = readByte(in);
			byte constitucion = readByte(in);
			return new AttributesResponse(fuerza,agilidad,inteligencia,carisma,constitucion);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeByte(out,fuerza);
		writeByte(out,agilidad);
		writeByte(out,inteligencia);
		writeByte(out,carisma);
		writeByte(out,constitucion);
	}
};

