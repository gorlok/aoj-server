package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

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
};

