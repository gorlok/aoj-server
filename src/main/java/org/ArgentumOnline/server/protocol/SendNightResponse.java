package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class SendNightResponse extends ServerPacket {
	// SendNight,b:night
	@Override
	public ServerPacketID id() {
		return ServerPacketID.SendNight;
	}
	public byte night;
	public SendNightResponse(byte night){
		this.night = night;
	}
};

