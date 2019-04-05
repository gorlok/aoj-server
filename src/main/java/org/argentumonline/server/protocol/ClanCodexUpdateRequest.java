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

public class ClanCodexUpdateRequest extends ClientPacket {
	// ClanCodexUpdate,s:desc,s:codex
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ClanCodexUpdate;
	}
	public String desc;
	public String codex;
	public ClanCodexUpdateRequest(String desc,String codex){
		this.desc = desc;
		this.codex = codex;
	}
	public static ClanCodexUpdateRequest decode(ByteBuf in) {    
		try {                                   
			String desc = readStr(in);
			String codex = readStr(in);
			return new ClanCodexUpdateRequest(desc,codex);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

