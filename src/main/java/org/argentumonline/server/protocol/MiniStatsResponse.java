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

public class MiniStatsResponse extends ServerPacket {
	// MiniStats,l:ciudadanosMatados,l:criminalesMatados,l:usuariosMatados,i:npcsMatados,b:clase,l:pena
	@Override
	public ServerPacketID id() {
		return ServerPacketID.MiniStats;
	}
	public int ciudadanosMatados;
	public int criminalesMatados;
	public int usuariosMatados;
	public short npcsMatados;
	public byte clase;
	public int pena;
	public MiniStatsResponse(int ciudadanosMatados,int criminalesMatados,int usuariosMatados,short npcsMatados,byte clase,int pena){
		this.ciudadanosMatados = ciudadanosMatados;
		this.criminalesMatados = criminalesMatados;
		this.usuariosMatados = usuariosMatados;
		this.npcsMatados = npcsMatados;
		this.clase = clase;
		this.pena = pena;
	}
	public static MiniStatsResponse decode(ByteBuf in) {    
		try {                                   
			int ciudadanosMatados = readInt(in);
			int criminalesMatados = readInt(in);
			int usuariosMatados = readInt(in);
			short npcsMatados = readShort(in);
			byte clase = readByte(in);
			int pena = readInt(in);
			return new MiniStatsResponse(ciudadanosMatados,criminalesMatados,usuariosMatados,npcsMatados,clase,pena);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeInt(out,ciudadanosMatados);
		writeInt(out,criminalesMatados);
		writeInt(out,usuariosMatados);
		writeShort(out,npcsMatados);
		writeByte(out,clase);
		writeInt(out,pena);
	}
};

