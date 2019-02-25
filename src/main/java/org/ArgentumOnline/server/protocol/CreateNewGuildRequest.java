package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CreateNewGuildRequest extends ClientPacket {
	// CreateNewGuild,s:desc,s:guildName,s:site,s:codex
	@Override
	public ClientPacketID id() {
		return ClientPacketID.CreateNewGuild;
	}
	public String desc;
	public String guildName;
	public String site;
	public String codex;
	public CreateNewGuildRequest(String desc,String guildName,String site,String codex){
		this.desc = desc;
		this.guildName = guildName;
		this.site = site;
		this.codex = codex;
	}
	public static CreateNewGuildRequest decode(ByteBuf in) {    
		try {                                   
			String desc = readStr(in);
			String guildName = readStr(in);
			String site = readStr(in);
			String codex = readStr(in);
			return new CreateNewGuildRequest(desc,guildName,site,codex);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

