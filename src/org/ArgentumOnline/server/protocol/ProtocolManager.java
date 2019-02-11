/**
 * ProtocolManager.java
 * 
 * Created 28/jan/2007
 * 
    AOJava Server
    Copyright (C) 2003-2007 Pablo Fernando Lillia (alias Gorlok)
    Web site: http://www.aojava.com.ar
    
    This file is part of AOJava.

    AOJava is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    AOJava is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA 
 */
package org.ArgentumOnline.server.protocol;

import java.util.Properties;

import org.ArgentumOnline.server.Client;
import org.ArgentumOnline.server.AojServer;
import org.ArgentumOnline.server.util.Log;

/**
 * @author gorlok
 *
 */
public class ProtocolManager {
	
	private ProtocolManager() {
		//
	}
	
	public static Protocol getDefaultProtocol(AojServer server, Client cliente) {
		try {
			Properties props = server.getConfig();
			String protocolClassName = props.getProperty("server.protocol", "org.ArgentumOnline.server.protocol.BinaryProtocol");
			Protocol protocol = (Protocol) Class.forName(protocolClassName).newInstance();
			protocol.setCliente(cliente);
			return protocol;
		} catch (Exception e) {
			Log.serverLogger().severe("Error loading protocol class. " + e.getMessage());
			return null;
		}
	}
}
