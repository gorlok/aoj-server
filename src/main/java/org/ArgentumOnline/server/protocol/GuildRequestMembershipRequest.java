package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildRequestMembershipRequest extends ClientPacket {
	// GuildRequestMembership,s:guild,s:application
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildRequestMembership;
	}
	public String guild;
	public String application;
	public GuildRequestMembershipRequest(String guild,String application){
		this.guild = guild;
		this.application = application;
	}
	public static GuildRequestMembershipRequest decode(ByteBuf in) {    
		try {                                   
			String guild = readStr(in);
			String application = readStr(in);
			return new GuildRequestMembershipRequest(guild,application);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

