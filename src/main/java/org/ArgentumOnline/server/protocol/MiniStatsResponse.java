package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class MiniStatsResponse extends ServerPacket {
	// MiniStats,l:ciudadanosMatados,l:criminalesMatados,l:usuariosMatados,i:npcsMatados,b:clase,l:pena
	@Override
	public ServerPacketID id() {
		return ServerPacketID.MiniStats;
	}
	public int ciudadanosMatados;
	public int criminalesMatados;
	public int usuariosMatados;
	public short npcsMatados;
	public byte clase;
	public int pena;
	public MiniStatsResponse(int ciudadanosMatados,int criminalesMatados,int usuariosMatados,short npcsMatados,byte clase,int pena){
		this.ciudadanosMatados = ciudadanosMatados;
		this.criminalesMatados = criminalesMatados;
		this.usuariosMatados = usuariosMatados;
		this.npcsMatados = npcsMatados;
		this.clase = clase;
		this.pena = pena;
	}
	public static MiniStatsResponse decode(ByteBuf in) {    
		try {                                   
			int ciudadanosMatados = readInt(in);
			int criminalesMatados = readInt(in);
			int usuariosMatados = readInt(in);
			short npcsMatados = readShort(in);
			byte clase = readByte(in);
			int pena = readInt(in);
			return new MiniStatsResponse(ciudadanosMatados,criminalesMatados,usuariosMatados,npcsMatados,clase,pena);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
};

