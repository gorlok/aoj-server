/**
 * JarMapTest.java
 *
 * Created on 23 de febrero de 2004, 22:59
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
package org.ArgentumOnline.server.test;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.net.JarURLConnection;
import java.net.URL;

/**
 * @author gorlok
 */
public class JarMapTest {
    
    /** Creates a new instance of JarMapTest */
    public JarMapTest() {
    	//
    }
    
    public static void main(String args[]) {
        try {
            URL url = new URL("jar:file:mapas.jar!/mapas/Mapa1.inf");
            JarURLConnection jarConnection = (JarURLConnection)url.openConnection();
            //Manifest manifest = jarConnection.getManifest();
            DataInputStream f =
                new DataInputStream(
                    new BufferedInputStream(
                        jarConnection.getInputStream()));
            System.out.println(f.readShort());
            f.close();            
            //System.out.println(manifest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
