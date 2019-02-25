package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static InquiryVoteRequest decode(ByteBuf in) {    
		try {                                   
			byte opt = readByte(in);
			return new InquiryVoteRequest(opt);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

