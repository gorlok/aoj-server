/**
 * FishermanClass.java
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

import org.ArgentumOnline.server.UserStats;
import org.ArgentumOnline.server.util.Util;

/**
 * Class of the fisherman character.
 * @author  Pablo Fernando Lillia
 */
public class FishermanClass extends CharClass {
    
    /** Creates a new instance of ClaseAsesino */
    protected FishermanClass() {
        this.name = "PESCADOR";
    }
    
    private static FishermanClass instance = null;
    
    public static FishermanClass getInstance() {
        if (instance == null) {
			instance = new FishermanClass();
		}
        return instance;
    }
    
    @Override
	public double modificadorPoderAtaqueArmas() {
        return 0.6;
    }
    
    @Override
	public double modificadorPoderAtaqueProyectiles() {
        return 0.65;
    }
    
    @Override
	public double modicadorDañoClaseArmas() {
        return 0.6;
    }
     
    @Override
	public double modicadorDañoClaseProyectiles() {
        return 0.6;
    }
    
    @Override
	public double modEvasionDeEscudoClase() {
        return 0.7;
    }
    
    @Override
	public double modNavegacion() {
        return 1.2;
    }
    
    @Override
	public short getEsfuerzoPescar() {
        return 1;
    }
    
    /** Incremento de salud al subir de nivel */
    @Override
	protected int getMejoraSalud(UserStats estads) {
        return Util.Azar(4, estads.userAtributos[ATRIB_CONSTITUCION] / 2);
    }
    
    /** Incremento de stamina al subir de nivel */
    @Override
	protected int getMejoraStamina() {
        return 15 + AdicionalSTPescador;
    }
    
    /** Incremento de golpe al subir de nivel */
    @Override
	protected int getMejoraGolpe() {
        return 1;
    }
    
}
