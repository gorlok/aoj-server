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

public class GuildNewsResponse extends ServerPacket {
	// GuildNews,s:guildNews,s:enemiesList,s:alliesList
	@Override
	public ServerPacketID id() {
		return ServerPacketID.GuildNews;
	}
	public String guildNews;
	public String enemiesList;
	public String alliesList;
	public GuildNewsResponse(String guildNews,String enemiesList,String alliesList){
		this.guildNews = guildNews;
		this.enemiesList = enemiesList;
		this.alliesList = alliesList;
	}
	public static GuildNewsResponse decode(ByteBuf in) {    
		try {                                   
			String guildNews = readStr(in);
			String enemiesList = readStr(in);
			String alliesList = readStr(in);
			return new GuildNewsResponse(guildNews,enemiesList,alliesList);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeStr(out,guildNews);
		writeStr(out,enemiesList);
		writeStr(out,alliesList);
	}
};

