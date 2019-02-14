package org.ArgentumOnline.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class MapPosTest {
	
	@Test
	void mapPosCreate() {
		MapPos mp1 = MapPos.xy(1, 1);
		MapPos mp2 = MapPos.xy(50, 50);
		MapPos mp3 = MapPos.xy(100, 100);
		
		assertEquals(1, mp1.x);
		assertEquals(1, mp1.y);
		
		assertEquals(50, mp2.x);
		assertEquals(50, mp2.y);

		assertEquals(100, mp3.x);
		assertEquals(100, mp3.y);
	}

	@Test
	void mapPosValidPos() {
		MapPos mp1 = MapPos.xy(1, 1);
		MapPos mp2 = MapPos.xy(50, 50);
		MapPos mp3 = MapPos.xy(100, 100);
		
		MapPos mp4 = MapPos.xy(0, 50);
		MapPos mp5 = MapPos.xy(50, 0);
		MapPos mp6 = MapPos.xy(101, 50);
		MapPos mp7 = MapPos.xy(50, 101);
		
		assertTrue(mp1.isValid());
		assertTrue(mp2.isValid());
		assertTrue(mp3.isValid());

		assertFalse(mp4.isValid());
		assertFalse(mp5.isValid());
		assertFalse(mp6.isValid());
		assertFalse(mp7.isValid());
	}
	
	@Test
	void mapPosValidXY() {
		assertTrue(MapPos.isValid(1, 1));
		assertTrue(MapPos.isValid(50, 50));
		assertTrue(MapPos.isValid(100, 100));
		
		assertFalse(MapPos.isValid(0, 50));
		assertFalse(MapPos.isValid(50, 0));
		assertFalse(MapPos.isValid(101, 50));
		assertFalse(MapPos.isValid(50, 101));
	}
	
	@Test
	void mapPosRangoVisionPos() {
		MapPos mp = MapPos.xy(50, 50);

		MapPos mp2 = MapPos.xy(55, 55);
		MapPos mp3 = MapPos.xy(100, 100);
		
		assertTrue(mp.inRangoVision(58, 50));
		assertTrue(mp.inRangoVision(50, 56));
		assertTrue(mp.inRangoVision(42, 50));
		assertTrue(mp.inRangoVision(50, 44));
		
		assertTrue(mp.inRangoVision(mp2));
		
		assertFalse(mp.inRangoVision(59, 50));
		assertFalse(mp.inRangoVision(50, 57));
		assertFalse(mp.inRangoVision(41, 50));
		assertFalse(mp.inRangoVision(50, 43));
		
		assertFalse(mp.inRangoVision(mp3));
	}

}
