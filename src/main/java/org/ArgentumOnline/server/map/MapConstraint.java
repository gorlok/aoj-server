package org.ArgentumOnline.server.map;

public enum MapConstraint {
	
	NONE("NO"),
	NEWBIE("NEWBIE"),
	FACTION("FACCION"),
	ROYAL_ARMY("ARMADA"),
	DARK_LEGION("CAOS");
	
	private String name;

	private MapConstraint(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	private static MapConstraint[] VALUES = MapConstraint.values();
	
	public static MapConstraint value(String value) {
		if (value == null) {
			return NONE;
		}
		for (MapConstraint constraint : VALUES) {
			if (constraint.toString().equalsIgnoreCase(value.trim())) {
				return constraint;
			}
		}
		return NONE;
	}
	
}