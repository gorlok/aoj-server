package org.ArgentumOnline.server.map;

public enum Terrain {
    /*0*/ FOREST("BOSQUE"),
    /*2*/ DESERT("DESIERTO"),
    /*3*/ SNOW("NIEVE");

	private String name;
    
    private Terrain(String name) {
		this.name = name;
	}
    
    @Override
    public String toString() {
    	return this.name;
    }
}