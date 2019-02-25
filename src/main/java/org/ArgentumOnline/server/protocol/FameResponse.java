package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class FameResponse extends ServerPacket {
	// Fame,l:asesinoRep,l:bandidoRep,l:burguesRep,l:ladronRep,l:nobleRep,l:pebleRep,l:promedio
	@Override
	public ServerPacketID id() {
		return ServerPacketID.Fame;
	}
	public int asesinoRep;
	public int bandidoRep;
	public int burguesRep;
	public int ladronRep;
	public int nobleRep;
	public int pebleRep;
	public int promedio;
	public FameResponse(int asesinoRep,int bandidoRep,int burguesRep,int ladronRep,int nobleRep,int pebleRep,int promedio){
		this.asesinoRep = asesinoRep;
		this.bandidoRep = bandidoRep;
		this.burguesRep = burguesRep;
		this.ladronRep = ladronRep;
		this.nobleRep = nobleRep;
		this.pebleRep = pebleRep;
		this.promedio = promedio;
	}
	public static FameResponse decode(ByteBuf in) {    
		try {                                   
			int asesinoRep = readInt(in);
			int bandidoRep = readInt(in);
			int burguesRep = readInt(in);
			int ladronRep = readInt(in);
			int nobleRep = readInt(in);
			int pebleRep = readInt(in);
			int promedio = readInt(in);
			return new FameResponse(asesinoRep,bandidoRep,burguesRep,ladronRep,nobleRep,pebleRep,promedio);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

