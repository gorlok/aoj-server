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

public class GuildLeaderInfoResponse extends ServerPacket {
	// GuildLeaderInfo,s:guildList,s:memberList,s:guildNews,s:requestsList
	@Override
	public ServerPacketID id() {
		return ServerPacketID.GuildLeaderInfo;
	}
	public String guildList;
	public String memberList;
	public String guildNews;
	public String requestsList;
	public GuildLeaderInfoResponse(String guildList,String memberList,String guildNews,String requestsList){
		this.guildList = guildList;
		this.memberList = memberList;
		this.guildNews = guildNews;
		this.requestsList = requestsList;
	}
	public static GuildLeaderInfoResponse decode(ByteBuf in) {    
		try {                                   
			String guildList = readStr(in);
			String memberList = readStr(in);
			String guildNews = readStr(in);
			String requestsList = readStr(in);
			return new GuildLeaderInfoResponse(guildList,memberList,guildNews,requestsList);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeStr(out,guildList);
		writeStr(out,memberList);
		writeStr(out,guildNews);
		writeStr(out,requestsList);
	}
};

