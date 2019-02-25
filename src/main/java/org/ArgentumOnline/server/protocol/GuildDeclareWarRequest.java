package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildDeclareWarRequest extends ClientPacket {
	// GuildDeclareWar,s:guild
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildDeclareWar;
	}
	public String guild;
	public GuildDeclareWarRequest(String guild){
		this.guild = guild;
	}
	public static GuildDeclareWarRequest decode(ByteBuf in) {    
		try {                                   
			String guild = readStr(in);
			return new GuildDeclareWarRequest(guild);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

