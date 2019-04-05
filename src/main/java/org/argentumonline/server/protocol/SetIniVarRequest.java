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

public class SetIniVarRequest extends ClientPacket {
	// SetIniVar,s:section,s:key,s:value
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SetIniVar;
	}
	public String section;
	public String key;
	public String value;
	public SetIniVarRequest(String section,String key,String value){
		this.section = section;
		this.key = key;
		this.value = value;
	}
	public static SetIniVarRequest decode(ByteBuf in) {    
		try {                                   
			String section = readStr(in);
			String key = readStr(in);
			String value = readStr(in);
			return new SetIniVarRequest(section,key,value);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

