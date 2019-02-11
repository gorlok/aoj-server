/**
 * BanditClass.java
 *
 * Created on 12 de marzo de 2004, 23:18
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
package org.ArgentumOnline.server.classes;

import org.ArgentumOnline.server.Client;
import org.ArgentumOnline.server.Factions;

/** 
 * Class of bandit character.
 * @author Pablo Fernando Lillia
 */
public class BanditClass extends CharClass {
    
    /** Creates a new instance of ClaseAsesino */
    protected BanditClass() {
        this.name = "BANDIDO";
    }
    
    private static BanditClass instance = null;
    
    public static BanditClass getInstance() {
        if (instance == null) {
			instance = new BanditClass();
		}
        return instance;
    }
    
    @Override
	public double modificadorEvasion() {
        return 0.9;
    }
        
    @Override
	public double modificadorPoderAtaqueArmas() {
        return 0.75;
    }
    
    @Override
	public double modificadorPoderAtaqueProyectiles() {
        return 0.8;
    }
    
    @Override
	public double modicadorDa�oClaseArmas() {
        return 0.8;
    }
     
    @Override
	public double modicadorDa�oClaseProyectiles() {
        return 0.75;
    }
    
    @Override
	public double modEvasionDeEscudoClase() {
        return 0.8;
    }
    
    @Override
	public short getArmaduraImperial(Client cliente) {
        if (cliente.getRaza() == RAZA_ENANO || cliente.getRaza() == RAZA_GNOMO) {
            return Factions.ArmaduraImperial3;
        }
        return Factions.ArmaduraImperial1;
    }
    
    @Override
	public short getArmaduraCaos(Client cliente) {
        if (cliente.getRaza() == RAZA_ENANO || cliente.getRaza() == RAZA_GNOMO) {
            return Factions.ArmaduraCaos3;
        }
        return Factions.ArmaduraCaos1;
    }
    
}
