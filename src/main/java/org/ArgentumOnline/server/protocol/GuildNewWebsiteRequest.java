package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class GuildNewWebsiteRequest extends ClientPacket {
	// GuildNewWebsite,s:website
	@Override
	public ClientPacketID id() {
		return ClientPacketID.GuildNewWebsite;
	}
	public String website;
	public GuildNewWebsiteRequest(String website){
		this.website = website;
	}
};

