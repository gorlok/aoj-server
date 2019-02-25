package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class GuildVoteRequest extends ClientPacket {
	// GuildVote,s:vote
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildVote;
	}
	public String vote;
	public GuildVoteRequest(String vote){
		this.vote = vote;
	}
	public static GuildVoteRequest decode(ByteBuf in) {    
		try {                                   
			String vote = readStr(in);
			return new GuildVoteRequest(vote);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

