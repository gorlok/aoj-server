package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class OfferDetailsResponse extends ServerPacket {
	// OfferDetails,s:details
	@Override
	public ServerPacketID id() {
		return ServerPacketID.OfferDetails;
	}
	public String details;
	public OfferDetailsResponse(String details){
		this.details = details;
	}
};

