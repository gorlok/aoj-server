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

public class ChatColorRequest extends ClientPacket {
	// ChatColor,b:red,b:green,b:blue
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChatColor;
	}
	public byte red;
	public byte green;
	public byte blue;
	public ChatColorRequest(byte red,byte green,byte blue){
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	public static ChatColorRequest decode(ByteBuf in) {    
		try {                                   
			byte red = readByte(in);
			byte green = readByte(in);
			byte blue = readByte(in);
			return new ChatColorRequest(red,green,blue);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

