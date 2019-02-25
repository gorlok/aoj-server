package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class SetIniVarRequest extends ClientPacket {
	// SetIniVar,s:section,s:key,s:value
	@Override
	public ClientPacketID id() {
		return ClientPacketID.SetIniVar;
	}
	public String section;
	public String key;
	public String value;
	public SetIniVarRequest(String section,String key,String value){
		this.section = section;
		this.key = key;
		this.value = value;
	}
};

