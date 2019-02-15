package org.ArgentumOnline.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dosse.upnp.UPnP;

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
