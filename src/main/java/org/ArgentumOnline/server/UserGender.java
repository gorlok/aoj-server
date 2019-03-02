package org.ArgentumOnline.server;

public enum UserGender {
	
    GENERO_HOMBRE,
    GENERO_MUJER;
    
	private static final UserGender[] VALUES = UserGender.values();
	public static UserGender value(int value) {
		return VALUES[value];
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
    
}