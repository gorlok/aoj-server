package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

