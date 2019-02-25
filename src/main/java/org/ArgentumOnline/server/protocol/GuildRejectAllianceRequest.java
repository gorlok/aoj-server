package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildRejectAllianceRequest extends ClientPacket {
	// GuildRejectAlliance,s:guild
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildRejectAlliance;
	}
	public String guild;
	public GuildRejectAllianceRequest(String guild){
		this.guild = guild;
	}
	public static GuildRejectAllianceRequest decode(ByteBuf in) {    
		try {                                   
			String guild = readStr(in);
			return new GuildRejectAllianceRequest(guild);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

