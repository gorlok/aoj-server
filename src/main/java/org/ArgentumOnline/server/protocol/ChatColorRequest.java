package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ChatColorRequest extends ClientPacket {
	// ChatColor,b:red,b:green,b:blue
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChatColor;
	}
	public byte red;
	public byte green;
	public byte blue;
	public ChatColorRequest(byte red,byte green,byte blue){
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
};

