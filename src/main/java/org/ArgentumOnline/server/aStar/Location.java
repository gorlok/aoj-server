package org.ArgentumOnline.server.aStar;public class Location {    public int x, y; // max values 999 and 999        public Location(int x, int y) {        this.x = x;        this.y = y;    }    @Override	public boolean equals(Object loc) {    	if (loc == null)    		return false;    	    	if (! (loc instanceof Location))    		return false;    	        return ((Location)loc).x == this.x && ((Location)loc).y == this.y;    }        @Override	public int hashCode() {        return 1000 * this.x + this.y;    }}