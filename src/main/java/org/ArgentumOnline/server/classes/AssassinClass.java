/**
 * AssassinClass.java
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

import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.UserStats;
import org.ArgentumOnline.server.Factions;
import org.ArgentumOnline.server.Factions.FactionArmors;
import org.ArgentumOnline.server.util.Util;

/**
 * Class of assassin character.
 * @author  gorlok
 */
public class AssassinClass extends AbstractClazz {
    
    AssassinClass() {
        this.name = "ASESINO";
        this.magic = true;
    }
    
    @Override
	public double modificadorEvasion() {
        return 1.1;
    }
        
    @Override
	public double modificadorPoderAtaqueArmas() {
        return 0.85;
    }
    
    @Override
	public double modificadorPoderAtaqueProyectiles() {
        return 0.75;
    }
    
    @Override
	public double modicadorDañoClaseArmas() {
        return 0.9;
    }
     
    @Override
	public double modicadorDañoClaseProyectiles() {
        return 0.8;
    }
    
    @Override
	public double modEvasionDeEscudoClase() {
        return 0.8;
    }
    
    @Override
	public int getManaInicial(int atribInteligencia) {
        return 50;
    }
    
    /** Incremento de salud al subir de nivel */
    @Override
	protected int getMejoraSalud(UserStats estads) {
        return Util.Azar(4, estads.userAttributes[ATRIB_CONSTITUCION] / 2);
    }
    
    /** Incremento de mana al subir de nivel */
    @Override
	protected int getMejoraMana(UserStats estads) {
        return estads.userAttributes[ATRIB_INTELIGENCIA];
    }
    
    /** Incremento de stamina al subir de nivel */
    @Override
	protected int getMejoraStamina() {
        return 15;
    }
    
    /** Incremento de golpe al subir de nivel */
    @Override
	protected int getMejoraGolpe() {
        return 3;
    }
    
    @Override
	public short getArmaduraImperial(Player cliente) {
        if (cliente.race() == RAZA_ENANO || cliente.race() == RAZA_GNOMO) {
            return Factions.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_3);
        }
        return Factions.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_1);
    }
    
    @Override
	public short getArmaduraCaos(Player cliente) {
        if (cliente.race() == RAZA_ENANO || cliente.race() == RAZA_GNOMO) {
            return Factions.getFactionArmor(FactionArmors.ARMADURA_CAOS_3);
        }
        return Factions.getFactionArmor(FactionArmors.ARMADURA_CAOS_1);
    }
    
}
