package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class CharacterCreateResponse extends ServerPacket {
	// CharacterCreate,i:charIndex,i:body,i:head,b:heading,b:x,b:y,i:weapon,i:shield,i:helmet,i:fx,i:fxLoops,s:name,b:criminal,b:privileges
	@Override
	public ServerPacketID id() {
		return ServerPacketID.CharacterCreate;
	}
	public short charIndex;
	public short body;
	public short head;
	public byte heading;
	public byte x;
	public byte y;
	public short weapon;
	public short shield;
	public short helmet;
	public short fx;
	public short fxLoops;
	public String name;
	public byte criminal;
	public byte privileges;
	public CharacterCreateResponse(short charIndex,short body,short head,byte heading,byte x,byte y,short weapon,short shield,short helmet,short fx,short fxLoops,String name,byte criminal,byte privileges){
		this.charIndex = charIndex;
		this.body = body;
		this.head = head;
		this.heading = heading;
		this.x = x;
		this.y = y;
		this.weapon = weapon;
		this.shield = shield;
		this.helmet = helmet;
		this.fx = fx;
		this.fxLoops = fxLoops;
		this.name = name;
		this.criminal = criminal;
		this.privileges = privileges;
	}
	public static CharacterCreateResponse decode(ByteBuf in) {    
		try {                                   
			short charIndex = readShort(in);
			short body = readShort(in);
			short head = readShort(in);
			byte heading = readByte(in);
			byte x = readByte(in);
			byte y = readByte(in);
			short weapon = readShort(in);
			short shield = readShort(in);
			short helmet = readShort(in);
			short fx = readShort(in);
			short fxLoops = readShort(in);
			String name = readStr(in);
			byte criminal = readByte(in);
			byte privileges = readByte(in);
			return new CharacterCreateResponse(charIndex,body,head,heading,x,y,weapon,shield,helmet,fx,fxLoops,name,criminal,privileges);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

