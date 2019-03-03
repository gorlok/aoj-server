package org.ArgentumOnline.server.npc;

/**
 * @author gorlok
 */
public enum NpcType {
	
    NPCTYPE_COMUN,
    NPCTYPE_SACERDOTE,
    NPCTYPE_GUARDIAS,
    NPCTYPE_ENTRENADOR,
    NPCTYPE_BANQUERO,
    NPCTYPE_NOBLE,
    NPCTYPE_DRAGON,
    NPCTYPE_TIMBERO,
    NPCTYPE_GUARDIAS_CAOS,
    NPCTYPE_SACERDOTE_NEWBIES,
    
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