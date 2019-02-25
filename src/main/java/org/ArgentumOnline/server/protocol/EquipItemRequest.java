package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class EquipItemRequest extends ClientPacket {
	// EquipItem,b:itemSlot
	@Override
	public ClientPacketID id() {
		return ClientPacketID.EquipItem;
	}
	public byte itemSlot;
	public EquipItemRequest(byte itemSlot){
		this.itemSlot = itemSlot;
	}
	public static EquipItemRequest decode(ByteBuf in) {    
		try {                                   
			byte itemSlot = readByte(in);
			return new EquipItemRequest(itemSlot);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

