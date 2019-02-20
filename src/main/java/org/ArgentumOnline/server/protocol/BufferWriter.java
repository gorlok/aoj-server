package org.ArgentumOnline.server.protocol;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author gorlok
 * @last modified by JAO (userforos: agushh/thorkes) Esta clase solo codifica un
 *       array de bytes para enviar al cliente
 */
public class BufferWriter {
	private static Logger log = LogManager.getLogger();

	private BufferWriter() {
	}

	public static void write(ByteBuffer buf, ServerPacketID packetId, Object... params) {
		
		log.warn("WRITE:" + packetId.name() + " => " + Arrays.toString(params));

		buf.put(packetId.id());

		if (params != null) {
			for (Object param : params) {
				if (param.getClass().getName().equals("java.lang.Byte")) {
					buf.put((Byte) param);

				} else if (param.getClass().getName().equals("java.lang.Short")) {
					buf.putShort((Short) param);

				} else if (param.getClass().getName().equals("java.lang.Integer")) {
					buf.putInt((Integer) param);

				} else if (param.getClass().getName().equals("java.lang.Long")) {
					buf.putLong((Long) param);

				} else if (param.getClass().getName().equals("java.lang.Float")) {
					buf.putFloat((Float) param);

				} else if (param.getClass().getName().equals("java.lang.Char")) {
					buf.putChar((Character) param);

				} else if (param.getClass().getName().equals("java.lang.String")) {
					String str = param.toString();
					// longitud de la cadena
					buf.putShort((short) str.length());
					// bytes de la cadena
					buf.put(str.getBytes());
					
				} else {
					System.out.println(
							"FAIL: BufferWriter.write(): tipo de dato desconocido - " + param.getClass().getName());
					System.exit(-1);
				}
			}
		}
	}

}
