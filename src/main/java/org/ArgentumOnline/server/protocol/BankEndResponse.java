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
package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class BankEndResponse extends ServerPacket {
	// BankEnd
	@Override
	public ServerPacketID id() {
		return ServerPacketID.BankEnd;
	}
	public BankEndResponse(){
	}
	public static BankEndResponse decode(ByteBuf in) {    
		try {                                   
			return new BankEndResponse();                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
	}
};

