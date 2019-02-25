package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildFundateRequest extends ClientPacket {
	// GuildFundate,b:clanType
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildFundate;
	}
	public byte clanType;
	public GuildFundateRequest(byte clanType){
		this.clanType = clanType;
	}
	public static GuildFundateRequest decode(ByteBuf in) {    
		try {                                   
			byte clanType = readByte(in);
			return new GuildFundateRequest(clanType);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

