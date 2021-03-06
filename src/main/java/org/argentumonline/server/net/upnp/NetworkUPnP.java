/*******************************************************************************
 *     Gorlok AO, an implementation of Argentum Online using Java.
 *     Copyright (C) 2019 Pablo Fernando Lillia �gorlok� 
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
package org.argentumonline.server.net.upnp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.argentumonline.server.Constants;

public class NetworkUPnP {
	private static Logger log = LogManager.getLogger();
	
	private NetworkUPnP() {}

    public static void openUPnP() {
		log.warn("Attempting UPnP port forwarding...");
        if (UPnP.isUPnPAvailable()) { //is UPnP available?
            if (UPnP.isMappedTCP(Constants.SERVER_PORT)) { //is the port already mapped?
                log.warn("UPnP port forwarding not enabled: port is already mapped");
            } else if (UPnP.openPortTCP(Constants.SERVER_PORT)) { //try to map port
            	log.warn("UPnP port forwarding enabled");
            } else {
            	log.warn("UPnP port forwarding failed");
            }
        } else {
        	log.warn("UPnP is not available");
        }
    }
}
