/*******************************************************************************
 *     Gorlok AO, an implementation of Argentum Online using Java.
 *     Copyright (C) 2019 Pablo Fernando Lillia �gorlok� 
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

public class ChangeMapInfoNoMagicRequest extends ClientPacket {
	// ChangeMapInfoNoMagic,b:noMagic
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangeMapInfoNoMagic;
	}
	public byte noMagic;
	public ChangeMapInfoNoMagicRequest(byte noMagic){
		this.noMagic = noMagic;
	}
	public static ChangeMapInfoNoMagicRequest decode(ByteBuf in) {    
		try {                                   
			byte noMagic = readByte(in);
			return new ChangeMapInfoNoMagicRequest(noMagic);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};
