package org.argentumonline.server.user;

public enum SpellTarget {
	NONE,
	
    /*1*/ USER,
    /*2*/ NPC,
    /*3*/ USER_AND_NPC,
    /*4*/ TERRAIN;
	
	public byte value() { 
		return (byte) (this.ordinal());
	}
	
	private final static SpellTarget[] VALUES = SpellTarget.values();
	public static SpellTarget value(int value) {
		return VALUES[value];
	}
	
}