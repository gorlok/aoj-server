package org.argentumonline.server;

import static org.junit.jupiter.api.Assertions.*;

import org.argentumonline.server.util.Color;
import org.junit.jupiter.api.Test;

public class ColorTest {

	@Test
	void redTest() throws Exception {
		assertEquals((byte)0xff, Color.r(Color.COLOR_ROJO));
		assertEquals((byte)0xff, Color.g(Color.COLOR_VERDE));
		assertEquals((byte)0xff, Color.b(Color.COLOR_AZUL));
		
		assertEquals(Color.r(Color.COLOR_ROJO), Color.r(Color.COLOR_BLANCO));
		assertEquals(Color.g(Color.COLOR_VERDE), Color.r(Color.COLOR_BLANCO));
		assertEquals(Color.b(Color.COLOR_AZUL), Color.r(Color.COLOR_BLANCO));
	}
	
}
