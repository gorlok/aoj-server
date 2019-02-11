package org.ArgentumOnline.server.protocol;

import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.ArgentumOnline.server.Client;
import org.ArgentumOnline.server.util.BytesReader;


/**
 * @author gorlok
 * @last modified by JAO (userforos: agushh/thorkes)
 * Esta clase solo codifica un array de bytes para enviar al cliente
 *
 */

public class BinaryProtocol extends Protocol {

	@Override
	public void encodeData(ByteBuffer buf, serverPacketID msg, Object... params) {
		
		buf.put(msg.binCode());
		
		//Falta poder codificar parametros de tipo byte
		
		if (params != null) {
			for (Object element : params) {
				if (element.getClass().getName().equals("java.lang.Byte")) { //JAO: agrego esto que no estaba!
			        buf.order(LITTLE_ENDIAN);
			        buf.put((Byte) element);
			        buf.order(BIG_ENDIAN);	
				
				}else if (element.getClass().getName().equals("java.lang.Short")) {
			        buf.order(LITTLE_ENDIAN);
			        buf.putShort((Short) element);
			        buf.order(BIG_ENDIAN);
					
				} else if (element.getClass().getName().equals("java.lang.Integer")) {					
			        buf.order(LITTLE_ENDIAN);
			        buf.putInt((Integer) element);
			        buf.order(BIG_ENDIAN);
					
				} else if (element.getClass().getName().equals("java.lang.Long")) {					
			        buf.order(LITTLE_ENDIAN);
			        buf.putLong((Long) element);
			        buf.order(BIG_ENDIAN);
					
				} else if (element.getClass().getName().equals("java.lang.Char")) {
			        buf.order(LITTLE_ENDIAN);
			        buf.putChar((Character) element);
			        buf.order(BIG_ENDIAN);
					
				} else if (element.getClass().getName().equals("java.lang.String")) { //JAO: agrego esto que no estaba!
					String str = element.toString();
			        // incluímos el largo
			        buf.order(ByteOrder.LITTLE_ENDIAN).putShort((short)str.length());
			        // bytes de la cadena
			        buf.order(ByteOrder.BIG_ENDIAN).put(str.getBytes()); 
				} else {
					System.out.println("encodeData(): tipo de dato desconocido - " + element.getClass().getName());
				}
			}
		}
	
	}

}
