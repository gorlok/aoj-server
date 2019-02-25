package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildPeaceDetailsRequest extends ClientPacket {
	// GuildPeaceDetails,s:guild
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildPeaceDetails;
	}
	public String guild;
	public GuildPeaceDetailsRequest(String guild){
		this.guild = guild;
	}
	public static GuildPeaceDetailsRequest decode(ByteBuf in) {    
		try {                                   
			String guild = readStr(in);
			return new GuildPeaceDetailsRequest(guild);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

