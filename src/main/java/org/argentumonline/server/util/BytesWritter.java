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
package org.argentumonline.server.util;

import java.nio.ByteBuffer;

import static java.nio.ByteOrder.*;

/**
 * JAO: Extraído de JFénix13. Gracias Franco!
 * Realiza operaciones de escritura sobre un array de bytes.
 *
 * bytes: array de bytes en donde se realiza la escritura
 * littleEndian: indica el orden en que se tienen que leer los bytes.
 */
public class BytesWritter {
    private byte[] bytes;
    private boolean littleEndian;

    public BytesWritter() {
        this(false);
    }

    public BytesWritter(boolean littleEndian) {
        bytes = new byte[0];
        setLittleEndian(littleEndian);
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void clear() {
        bytes = new byte[0];
    }

    public int getSize() {
        return bytes.length;
    }

    public boolean isLittleEndian() {
        return littleEndian;
    }

    public void setLittleEndian(boolean littleEndian) {
        this.littleEndian = littleEndian;
    }

    /**
     * Agrega los nuevos bytes al final del array principal de bytes.
     */
    private void add(byte[] b) {
        byte[] bytes2 = new byte[bytes.length + b.length];
        System.arraycopy(bytes, 0, bytes2, 0, bytes.length);
        System.arraycopy(b, 0, bytes2, bytes.length, b.length);
        bytes = bytes2;
    }

    public void writeByte(int val) {
        byte[] b = new byte[1];
        if (val > 127) val -= 256;
        b[0] = (byte)val;
        add(b);
    }

    public void writeBoolean(boolean val) {
        writeByte((byte)(val? 1 : 0));
    }

    public void writeShort(int val) {
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.order(LITTLE_ENDIAN);
        buf.putShort((short)val);
        buf.order(BIG_ENDIAN);
        add(buf.array());
    }

    public void writeInt(int val) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.order(LITTLE_ENDIAN);
        buf.putInt(val);
        buf.order(BIG_ENDIAN);
        add(buf.array());
    }

    public void writeFloat(float val) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.order(LITTLE_ENDIAN);
        buf.putFloat(val);
        buf.order(BIG_ENDIAN);
        add(buf.array());
    }

    public void writeDouble(double val) {
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.order(LITTLE_ENDIAN);
        buf.putDouble(val);
        buf.order(BIG_ENDIAN);
        add(buf.array());
    }

    public void writeString(String texto) {
        writeString(texto, -1);
    }

    public void writeString(String texto, int length) {
        // Si la longitud es por defecto o sobrepasa el límite, se ajusta al tamaño del texto.
        if (length == -1 || (length > texto.length()))
            length = texto.length();

        writeShort((short) length);

        // Si la longitud es 0 no agregamos nada.
        if (length == 0) return;
        add(texto.substring(0, length).getBytes());
    }


}
