package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ShowSignalResponse extends ServerPacket {
	// ShowSignal,s:texto,i:grhSecundario
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ShowSignal;
	}
	public String texto;
	public short grhSecundario;
	public ShowSignalResponse(String texto,short grhSecundario){
		this.texto = texto;
		this.grhSecundario = grhSecundario;
	}
};

