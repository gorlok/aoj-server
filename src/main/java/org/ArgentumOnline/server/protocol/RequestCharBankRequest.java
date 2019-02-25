package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class RequestCharBankRequest extends ClientPacket {
	// RequestCharBank,s:userName
	@Override
	public ClientPacketID id() {
		return ClientPacketID.RequestCharBank;
	}
	public String userName;
	public RequestCharBankRequest(String userName){
		this.userName = userName;
	}
	public static RequestCharBankRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			return new RequestCharBankRequest(userName);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

