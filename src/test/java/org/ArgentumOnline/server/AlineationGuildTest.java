package org.ArgentumOnline.server;

import org.ArgentumOnline.server.guilds.AlineacionGuild;
import org.junit.jupiter.api.Test;

public class AlineationGuildTest {
	
	@Test
	void alineationToStringTest() throws Exception {
		
		for (var a : AlineacionGuild.values()) {
			System.out.println(a.toString());
		}
	}

}
