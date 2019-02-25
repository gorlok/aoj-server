package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

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
	public static UserNameListResponse decode(ByteBuf in) {    
		try {                                   
			String userNamesList = readStr(in);
			return new UserNameListResponse(userNamesList);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

