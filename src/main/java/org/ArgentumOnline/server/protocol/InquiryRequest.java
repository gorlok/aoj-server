package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class InquiryRequest extends ClientPacket {
	// Inquiry
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Inquiry;
	}
	public InquiryRequest(){
	}
};

