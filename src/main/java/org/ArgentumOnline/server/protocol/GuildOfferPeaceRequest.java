package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildOfferPeaceRequest extends ClientPacket {
	// GuildOfferPeace,s:guild,s:proposal
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildOfferPeace;
	}
	public String guild;
	public String proposal;
	public GuildOfferPeaceRequest(String guild,String proposal){
		this.guild = guild;
		this.proposal = proposal;
	}
	public static GuildOfferPeaceRequest decode(ByteBuf in) {    
		try {                                   
			String guild = readStr(in);
			String proposal = readStr(in);
			return new GuildOfferPeaceRequest(guild,proposal);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

