package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildOfferAllianceRequest extends ClientPacket {
	// GuildOfferAlliance,s:guild,s:proposal
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildOfferAlliance;
	}
	public String guild;
	public String proposal;
	public GuildOfferAllianceRequest(String guild,String proposal){
		this.guild = guild;
		this.proposal = proposal;
	}
	public static GuildOfferAllianceRequest decode(ByteBuf in) {    
		try {                                   
			String guild = readStr(in);
			String proposal = readStr(in);
			return new GuildOfferAllianceRequest(guild,proposal);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

