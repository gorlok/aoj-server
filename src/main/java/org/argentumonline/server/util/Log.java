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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Pablo F. Lillia
 */
public class Log {
    
    private Log() {
    }
    
    public static String getTimestamp() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return df.format(new Date());
    }

    /** TODO */
    public static void logGM(String gmName, String msg) {
        System.out.println(getTimestamp() + " GM " + gmName + "> " + msg);
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
    
    /** TODO */
    public static void logCentinela(String msg) {
        System.out.println(getTimestamp() + " LOG CENTINELA: " + msg);
    }
    
}
