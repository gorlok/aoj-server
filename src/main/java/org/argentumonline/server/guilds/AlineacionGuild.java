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
package org.argentumonline.server.guilds;

import org.argentumonline.server.util.Util;

public enum AlineacionGuild {
    ALINEACION_LEGION,
    ALINEACION_CRIMINAL,
    ALINEACION_NEUTRO,
    ALINEACION_CIUDADANO,
    ALINEACION_ARMADA,
    ALINEACION_MASTER;
    
    private static AlineacionGuild values[] = AlineacionGuild.values();
    
    /**
     * Gives enumeration value at ordinal index 
     * @param index starting at 1
     * @return enumeration value
     */
    public static AlineacionGuild value(int i) {
    	return values[i-1];
    }
    
    /**
     * Gives ordinal value, starting at 1
     * @return ordinal value
     */
    public byte value() {
    	return (byte) (this.ordinal()+1);
    }
    
    @Override
    public String toString() {
    	return Util.capitalize(super.toString().split("_")[1]);
    }
}