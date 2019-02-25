package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class UserNameListResponse extends ServerPacket {
	// UserNameList,s:userNamesList
	@Override
	public ServerPacketID id() {
		return ServerPacketID.UserNameList;
	}
	public String userNamesList;
	public UserNameListResponse(String userNamesList){
		this.userNamesList = userNamesList;
	}
};

