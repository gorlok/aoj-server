package org.argentumonline.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.argentumonline.server.Pos;
import org.junit.jupiter.api.Test;

public class PosTest {
	
	@Test
	void posCreate() {
		Pos p1 = Pos.xy(1, 1);
		Pos p2 = Pos.xy(50, 50);
		Pos p3 = Pos.xy(100, 100);
		
		assertEquals(1, p1.x);
		assertEquals(1, p1.y);
		
		assertEquals(50, p2.x);
		assertEquals(50, p2.y);

		assertEquals(100, p3.x);
		assertEquals(100, p3.y);
	}

	@Test
	void posValidPos() {
		Pos p1 = Pos.xy(1, 1);
		Pos p2 = Pos.xy(50, 50);
		Pos p3 = Pos.xy(100, 100);
		
		Pos p4 = Pos.xy(0, 50);
		Pos p5 = Pos.xy(50, 0);
		Pos p6 = Pos.xy(101, 50);
		Pos p7 = Pos.xy(50, 101);
		
		assertTrue(p1.isValid());
		assertTrue(p2.isValid());
		assertTrue(p3.isValid());

		assertFalse(p4.isValid());
		assertFalse(p5.isValid());
		assertFalse(p6.isValid());
		assertFalse(p7.isValid());
	}
	
	@Test
	void posValidXY() {
		assertTrue(Pos.isValid(1, 1));
		assertTrue(Pos.isValid(50, 50));
		assertTrue(Pos.isValid(100, 100));
		
		assertFalse(Pos.isValid(0, 50));
		assertFalse(Pos.isValid(50, 0));
		assertFalse(Pos.isValid(101, 50));
		assertFalse(Pos.isValid(50, 101));
	}
	
	@Test
	void posRangoVisionPos() {
		Pos p1 = Pos.xy(50, 50);

		Pos p2 = Pos.xy(55, 55);
		Pos p3 = Pos.xy(100, 100);
		
		assertTrue(p1.inRangoVision(58, 50));
		assertTrue(p1.inRangoVision(50, 56));
		assertTrue(p1.inRangoVision(42, 50));
		assertTrue(p1.inRangoVision(50, 44));
		
		assertTrue(p1.inRangoVision(p2));
		
		assertFalse(p1.inRangoVision(59, 50));
		assertFalse(p1.inRangoVision(50, 57));
		assertFalse(p1.inRangoVision(41, 50));
		assertFalse(p1.inRangoVision(50, 43));
		
		assertFalse(p1.inRangoVision(p3));
	}

}
