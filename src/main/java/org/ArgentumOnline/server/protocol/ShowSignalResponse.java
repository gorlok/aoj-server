package org.ArgentumOnline.server.protocol;

import org.ArgentumOnline.server.net.*;
import io.netty.buffer.ByteBuf;

public class ShowSignalResponse extends ServerPacket {
	// ShowSignal,s:texto,i:grhSecundario
	@Override
	public ServerPacketID id() {
		return ServerPacketID.ShowSignal;
	}
	public String texto;
	public short grhSecundario;
	public ShowSignalResponse(String texto,short grhSecundario){
		this.texto = texto;
		this.grhSecundario = grhSecundario;
	}
	public static ShowSignalResponse decode(ByteBuf in) {    
		try {                                   
			String texto = readStr(in);
			short grhSecundario = readShort(in);
			return new ShowSignalResponse(texto,grhSecundario);                  
		} catch (IndexOutOfBoundsException e) { 
			return null;                        
		}                                       
	}                                        
	@Override
	public void encode(ByteBuf out) {
		writeByte(out,this.id().id());
		writeStr(out,texto);
		writeShort(out,grhSecundario);
	}
};

