package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildRequestDetailsRequest extends ClientPacket {
	// GuildRequestDetails,s:guild
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildRequestDetails;
	}
	public String guild;
	public GuildRequestDetailsRequest(String guild){
		this.guild = guild;
	}
	public static GuildRequestDetailsRequest decode(ByteBuf in) {    
		try {                                   
			String guild = readStr(in);
			return new GuildRequestDetailsRequest(guild);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

