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
package org.ArgentumOnline.server.classes;

import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.UserStats;
import org.ArgentumOnline.server.UserAttributes.Attribute;
import org.ArgentumOnline.server.UserFaction.FactionArmors;
import org.ArgentumOnline.server.UserFaction;
import org.ArgentumOnline.server.UserRace;
import org.ArgentumOnline.server.util.Util;

/**
 * Character of the paladin character.
 * @author gorlok
 */
public class PaladinClass extends AbstractClazz {
    
    PaladinClass() {
        this.name = "PALADIN";
    }
    
    @Override
	public double modificadorEvasion() {
        return 0.9;
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
        return 1.0;
    }
    
    /** Incremento de salud al subir de nivel */
    @Override
	protected int getMejoraSalud(UserStats estads) {
        return Util.Azar(4, estads.attr().get(Attribute.CONSTITUCION) / 2) + AdicionalHPGuerrero;
    }
    
    /** Incremento de mana al subir de nivel */
    @Override
	protected int getMejoraMana(UserStats estads) {
        return estads.attr().get(Attribute.INTELIGENCIA);
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
	public short getArmaduraImperial(Player player) {
        if (player.race() == UserRace.RAZA_ENANO || player.race() == UserRace.RAZA_GNOMO) {
            return UserFaction.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_3);
        }
        return UserFaction.getFactionArmor(FactionArmors.ARMADURA_IMPERIAL_1);
    }
    
    @Override
	public short getArmaduraCaos(Player player) {
        if (player.race() == UserRace.RAZA_ENANO || player.race() == UserRace.RAZA_GNOMO) {
            return UserFaction.getFactionArmor(FactionArmors.ARMADURA_CAOS_3);
        }
        return UserFaction.getFactionArmor(FactionArmors.ARMADURA_CAOS_1);
    }
    
}
