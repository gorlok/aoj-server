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

public class ImperialArmourRequest extends ClientPacket {
	// ImperialArmour,b:index,i:objIndex
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ImperialArmour;
	}
	public byte index;
	public short objIndex;
	public ImperialArmourRequest(byte index,short objIndex){
		this.index = index;
		this.objIndex = objIndex;
	}
	public static ImperialArmourRequest decode(ByteBuf in) {    
		try {                                   
			byte index = readByte(in);
			short objIndex = readShort(in);
			return new ImperialArmourRequest(index,objIndex);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

