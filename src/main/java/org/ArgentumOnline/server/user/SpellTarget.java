package org.ArgentumOnline.server.user;

public enum SpellTarget {
	NONE,
	
    /*1*/ User,
    /*2*/ Npc,
    /*3*/ UserAndNpc,
    /*4*/ Terrain;
	
	public byte value() { 
		return (byte) (this.ordinal());
	}
	
	private final static SpellTarget[] VALUES = SpellTarget.values();
	public static SpellTarget value(int value) {
		return VALUES[value];
	}
	
}