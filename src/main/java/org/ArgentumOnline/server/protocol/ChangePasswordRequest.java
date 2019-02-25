package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ChangePasswordRequest extends ClientPacket {
	// ChangePassword,s:oldPassword,s:newPassword
	@Override
	public ClientPacketID id() {
		return ClientPacketID.ChangePassword;
	}
	public String oldPassword;
	public String newPassword;
	public ChangePasswordRequest(String oldPassword,String newPassword){
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
	}
	public static ChangePasswordRequest decode(ByteBuf in) {    
		try {                                   
			String oldPassword = readStr(in);
			String newPassword = readStr(in);
			return new ChangePasswordRequest(oldPassword,newPassword);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

