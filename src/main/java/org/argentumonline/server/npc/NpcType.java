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
package org.argentumonline.server.npc;

/**
 * @author gorlok
 */
public enum NpcType {
	
    NPCTYPE_COMMON,
    NPCTYPE_PRIEST,
    NPCTYPE_ROYAL_GUARD,
    NPCTYPE_TRAINER,
    NPCTYPE_CASHIER,
    NPCTYPE_NOBLE,
    NPCTYPE_DRAGON,
    NPCTYPE_GAMBLER,
    NPCTYPE_GUARDAS_CHAOS,
    NPCTYPE_PRIEST_NEWBIES,
    
    @Deprecated NPCTYPE_QUEST,
    @Deprecated NPCTYPE_AMIGOQUEST;
	
	private static final NpcType[] VALUES = NpcType.values();
	
	public static NpcType value(int value) {
		return VALUES[value];
	}
	
	public byte value() {
		return (byte) ordinal();
	}
}