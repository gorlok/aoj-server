package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildAcceptPeaceRequest extends ClientPacket {
	// GuildAcceptPeace,s:guild
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildAcceptPeace;
	}
	public String guild;
	public GuildAcceptPeaceRequest(String guild){
		this.guild = guild;
	}
	public static GuildAcceptPeaceRequest decode(ByteBuf in) {    
		try {                                   
			String guild = readStr(in);
			return new GuildAcceptPeaceRequest(guild);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

