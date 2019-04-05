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

public class ConsoleMsgResponse extends ServerPacket {
	// ConsoleMsg,s:chat,b:fontIndex
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ConsoleMsg;
	}
	public String chat;
	public byte fontIndex;
	public ConsoleMsgResponse(String chat,byte fontIndex){
		this.chat = chat;
		this.fontIndex = fontIndex;
	}
	public static ConsoleMsgResponse decode(ByteBuf in) {    
		try {                                   
			String chat = readStr(in);
			byte fontIndex = readByte(in);
			return new ConsoleMsgResponse(chat,fontIndex);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeStr(out,chat);
		writeByte(out,fontIndex);
	}
};

