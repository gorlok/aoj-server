package org.ArgentumOnline.server.net;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;

public class Packet {

	static Charset charset = Charset.forName("ISO-8859-1");

	protected static String readStr(ByteBuf in) {
		short len = in.readShort();
		return in.readCharSequence(len, charset).toString();			
	}

	protected static byte readByte(ByteBuf in) {
		return in.readByte();
	}

	protected static short readShort(ByteBuf in) {
		return in.readShort();
	}

	protected static int readInt(ByteBuf in) {
		return in.readInt();
	}

	protected static float readFloat(ByteBuf in) {
		return in.readFloat();
	}
	
	protected static byte[] readBytes(ByteBuf in, int len) {
		byte[] bytes = new byte[len];
		in.readBytes(bytes);
		return bytes;
	}

}