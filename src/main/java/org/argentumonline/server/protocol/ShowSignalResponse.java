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

public class ShowSignalResponse extends ServerPacket {
	// ShowSignal,s:texto,i:grhSecundario
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ShowSignal;
	}
	public String texto;
	public short grhSecundario;
	public ShowSignalResponse(String texto,short grhSecundario){
		this.texto = texto;
		this.grhSecundario = grhSecundario;
	}
	public static ShowSignalResponse decode(ByteBuf in) {    
		try {                                   
			String texto = readStr(in);
			short grhSecundario = readShort(in);
			return new ShowSignalResponse(texto,grhSecundario);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeStr(out,texto);
		writeShort(out,grhSecundario);
	}
};

