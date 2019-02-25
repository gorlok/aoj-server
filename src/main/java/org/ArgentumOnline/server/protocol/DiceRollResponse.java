package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class DiceRollResponse extends ServerPacket {
	// DiceRoll,b:fuerza,b:agilidad,b:inteligencia,b:carisma,b:constitucion
	@Override
	public ServerPacketID id() {
		return ServerPacketID.DiceRoll;
	}
	public byte fuerza;
	public byte agilidad;
	public byte inteligencia;
	public byte carisma;
	public byte constitucion;
	public DiceRollResponse(byte fuerza,byte agilidad,byte inteligencia,byte carisma,byte constitucion){
		this.fuerza = fuerza;
		this.agilidad = agilidad;
		this.inteligencia = inteligencia;
		this.carisma = carisma;
		this.constitucion = constitucion;
	}
};

