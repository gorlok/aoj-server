package org.ArgentumOnline.server.map;

public enum Zone {
	/*0*/ COUNTRY("CAMPO"),
	/*1*/ CITY("CIUDAD"),
	/*2*/ DUNGEON("DUNGEON");
	
	private String name;

	private Zone(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return this.name;
	}
}