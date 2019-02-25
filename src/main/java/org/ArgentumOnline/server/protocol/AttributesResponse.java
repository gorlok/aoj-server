package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;

public class AttributesResponse extends ServerPacket {
	// Attributes,b:fuerza,b:agilidad,b:inteligencia,b:carisma,b:constitucion
	@Override
	public ServerPacketID id() {
		return ServerPacketID.Attributes;
	}
	public byte fuerza;
	public byte agilidad;
	public byte inteligencia;
	public byte carisma;
	public byte constitucion;
	public AttributesResponse(byte fuerza,byte agilidad,byte inteligencia,byte carisma,byte constitucion){
		this.fuerza = fuerza;
		this.agilidad = agilidad;
		this.inteligencia = inteligencia;
		this.carisma = carisma;
		this.constitucion = constitucion;
	}
};

