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
import org.ArgentumOnline.server.UserFaction;
import org.ArgentumOnline.server.UserRace;
import org.ArgentumOnline.server.UserFaction.FactionArmors;

/** 
 * Class of bandit character.
 * @author gorlok
 */
public class BanditClass extends AbstractClazz {
    
    BanditClass() {
        this.name = "BANDIDO";
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
	public double modicadorDañoClaseArmas() {
        return 0.8;
    }
     
    @Override
	public double modicadorDañoClaseProyectiles() {
        return 0.75;
    }
    
    @Override
	public double modEvasionDeEscudoClase() {
        return 0.8;
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
