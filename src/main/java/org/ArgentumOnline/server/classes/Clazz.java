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

public enum Clazz {

    Mage(new MagueClass()),        // Mago
    Cleric(new ClericClass()),      // Clérigo
    Warrior(new WarriorClass()),     // Guerrero
    Assassin(new AssassinClass()),     // Asesino
    Thief(new ThiefClass()),       // Ladrón
    Bard(new BardClass()),        // Bardo
    Druid(new DruidClass()),       // Druida
    Bandit(new BanditClass()),      // Bandido
    Paladin(new PaladinClass()),     // Paladín
    Hunter(new HunterClass()),      // Cazador
    Fisher(new FisherClass()),      // Pescador
    Blacksmith(new BlacksmithClass()),  // Herrero
    Lumberjack(new LumberjackClass()),  // Leñador
    Miner(new MinerClass()),       // Minero
    Carpenter(new CarpenterClass()),   // Carpintero
    Pirate(new PirateClass());      // Pirata

	private AbstractClazz clazz;
	
	private Clazz(AbstractClazz clazz) {
		this.clazz = clazz;
	}
	
	public byte id() {
		return (byte) (ordinal() + 1);
	}
    
	public AbstractClazz clazz() {
		return this.clazz;
	}
	
	public static Clazz[] values = Clazz.values();
	public static Clazz value(int i) {
		return values[i-1];
	}
	
	public static Clazz findByName(String name) {
		for (Clazz c : values) {
			if (c.clazz().getName().equalsIgnoreCase(name))
				return c;
		}
		return null;
	}
}
