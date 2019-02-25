package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class AlterPasswordRequest extends ClientPacket {
	// AlterPassword,s:userName,s:copyFrom
	@Override
	public ClientPacketID id() {
		return ClientPacketID.AlterPassword;
	}
	public String userName;
	public String copyFrom;
	public AlterPasswordRequest(String userName,String copyFrom){
		this.userName = userName;
		this.copyFrom = copyFrom;
	}
	public static AlterPasswordRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			String copyFrom = readStr(in);
			return new AlterPasswordRequest(userName,copyFrom);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

