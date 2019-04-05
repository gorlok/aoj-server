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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * @author gorlok
 */
public class IniFile {

	private HashMap<String, HashMap<String, String>> data = new HashMap<String, HashMap<String, String>>();

	/** Creates a new instance of IniFile */
	public IniFile() {
	}

	/** Creates a new instance of IniFile */
	public IniFile(String filename)
			throws java.io.FileNotFoundException, java.io.IOException {
		load(filename);
	}

	public void store(String filename)
			throws java.io.FileNotFoundException, java.io.IOException {
		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		BufferedWriter f = new BufferedWriter(
				new OutputStreamWriter(
						new FileOutputStream(filename), "ISO-8859-1")); // "UTF-8"
		try {
			f.write(";Archivo: " + filename);
			f.write("\n;Ultima modificacion: " + df.format(new java.util.Date()) + "\n\n");
			TreeSet<String> ts = new TreeSet<String>(this.data.keySet());
			for (String section : ts) {
				HashMap<String, String> keys = this.data.get(section);
				f.write("[" + section + "]\n");
				TreeSet<String> kts = new TreeSet<String>(keys.keySet());
				for (String key : kts) {
					String value = keys.get(key);
					f.write(key + "=" + value + "\n");
				}
				f.write("\n");
			}
		} finally {
			f.close();
		}
	}

	/**
	 * Carga y parsea un archivo INI.
	 * 
	 * @param filename Nombre del archivo ini
	 * @throws FileNotFoundException Archivo no encontrado
	 * @throws IOException           Error de E/S
	 */
	public void load(String filename)
			throws java.io.FileNotFoundException, java.io.IOException {
		BufferedReader f = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(filename), "ISO-8859-1")); // "UTF-8"
		try {
			loadFromFile(f);
		} finally {
			f.close();
		}
	}

	private void loadFromFile(BufferedReader f)
			throws java.io.FileNotFoundException, java.io.IOException {
		int corcheteCierre, separador, comentario;
		String section = null, key = null, value = null;
		String str = f.readLine();
		while (str != null) {
			str = str.trim();
			if (str.length() > 0) {
				switch (str.charAt(0)) {
				case '\'':
				case ';':
				case '*':
				case '#':
					break;
				case '[':
					if ((corcheteCierre = str.indexOf(']')) > -1) {
						section = str.substring(1, corcheteCierre).toUpperCase();
						if (!this.data.containsKey(section)) {
							this.data.put(section, new HashMap<String, String>());
						}
					}
					break;
				default:
					// Si esta dentro de una seccion, y hay un signo = en la linea
					if ((section != null) && (separador = str.indexOf('=')) > -1) {
						key = str.substring(0, separador).trim().toUpperCase();
						// Si hay algo despues del signo =
						if (str.length() > separador) {
							value = str.substring(separador + 1, str.length()).trim();
							// Si hay un comentario al final de la linea, quitarlo.
							if ((comentario = value.indexOf(';')) > -1) {
								value = value.substring(0, comentario);
							}
						} else {
							value = "";
						}
						// Recuperar el conjunto de claves de la seccion,
						// y agregar el par (clave, valor).
						this.data.get(section).put(key, value);
					}
					break;
				}
			}
			str = f.readLine();
		}
	}

	/**
	 * Asigna el valor string de una clave en la seccion dada.
	 * 
	 * @param section el nombre de la seccion en el archivo ini
	 * @param key     el nombre de la clave
	 * @param val     el valor de la clave
	 */
	public void setValue(String section, String key, String val) {
		section = section.toUpperCase();
		if (!this.data.containsKey(section)) {
			this.data.put(section, new HashMap<String, String>());
		}
		this.data.get(section).put(key.toUpperCase(), val);
	}

	/**
	 * Asigna el valor string de una clave en la seccion dada.
	 * 
	 * @param section el nombre de la seccion en el archivo ini
	 * @param key     el nombre de la clave
	 * @param val     el valor de la clave
	 */
	public void setValue(String section, String key, double val) {
		section = section.toUpperCase();
		if (!this.data.containsKey(section)) {
			this.data.put(section, new HashMap<String, String>());
		}
		this.data.get(section).put(key.toUpperCase(), "" + (long) val);
	}

	/**
	 * Asigna el valor string de una clave en la seccion dada.
	 * 
	 * @param section el nombre de la seccion en el archivo ini
	 * @param key     el nombre de la clave
	 * @param val     el valor de la clave
	 */
	public void setValue(String section, String key, long val) {
		section = section.toUpperCase();
		if (!this.data.containsKey(section)) {
			this.data.put(section, new HashMap<String, String>());
		}
		this.data.get(section).put(key.toUpperCase(), "" + val);
	}

	/**
	 * Asigna el valor string de una clave en la seccion dada.
	 * 
	 * @param section el nombre de la seccion en el archivo ini
	 * @param key     el nombre de la clave
	 * @param val     el valor de la clave
	 */
	public void setValue(String section, String key, boolean val) {
		section = section.toUpperCase();
		if (!this.data.containsKey(section)) {
			this.data.put(section, new HashMap<String, String>());
		}
		this.data.get(section).put(key.toUpperCase(), val ? "1" : "0");
	}

	/**
	 * Devuelve el valor string de una clave en la seccion dada.
	 * 
	 * @param section      el nombre de la seccion en el archivo ini
	 * @param key          el nombre de la clave
	 * @param defaultValue el valor por defecto si la clave no existe o no tiene
	 *                     ningun valor
	 * @return el valor String de la clave
	 */
	public String getString(String section, String key, String defaultValue) {
		section = section.toUpperCase();
		String value = null;
		if (this.data.containsKey(section)) {
			value = this.data.get(section).get(key.toUpperCase());
		}
		if (value != null && !value.equals("")) {
			return value;
		}
		return defaultValue;
	}

	/**
	 * Devuelve el valor string de una clave en la seccion dada. El valor por
	 * defecto de la clave (si no existe) es "".
	 * 
	 * @param section el nombre de la seccion en el archivo ini
	 * @param key     el nombre de la clave
	 * @return el valor String de la clave
	 */
	public String getString(String section, String key) {
		return getString(section, key, "");
	}

	/**
	 * Devuelve el valor entero de una clave en la seccion dada.
	 * 
	 * @param section      el nombre de la seccion en el archivo ini
	 * @param key          el nombre de la clave
	 * @param defaultValue el valor por defecto si la clave no existe o no tiene
	 *                     ningun valor
	 * @return el valor de la clave
	 */
	public double getDouble(String section, String key, double defaultValue) {
		section = section.toUpperCase();
		String value = null;
		if (this.data.containsKey(section)) {
			value = this.data.get(section).get(key.toUpperCase());
		}
		if (value != null && !value.equals("")) {
			return Double.parseDouble(value);
		}
		return defaultValue;
	}

	/**
	 * Devuelve el valor entero de una clave en la seccion dada. El valor por
	 * defecto de la clave (si no existe) es 0.
	 * 
	 * @param section el nombre de la seccion en el archivo ini
	 * @param key     el nombre de la clave
	 * @return el valor de la clave
	 */
	public double getDouble(String section, String key) {
		return getDouble(section, key, 0);
	}

	/**
	 * Devuelve el valor entero de una clave en la seccion dada.
	 * 
	 * @param section      el nombre de la seccion en el archivo ini
	 * @param key          el nombre de la clave
	 * @param defaultValue el valor por defecto si la clave no existe o no tiene
	 *                     ningun valor
	 * @return el valor de la clave
	 */
	public long getLong(String section, String key, long defaultValue) {
		section = section.toUpperCase();
		String value = null;
		if (this.data.containsKey(section)) {
			value = this.data.get(section).get(key.toUpperCase());
		}
		if (value != null && !value.equals("")) {
			return Long.parseLong(value.split("'")[0].trim());
		}
		return defaultValue;
	}

	/**
	 * Devuelve el valor entero de una clave en la seccion dada. El valor por
	 * defecto de la clave (si no existe) es 0.
	 * 
	 * @param section el nombre de la seccion en el archivo ini
	 * @param key     el nombre de la clave
	 * @return el valor de la clave
	 */
	public long getLong(String section, String key) {
		return getLong(section, key, 0);
	}

	/**
	 * Devuelve el valor entero de una clave en la seccion dada.
	 * 
	 * @param section      el nombre de la seccion en el archivo ini
	 * @param key          el nombre de la clave
	 * @param defaultValue el valor por defecto si la clave no existe o no tiene
	 *                     ningun valor
	 * @return el valor int de la clave
	 */
	public int getInt(String section, String key, int defaultValue) {
		section = section.toUpperCase();
		String value = null;
		if (this.data.containsKey(section)) {
			value = this.data.get(section).get(key.toUpperCase());
		}
		if (value != null && !value.equals("")) {
			return Integer.parseInt(value.split("'")[0].trim());
		}
		return defaultValue;
	}

	/**
	 * Devuelve el valor entero de una clave en la seccion dada. El valor por
	 * defecto de la clave (si no existe) es 0.
	 * 
	 * @param section el nombre de la seccion en el archivo ini
	 * @param key     el nombre de la clave
	 * @return el valor int de la clave
	 */
	public int getInt(String section, String key) {
		return getInt(section, key, 0);
	}

	/**
	 * Devuelve el valor entero de una clave en la seccion dada.
	 * 
	 * @param section      el nombre de la seccion en el archivo ini
	 * @param key          el nombre de la clave
	 * @param defaultValue el valor por defecto si la clave no existe o no tiene
	 *                     ningun valor
	 * @return el valor short de la clave
	 */
	public short getShort(String section, String key, short defaultValue) {
		section = section.toUpperCase();
		String value = null;
		if (this.data.containsKey(section)) {
			value = this.data.get(section).get(key.toUpperCase());
		}
		if (value != null && !value.equals("")) {
			return Short.parseShort(value.split("'")[0].trim());
		}
		return defaultValue;
	}

	/**
	 * Devuelve el valor entero de una clave en la seccion dada. El valor por
	 * defecto de la clave (si no existe) es 0.
	 * 
	 * @param section el nombre de la seccion en el archivo ini
	 * @param key     el nombre de la clave
	 * @return el valor short de la clave
	 */
	public short getShort(String section, String key) {
		return getShort(section, key, (short) 0);
	}

}
