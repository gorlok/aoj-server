package org.argentumonline.server.user;

public enum SpellAction {
	NONE,
	
    /*1*/ PROPERTIES,
    /*2*/ STATUS,
    /*3*/ MATERIALIZE, // UNUSED
    /*4*/ SUMMON;
	
	public byte value() {
		return (byte) (this.ordinal());
	}
	
	private final static SpellAction[] VALUES = SpellAction.values();
	public static SpellAction value(int value) {
		return VALUES[value];
	}
}