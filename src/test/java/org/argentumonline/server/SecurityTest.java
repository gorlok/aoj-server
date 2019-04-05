package org.argentumonline.server;

import static org.junit.jupiter.api.Assertions.*;

import org.argentumonline.server.Security;
import org.junit.jupiter.api.Test;

public class SecurityTest {

	@Test
	void hashTest() throws Exception {
		String userName = "z";
		String password = "z";
		
		String hashPassword = Security.hashPassword(userName, password);
		System.out.println(hashPassword);
		
		assertTrue(Security.validatePassword(userName, password, hashPassword));
	}
}
