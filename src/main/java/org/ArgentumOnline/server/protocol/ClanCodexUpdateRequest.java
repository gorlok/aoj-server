package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static ClanCodexUpdateRequest decode(ByteBuf in) {    
		try {                                   
			String desc = readStr(in);
			String codex = readStr(in);
			return new ClanCodexUpdateRequest(desc,codex);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

