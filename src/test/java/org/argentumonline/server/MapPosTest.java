package org.argentumonline.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.argentumonline.server.map.Heading;
import org.argentumonline.server.map.MapPos;
import org.junit.jupiter.api.Test;

public class MapPosTest {
	
	@Test
	void mapPosCreateEmpty() {
		MapPos mp = MapPos.empty();
		
		assertEquals(mp.map, 0);
		assertEquals(mp.x, 0);
		assertEquals(mp.y, 0);
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	void mapPosEqualsBasic() {
		MapPos mp1 = MapPos.mxy(10, 20, 30);
		
		assertTrue(mp1.equals(mp1));
		assertFalse(mp1.equals(null));
		assertFalse(mp1.equals("x"));
	}	

	@Test
	void mapPosEqualsTwoMapPos() {
		MapPos mp1 = MapPos.mxy(22, 33, 44);
		MapPos mp2 = MapPos.mxy(22, 33, 44);
		MapPos mp3 = MapPos.mxy(22, 33, 100);
		
		assertEquals(mp1, mp2);
		assertNotEquals(mp1, mp3);
	}
	
	@Test
	void mapPosCopy() {
		MapPos mp = MapPos.mxy(50, 10, 20);
		MapPos copy = mp.copy();
		
		MapPos modCopy = mp.copy();
		modCopy.set(33, 33, 33);

		assertEquals(mp, copy);
		assertNotEquals(mp, modCopy);
	}

	@Test
	void mapPosDistanceSameMap() {
		MapPos mp1 = MapPos.mxy(10, 20, 30);
		MapPos mp2 = MapPos.mxy(10, 30, 40);
		
		assertEquals(20, mp1.distance(mp2));
		assertEquals(20, mp2.distance(mp1));
		assertEquals(0, mp1.distance(mp1));
	}	

	@Test
	void mapPosDistanceOtherMap() {
		MapPos mp1 = MapPos.mxy(10, 10, 10);
		MapPos mp2 = MapPos.mxy(20, 10, 10);
		MapPos mp3 = MapPos.mxy(50, 50, 50);
		
		assertEquals(1000, mp1.distance(mp2));
		assertEquals(1000, mp2.distance(mp1));
		assertEquals(40*100+40+40, mp3.distance(mp1));
	}	
	
	@Test
	void mapPosMirarDir() {
		MapPos origin = MapPos.mxy(10, 10, 10);
		
		MapPos mp_north = origin.copy().moveToHeading(Heading.NORTH);
		MapPos mp_south = origin.copy().moveToHeading(Heading.SOUTH);
		MapPos mp_west = origin.copy().moveToHeading(Heading.WEST);
		MapPos mp_east = origin.copy().moveToHeading(Heading.EAST);
		
		MapPos same = origin.copy().moveToHeading(Heading.NONE);
		
		assertEquals(MapPos.mxy(10, 10, 9), mp_north);
		assertEquals(MapPos.mxy(10, 10, 11), mp_south);
		assertEquals(MapPos.mxy(10, 9, 10), mp_west);
		assertEquals(MapPos.mxy(10, 11, 10), mp_east);
		
		assertEquals(origin, same);
	}
	
	@Test
	void mapPosFindDirTest() {
		MapPos origin = 	MapPos.mxy(1, 50, 50);
		
		MapPos north = 	MapPos.mxy(1, 	50, 	1);
		MapPos south = 	MapPos.mxy(1, 	50, 	100);
		MapPos west = 	MapPos.mxy(1, 	1, 		50);
		MapPos east = 	MapPos.mxy(1, 	100, 	50);
		
		MapPos north_east = 	MapPos.mxy(1, 100, 1);
		MapPos north_west = 	MapPos.mxy(1, 1, 1);
		
		MapPos south_east = 	MapPos.mxy(1, 100, 100);
		MapPos south_west = 	MapPos.mxy(1, 1, 100);
		
		MapPos same = 	MapPos.mxy(1, 50, 50);
		
		assertEquals(Heading.NORTH, origin.findDirection(north));
		assertEquals(Heading.SOUTH, origin.findDirection(south));
		assertEquals(Heading.WEST, origin.findDirection(west));
		assertEquals(Heading.EAST, origin.findDirection(east));
		
		assertEquals(Heading.NORTH, origin.findDirection(north_east));
		assertEquals(Heading.WEST, origin.findDirection(north_west));
		assertEquals(Heading.SOUTH, origin.findDirection(south_east));
		assertEquals(Heading.WEST, origin.findDirection(south_west));
		
		assertEquals(Heading.NONE, origin.findDirection(same));
	}
	
	@Test
	void directionValue() {
		assertEquals(Heading.NORTH, Heading.value(1));
		assertEquals(Heading.WEST, Heading.value(4));
	}
	
	@Test
	void mapPosToString() {
		String str = MapPos.mxy(1, 2, 3).toString();
		
		assertEquals("(map=1,x=2,y=3)", str);
	}
	
}


