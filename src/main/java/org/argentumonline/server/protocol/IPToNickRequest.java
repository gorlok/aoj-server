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

public class IPToNickRequest extends ClientPacket {
	// IPToNick,b:ip1,b:ip2,b:ip3,b:ip4
	@Override
	public ClientPacketID id() {
		return ClientPacketID.IPToNick;
	}
	public byte ip1;
	public byte ip2;
	public byte ip3;
	public byte ip4;
	public IPToNickRequest(byte ip1,byte ip2,byte ip3,byte ip4){
		this.ip1 = ip1;
		this.ip2 = ip2;
		this.ip3 = ip3;
		this.ip4 = ip4;
	}
	public static IPToNickRequest decode(ByteBuf in) {    
		try {                                   
			byte ip1 = readByte(in);
			byte ip2 = readByte(in);
			byte ip3 = readByte(in);
			byte ip4 = readByte(in);
			return new IPToNickRequest(ip1,ip2,ip3,ip4);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

