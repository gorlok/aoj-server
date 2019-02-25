package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

