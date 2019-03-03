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

import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.UserStats;
import org.ArgentumOnline.server.UserAttributes.Attribute;
import org.ArgentumOnline.server.UserFaction.FactionArmors;
import org.ArgentumOnline.server.UserFaction;
import org.ArgentumOnline.server.UserRace;
import org.ArgentumOnline.server.util.Util;

/**
 * Class of the magician character.
 * @author  gorlok
 */
public class MagueClass extends AbstractClazz {
    
    /** Creates a new instance of ClaseAsesino */
    MagueClass() {
        this.name = "MAGO";
        this.magic = true;
    }
    
    @Override
	public int getManaInicial(int atribInteligencia) {
        return 100 + Util.Azar(1, atribInteligencia / 3);
    }
    
    /** Incremento de salud al subir de nivel */
    @Override
	protected int getMejoraSalud(UserStats estads) {
        return Util.Azar(4, estads.attr().get(Attribute.CONSTITUCION) / 2) + AdicionalHPGuerrero / 2;
    }
    
    /** Incremento de mana al subir de nivel */
    @Override
	protected int getMejoraMana(UserStats estads) {
        return 3 * estads.attr().get(Attribute.INTELIGENCIA);
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
	public short getArmaduraImperial(Player player) {
        if (player.race() == UserRace.RAZA_ENANO || player.race() == UserRace.RAZA_GNOMO) {
            return UserFaction.getFactionArmor(FactionArmors.TUNICA_MAGO_IMPERIAL_ENANOS);
        }
        return UserFaction.getFactionArmor(FactionArmors.TUNICA_MAGO_IMPERIAL);
    }
    
    @Override
	public short getArmaduraCaos(Player player) {
        if (player.race() == UserRace.RAZA_ENANO || player.race() == UserRace.RAZA_GNOMO) {
            return UserFaction.getFactionArmor(FactionArmors.TUNICA_MAGO_CAOS_ENANOS);
        }
        return UserFaction.getFactionArmor(FactionArmors.TUNICA_MAGO_CAOS);
    }
    
}
