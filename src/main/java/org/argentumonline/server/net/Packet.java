/*******************************************************************************
 *     Gorlok AO, an implementation of Argentum Online using Java.
 *     Copyright (C) 2019 Pablo Fernando Lillia «gorlok» 
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.argentumonline.server.net;

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