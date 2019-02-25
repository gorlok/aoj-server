package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CharacterChangeResponse extends ServerPacket {
	// CharacterChange,i:charIndex,i:body,i:head,b:heading,i:weapon,i:shield,i:helmet,i:fx,i:fxLoops
	@Override
	public ServerPacketID id() {
		return ServerPacketID.CharacterChange;
	}
	public short charIndex;
	public short body;
	public short head;
	public byte heading;
	public short weapon;
	public short shield;
	public short helmet;
	public short fx;
	public short fxLoops;
	public CharacterChangeResponse(short charIndex,short body,short head,byte heading,short weapon,short shield,short helmet,short fx,short fxLoops){
		this.charIndex = charIndex;
		this.body = body;
		this.head = head;
		this.heading = heading;
		this.weapon = weapon;
		this.shield = shield;
		this.helmet = helmet;
		this.fx = fx;
		this.fxLoops = fxLoops;
	}
	public static CharacterChangeResponse decode(ByteBuf in) {    
		try {                                   
			short charIndex = readShort(in);
			short body = readShort(in);
			short head = readShort(in);
			byte heading = readByte(in);
			short weapon = readShort(in);
			short shield = readShort(in);
			short helmet = readShort(in);
			short fx = readShort(in);
			short fxLoops = readShort(in);
			return new CharacterChangeResponse(charIndex,body,head,heading,weapon,shield,helmet,fx,fxLoops);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

