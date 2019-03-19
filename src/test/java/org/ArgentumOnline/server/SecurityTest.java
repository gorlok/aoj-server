package org.ArgentumOnline.server;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class SecurityTest {

	@Test
	void hashTest() throws Exception {
		String userName = "d";
		String password = "d";
		
		String hashPassword = Security.hashPassword(userName, password);
		System.out.println(hashPassword);
		
		assertTrue(Security.validatePassword(userName, password, hashPassword));
	}
}
