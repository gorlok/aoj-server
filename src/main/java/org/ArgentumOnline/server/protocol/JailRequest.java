package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class JailRequest extends ClientPacket {
	// Jail,s:userName,s:reason,b:jailTime
	@Override
	public ClientPacketID id() {
		return ClientPacketID.Jail;
	}
	public String userName;
	public String reason;
	public byte jailTime;
	public JailRequest(String userName,String reason,byte jailTime){
		this.userName = userName;
		this.reason = reason;
		this.jailTime = jailTime;
	}
};

