package org.ArgentumOnline.server;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ObjTypeTest {
	
	@Test
	void valueTest() throws Exception {
		assertEquals(ObjType.Foros, ObjType.value(10));
	}

}
