package org.ArgentumOnline.server.guilds;

import org.ArgentumOnline.server.util.Util;

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