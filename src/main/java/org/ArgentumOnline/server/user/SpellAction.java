package org.ArgentumOnline.server.user;

public enum SpellAction {
    /*1*/ PROPERTIES,
    /*2*/ STATUS,
    /*3*/ MATERIALIZE, // UNUSED
    /*4*/ SUMMON;
	
	public byte value() {
		return (byte) (this.ordinal() + 1);
	}
	
	private final static SpellAction[] VALUES = SpellAction.values();
	public static SpellAction value(int value) {
		return VALUES[value - 1];
	}
}