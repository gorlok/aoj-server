package org.ArgentumOnline.server;

/**
 * @author gorlok
 */
public enum UserRace {
	
	RAZA_HUMANO ("Humano", 2, 1, 2, 1, 0),
	RAZA_ELFO 	("Elfo", 0, 2, 0, 2, 2),
	RAZA_DROW   ("Elfo Oscuro", 1, 2, 0, 2, 2),
	RAZA_ENANO	("Enano", 3, 0, 3, -6, 0),
	RAZA_GNOMO	("Gnomo", -5, 3, 0, 3, 0);

	private String name;
    private int modificadorFuerza;
    private int modificadorAgilidad;
    private int modificadorConstitucion;
    private int modificadorInteligencia;
    private int modificadorCarisma;
	
	private UserRace(String name, 
			int modificadorFuerza, 
			int modificadorAgilidad, 
			int modificadorConstitucion,
			int modificadorInteligencia, 
			int modificadorCarisma) {
		this.name = name;
		this.modificadorFuerza = modificadorFuerza;
		this.modificadorAgilidad = modificadorAgilidad;
		this.modificadorConstitucion = modificadorConstitucion;
		this.modificadorInteligencia = modificadorInteligencia;
		this.modificadorCarisma = modificadorCarisma;
	}
	
	private static final UserRace[] VALUES = UserRace.values();
	public static UserRace value(int value) {
		return VALUES[value];
	}
	
	public String toString() {
		return this.name;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}

	public String getName() {
		return name;
	}

	public byte modificadorFuerza() {
		return (byte)modificadorFuerza;
	}

	public byte modificadorAgilidad() {
		return (byte)modificadorAgilidad;
	}

	public byte modificadorConstitucion() {
		return (byte)modificadorConstitucion;
	}

	public byte modificadorInteligencia() {
		return (byte)modificadorInteligencia;
	}

	public byte modificadorCarisma() {
		return (byte)modificadorCarisma;
	}
	
}

