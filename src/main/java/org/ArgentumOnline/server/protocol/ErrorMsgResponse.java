package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ErrorMsgResponse extends ServerPacket {
	// ErrorMsg,s:msg
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ErrorMsg;
	}
	public String msg;
	public ErrorMsgResponse(String msg){
		this.msg = msg;
	}
};

