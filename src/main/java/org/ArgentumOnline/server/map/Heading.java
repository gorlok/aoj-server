package org.ArgentumOnline.server.map;

public enum Heading {
    NONE,
    
    /*1*/ NORTH,
    /*2*/ EAST,
    /*3*/ SOUTH,
    /*4*/ WEST;
	
	public byte value() {
		return (byte) (this.ordinal());
	}
    
	// cache values() because performance
	private static final Heading[] values = Heading.values();
	
    public static Heading value(int heading) {
    	return values[heading];
    }
}