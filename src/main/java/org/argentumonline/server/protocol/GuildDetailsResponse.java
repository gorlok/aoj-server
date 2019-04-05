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

public class GuildDetailsResponse extends ServerPacket {
	// GuildDetails,s:guildName,s:founder,s:foundationDate,s:leader,s:url,i:memberCount,b:electionsOpen,s:alignment,i:enemiesCount,i:alliesCount,s:antifactionPoints,s:codex,s:guildDesc
	@Override
	public ServerPacketID id() {
		return ServerPacketID.GuildDetails;
	}
	public String guildName;
	public String founder;
	public String foundationDate;
	public String leader;
	public String url;
	public short memberCount;
	public byte electionsOpen;
	public String alignment;
	public short enemiesCount;
	public short alliesCount;
	public String antifactionPoints;
	public String codex;
	public String guildDesc;
	public GuildDetailsResponse(String guildName,String founder,String foundationDate,String leader,String url,short memberCount,byte electionsOpen,String alignment,short enemiesCount,short alliesCount,String antifactionPoints,String codex,String guildDesc){
		this.guildName = guildName;
		this.founder = founder;
		this.foundationDate = foundationDate;
		this.leader = leader;
		this.url = url;
		this.memberCount = memberCount;
		this.electionsOpen = electionsOpen;
		this.alignment = alignment;
		this.enemiesCount = enemiesCount;
		this.alliesCount = alliesCount;
		this.antifactionPoints = antifactionPoints;
		this.codex = codex;
		this.guildDesc = guildDesc;
	}
	public static GuildDetailsResponse decode(ByteBuf in) {    
		try {                                   
			String guildName = readStr(in);
			String founder = readStr(in);
			String foundationDate = readStr(in);
			String leader = readStr(in);
			String url = readStr(in);
			short memberCount = readShort(in);
			byte electionsOpen = readByte(in);
			String alignment = readStr(in);
			short enemiesCount = readShort(in);
			short alliesCount = readShort(in);
			String antifactionPoints = readStr(in);
			String codex = readStr(in);
			String guildDesc = readStr(in);
			return new GuildDetailsResponse(guildName,founder,foundationDate,leader,url,memberCount,electionsOpen,alignment,enemiesCount,alliesCount,antifactionPoints,codex,guildDesc);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeStr(out,guildName);
		writeStr(out,founder);
		writeStr(out,foundationDate);
		writeStr(out,leader);
		writeStr(out,url);
		writeShort(out,memberCount);
		writeByte(out,electionsOpen);
		writeStr(out,alignment);
		writeShort(out,enemiesCount);
		writeShort(out,alliesCount);
		writeStr(out,antifactionPoints);
		writeStr(out,codex);
		writeStr(out,guildDesc);
	}
};

