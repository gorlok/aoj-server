package org.argentumonline.server;

public enum City {
	NONE(""),
	
    /*1*/ ULLATHORPE	("Ullathorpe"),
    /*2*/ NIX			("Nix"),
    /*3*/ BANDERBILL	("Banderbill"),
    /*4*/ LINDOS		("Lindos"),
    /*5*/ ARGHAL		("Arghâl");
	
	private String name;
	
	private City(String name) {
		this.name = name;
	}
	
	public byte id() {
		return (byte) this.ordinal();
	}
	
	public String toString() {
		return this.name;
	}
	
	private static final City[] VALUES = City.values(); 
	public static City value(int index) {
		return VALUES[index];
	}
	
}