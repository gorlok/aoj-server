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

public class ForumPostRequest extends ClientPacket {
	// ForumPost,s:title,s:msg
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ForumPost;
	}
	public String title;
	public String msg;
	public ForumPostRequest(String title,String msg){
		this.title = title;
		this.msg = msg;
	}
	public static ForumPostRequest decode(ByteBuf in) {    
		try {                                   
			String title = readStr(in);
			String msg = readStr(in);
			return new ForumPostRequest(title,msg);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

