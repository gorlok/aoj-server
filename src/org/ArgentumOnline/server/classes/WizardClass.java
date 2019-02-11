/**
 * WizardClass.java
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
import org.ArgentumOnline.server.UserStats;
import org.ArgentumOnline.server.Factions;
import org.ArgentumOnline.server.util.Util;

/**
 * Class of the magician character.
 * @author  Pablo Fernando Lillia
 */
public class WizardClass extends CharClass {
    
    /** Creates a new instance of ClaseAsesino */
    protected WizardClass() {
        this.name = "MAGO";
        this.magica = true;
    }
    
    private static WizardClass instance = null;
    
    public static WizardClass getInstance() {
        if (instance == null) {
			instance = new WizardClass();
		}
        return instance;
    }
    
    @Override
	public int getManaInicial(int atribInteligencia) {
        return 100 + Util.Azar(1, atribInteligencia / 3);
    }
    
    /** Incremento de salud al subir de nivel */
    @Override
	protected int getMejoraSalud(UserStats estads) {
        return Util.Azar(4, estads.userAtributos[ATRIB_CONSTITUCION] / 2) + AdicionalHPGuerrero / 2;
    }
    
    /** Incremento de mana al subir de nivel */
    @Override
	protected int getMejoraMana(UserStats estads) {
        return 3 * estads.userAtributos[ATRIB_INTELIGENCIA];
    }
    
    /** Incremento de stamina al subir de nivel */
    @Override
	protected int getMejoraStamina() {
        int valor = 15 - AdicionalSTLadron / 2;
        return (valor < 1) ? 5 : valor;
    }
    
    /** Incremento de golpe al subir de nivel */
    @Override
	protected int getMejoraGolpe() {
        return 1;
    }
    
    @Override
	public short getArmaduraImperial(Client cliente) {
        if (cliente.getRaza() == RAZA_ENANO || cliente.getRaza() == RAZA_GNOMO) {
            return Factions.TunicaMagoImperialEnanos;
        }
        return Factions.TunicaMagoImperial;
    }
    
    @Override
	public short getArmaduraCaos(Client cliente) {
        if (cliente.getRaza() == RAZA_ENANO || cliente.getRaza() == RAZA_GNOMO) {
            return Factions.TunicaMagoCaosEnanos;
        }
        return Factions.TunicaMagoCaos;
    }
    
}
