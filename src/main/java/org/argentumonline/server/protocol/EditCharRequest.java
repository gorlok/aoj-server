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

public class EditCharRequest extends ClientPacket {
	// EditChar,s:userName,b:option,s:param1,s:param2
	@Override
	public ClientPacketID id() {
		return ClientPacketID.EditChar;
	}
	public String userName;
	public byte option;
	public String param1;
	public String param2;
	public EditCharRequest(String userName,byte option,String param1,String param2){
		this.userName = userName;
		this.option = option;
		this.param1 = param1;
		this.param2 = param2;
	}
	public static EditCharRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			byte option = readByte(in);
			String param1 = readStr(in);
			String param2 = readStr(in);
			return new EditCharRequest(userName,option,param1,param2);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

