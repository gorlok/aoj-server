package org.ArgentumOnline.server.net;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;

public class Packet {

	static Charset charset = Charset.forName("ISO-8859-1");

	protected static String readStr(ByteBuf in) {
		short len = in.readShortLE();
		return in.readCharSequence(len, charset).toString();			
	}

	protected static byte readByte(ByteBuf in) {
		return in.readByte();
	}

	protected static short readShort(ByteBuf in) {
		return in.readShortLE();
	}

	protected static int readInt(ByteBuf in) {
		return in.readIntLE();
	}

	protected static float readFloat(ByteBuf in) {
		return in.readFloatLE();
	}
	
	protected static byte[] readBytes(ByteBuf in, int len) {
		byte[] bytes = new byte[len];
		in.readBytes(bytes);
		return bytes;
	}

	protected void writeStr(ByteBuf out, String s) {
		out.writeShortLE(s.length());
		out.writeCharSequence(s, charset);
	}

	protected void writeByte(ByteBuf out, int b) {
		out.writeByte(b);
	}

	protected void writeShort(ByteBuf out, short s) {
		out.writeShortLE(s);
	}

	protected void writeInt(ByteBuf out, int i) {
		out.writeIntLE(i);
	}

	protected void writeFloat(ByteBuf out, float f) {
		out.writeFloatLE(f);
	}
	
	protected void writeBytes(ByteBuf out, byte[] bytes) {
		out.writeBytes(bytes);
	}
	
}