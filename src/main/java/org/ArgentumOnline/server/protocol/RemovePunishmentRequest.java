package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RemovePunishmentRequest extends ClientPacket {
	// RemovePunishment,s:userName,b:punishment,s:newText
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RemovePunishment;
	}
	public String userName;
	public byte punishment;
	public String newText;
	public RemovePunishmentRequest(String userName,byte punishment,String newText){
		this.userName = userName;
		this.punishment = punishment;
		this.newText = newText;
	}
	public static RemovePunishmentRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			byte punishment = readByte(in);
			String newText = readStr(in);
			return new RemovePunishmentRequest(userName,punishment,newText);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

