package org.ArgentumOnline.server.protocol;

import java.nio.ByteBuffer;

/**
 * @author gorlok
 * @last modified by JAO (userforos: agushh/thorkes)
 * Esta clase solo codifica un array de bytes para enviar al cliente
 */
public class BufferWriter {
	
	private BufferWriter() {
	}
	
	public static void write(ByteBuffer buf, serverPacketID msg, Object... params) {
		
		buf.put(msg.binCode());
		
		if (params != null) {
			for (Object param : params) {
				if (param.getClass().getName().equals("java.lang.Byte")) { //JAO: agrego esto que no estaba!
			        buf.put((Byte) param);
				
				}else if (param.getClass().getName().equals("java.lang.Short")) {
			        buf.putShort((Short) param);
					
				} else if (param.getClass().getName().equals("java.lang.Integer")) {					
			        buf.putInt((Integer) param);
					
				} else if (param.getClass().getName().equals("java.lang.Long")) {					
			        buf.putLong((Long) param);
					
				} else if (param.getClass().getName().equals("java.lang.Char")) {
			        buf.putChar((Character) param);
					
				} else if (param.getClass().getName().equals("java.lang.String")) { //JAO: agrego esto que no estaba!
					String str = param.toString();
			        // longitud de la cadena
			        buf.putShort((short)str.length());
			        // bytes de la cadena
			        buf.put(str.getBytes());
			        
				} else {
					System.out.println("FAIL: BufferWriter.write(): tipo de dato desconocido - " + param.getClass().getName());
					System.exit(-1);
				}
			}
		}
	}

}
