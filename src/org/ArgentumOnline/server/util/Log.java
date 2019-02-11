/**
 * Log.java
 *
 * Created on 17 de septiembre de 2003, 22:44
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
package org.ArgentumOnline.server.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Pablo F. Lillia
 */
public class Log {
    
    static SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmssSSS");
    
    private static Logger serverLogger = Logger.getLogger("org.ArgentumOnline");
    
    /** Creates a new instance of Log */
    private Log() {
    	//
    }
    
    public static void start() 
    throws java.io.IOException {
        FileHandler fh;
        // Configure server logger
        fh = new FileHandler("log/server%g.log", 1000000, 10);
        fh.setFormatter(new LineFormatter());
        serverLogger.addHandler(fh);
        serverLogger.setLevel(Level.INFO);
        serverLogger.info("Server logger started");
    }
    
    public static void stop() {
        serverLogger.info("Server logger stoped");
    }
    
    public static Logger serverLogger() {
        return serverLogger;
    }
    
    public static String getTimestamp() {
        return df.format(new Date());
    }

    /** TODO */
    public static void logGM(String nickGM, String msg) {
        System.out.println(getTimestamp() + "GM " + nickGM + "> " + msg);
    }
    
    /** TODO */
    public static void logHack(String msg) {
        System.out.println(getTimestamp() + " HACK: " + msg);
    }

    /** TODO */
    public static void logVentaCasa(String msg) {
        System.out.println(getTimestamp() + " logVentaCasa: " + msg);
    }

    /** TODO */
    public static void logEjercitoReal(String msg) {
        System.out.println(getTimestamp() + " LOG EJERCITO REAL: " + msg);
    }
    
    /** TODO */
    public static void logEjercitoCaos(String msg) {
        System.out.println(getTimestamp() + " LOG EJERCITO CAOS: " + msg);
    }
}
