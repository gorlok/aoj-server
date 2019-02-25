package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class InquiryVoteRequest extends ClientPacket {
	// InquiryVote,b:opt
	@Override
	public ClientPacketID id() {
		return ClientPacketID.InquiryVote;
	}
	public byte opt;
	public InquiryVoteRequest(byte opt){
		this.opt = opt;
	}
};

