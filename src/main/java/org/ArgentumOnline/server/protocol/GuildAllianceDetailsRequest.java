package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildAllianceDetailsRequest extends ClientPacket {
	// GuildAllianceDetails,s:guild
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildAllianceDetails;
	}
	public String guild;
	public GuildAllianceDetailsRequest(String guild){
		this.guild = guild;
	}
	public static GuildAllianceDetailsRequest decode(ByteBuf in) {    
		try {                                   
			String guild = readStr(in);
			return new GuildAllianceDetailsRequest(guild);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

