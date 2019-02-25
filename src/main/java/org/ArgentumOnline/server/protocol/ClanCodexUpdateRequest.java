package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class ClanCodexUpdateRequest extends ClientPacket {
	// ClanCodexUpdate,s:desc,s:codex
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ClanCodexUpdate;
	}
	public String desc;
	public String codex;
	public ClanCodexUpdateRequest(String desc,String codex){
		this.desc = desc;
		this.codex = codex;
	}
};

