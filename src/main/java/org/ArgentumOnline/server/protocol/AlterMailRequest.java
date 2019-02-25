package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class AlterMailRequest extends ClientPacket {
	// AlterMail,s:userName,s:newEmail
	@Override
	public ClientPacketID id() {
		return ClientPacketID.AlterMail;
	}
	public String userName;
	public String newEmail;
	public AlterMailRequest(String userName,String newEmail){
		this.userName = userName;
		this.newEmail = newEmail;
	}
	public static AlterMailRequest decode(ByteBuf in) {    
		try {                                   
			String userName = readStr(in);
			String newEmail = readStr(in);
			return new AlterMailRequest(userName,newEmail);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

