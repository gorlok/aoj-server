package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildRejectPeaceRequest extends ClientPacket {
	// GuildRejectPeace,s:guild
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildRejectPeace;
	}
	public String guild;
	public GuildRejectPeaceRequest(String guild){
		this.guild = guild;
	}
	public static GuildRejectPeaceRequest decode(ByteBuf in) {    
		try {                                   
			String guild = readStr(in);
			return new GuildRejectPeaceRequest(guild);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

