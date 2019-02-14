package org.ArgentumOnline.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.ArgentumOnline.server.WorldPos.Direction;
import org.junit.jupiter.api.Test;

public class WorldPosTest {
	
	@Test
	void worldPosCreateEmpty() {
		WorldPos wp = WorldPos.empty();
		
		assertEquals(wp.map, 0);
		assertEquals(wp.x, 0);
		assertEquals(wp.y, 0);
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	void worldPosEqualsBasic() {
		WorldPos wp1 = WorldPos.mxy(10, 20, 30);
		
		assertTrue(wp1.equals(wp1));
		assertFalse(wp1.equals(null));
		assertFalse(wp1.equals("x"));
	}	

	@Test
	void worldPosEqualsTwoWP() {
		WorldPos wp1 = WorldPos.mxy(22, 33, 44);
		WorldPos wp2 = WorldPos.mxy(22, 33, 44);
		WorldPos wp3 = WorldPos.mxy(22, 33, 100);
		
		assertEquals(wp1, wp2);
		assertNotEquals(wp1, wp3);
	}
	
	@Test
	void worldPosCopy() {
		WorldPos wp = WorldPos.mxy(50, 10, 20);
		WorldPos copy = wp.copy();
		
		WorldPos modCopy = wp.copy();
		modCopy.set(33, 33, 33);

		assertEquals(wp, copy);
		assertNotEquals(wp, modCopy);
	}

	@Test
	void worldPosDistanceSameMap() {
		WorldPos wp1 = WorldPos.mxy(10, 20, 30);
		WorldPos wp2 = WorldPos.mxy(10, 30, 40);
		
		assertEquals(20, wp1.distance(wp2));
		assertEquals(20, wp2.distance(wp1));
		assertEquals(0, wp1.distance(wp1));
	}	

	@Test
	void worldPosDistanceOtherMap() {
		WorldPos wp1 = WorldPos.mxy(10, 10, 10);
		WorldPos wp2 = WorldPos.mxy(20, 10, 10);
		WorldPos wp3 = WorldPos.mxy(50, 50, 50);
		
		assertEquals(1000, wp1.distance(wp2));
		assertEquals(1000, wp2.distance(wp1));
		assertEquals(40*100+40+40, wp3.distance(wp1));
	}	
	
	@Test
	void worldPosMirarDir() {
		WorldPos origin = WorldPos.mxy(10, 10, 10);
		
		WorldPos wp_north = origin.copy().moveToDir(Direction.NORTH);
		WorldPos wp_south = origin.copy().moveToDir(Direction.SOUTH);
		WorldPos wp_west = origin.copy().moveToDir(Direction.WEST);
		WorldPos wp_east = origin.copy().moveToDir(Direction.EAST);
		
		WorldPos same = origin.copy().moveToDir(Direction.NONE);
		
		assertEquals(WorldPos.mxy(10, 10, 9), wp_north);
		assertEquals(WorldPos.mxy(10, 10, 11), wp_south);
		assertEquals(WorldPos.mxy(10, 9, 10), wp_west);
		assertEquals(WorldPos.mxy(10, 11, 10), wp_east);
		
		assertEquals(origin, same);
	}
	
	@Test
	void worldPosFindDirTest() {
		WorldPos origin = 	WorldPos.mxy(1, 50, 50);
		
		WorldPos north = 	WorldPos.mxy(1, 	50, 	1);
		WorldPos south = 	WorldPos.mxy(1, 	50, 	100);
		WorldPos west = 	WorldPos.mxy(1, 	1, 		50);
		WorldPos east = 	WorldPos.mxy(1, 	100, 	50);
		
		WorldPos north_east = 	WorldPos.mxy(1, 100, 1);
		WorldPos north_west = 	WorldPos.mxy(1, 1, 1);
		
		WorldPos south_east = 	WorldPos.mxy(1, 100, 100);
		WorldPos south_west = 	WorldPos.mxy(1, 1, 100);
		
		WorldPos same = 	WorldPos.mxy(1, 50, 50);
		
		assertEquals(Direction.NORTH, origin.findDirection(north));
		assertEquals(Direction.SOUTH, origin.findDirection(south));
		assertEquals(Direction.WEST, origin.findDirection(west));
		assertEquals(Direction.EAST, origin.findDirection(east));
		
		assertEquals(Direction.NORTH, origin.findDirection(north_east));
		assertEquals(Direction.WEST, origin.findDirection(north_west));
		assertEquals(Direction.SOUTH, origin.findDirection(south_east));
		assertEquals(Direction.WEST, origin.findDirection(south_west));
		
		assertEquals(Direction.NONE, origin.findDirection(same));
	}
	
	@Test
	void directionValue() {
		assertEquals(Direction.NORTH, Direction.value(1));
		assertEquals(Direction.WEST, Direction.value(4));
	}
	
	@Test
	void worldPosToString() {
		String str = WorldPos.mxy(1, 2, 3).toString();
		
		assertEquals("(map=1,x=2,y=3)", str);
	}
	
}


