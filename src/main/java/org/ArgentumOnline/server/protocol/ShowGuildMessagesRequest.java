package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ShowGuildMessagesRequest extends ClientPacket {
	// ShowGuildMessages,s:guild
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ShowGuildMessages;
	}
	public String guild;
	public ShowGuildMessagesRequest(String guild){
		this.guild = guild;
	}
	public static ShowGuildMessagesRequest decode(ByteBuf in) {    
		try {                                   
			String guild = readStr(in);
			return new ShowGuildMessagesRequest(guild);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

