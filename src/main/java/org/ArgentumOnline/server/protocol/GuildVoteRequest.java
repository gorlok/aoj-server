package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

