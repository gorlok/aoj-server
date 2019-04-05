package org.argentumonline.server.map;

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
    
    public Heading invertHeading() {
    	// Returns the opposite heading 
    	switch (this) {
		case EAST:
			return WEST;
		case WEST:
			return EAST;
		case SOUTH:
			return NORTH;
		case NORTH:
			return SOUTH;
		default:
			return NONE;
    	}
    }
    
}