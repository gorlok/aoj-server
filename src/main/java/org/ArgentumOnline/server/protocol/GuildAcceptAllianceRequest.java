package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildAcceptAllianceRequest extends ClientPacket {
	// GuildAcceptAlliance,s:guild
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildAcceptAlliance;
	}
	public String guild;
	public GuildAcceptAllianceRequest(String guild){
		this.guild = guild;
	}
	public static GuildAcceptAllianceRequest decode(ByteBuf in) {    
		try {                                   
			String guild = readStr(in);
			return new GuildAcceptAllianceRequest(guild);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

