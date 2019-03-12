package org.ArgentumOnline.server;

public enum Ciudad {
	NONE(""),
	
    /*1*/ ULLATHORPE	("Ullathorpe"),
    /*2*/ NIX			("Nix"),
    /*3*/ BANDERBILL	("Banderbill"),
    /*4*/ LINDOS		("Lindos"),
    /*5*/ ARGHAL		("Arghâl");
	
	private String name;
	
	private Ciudad(String name) {
		this.name = name;
	}
	
	public byte id() {
		return (byte) this.ordinal();
	}
	
	public String toString() {
		return this.name;
	}
	
	private static final Ciudad[] VALUES = Ciudad.values(); 
	public static Ciudad value(int index) {
		return VALUES[index];
	}
	
}