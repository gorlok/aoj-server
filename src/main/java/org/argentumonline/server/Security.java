package org.argentumonline.server;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class Security {
	
	private static String strToHash(String userName, String password) {
		return (userName.trim().toLowerCase() + password.trim());
	}
	
	/**
	 * Hash username+password with salt, using bcrypt algo.
	 * @param userName is not case-sensitive.
	 * @param password is case-sensitive
	 * @return hash string
	 */
	public static String hashPassword(String userName, String password) {
		String strToHash = strToHash(userName, password);

		String bcryptHashString = BCrypt
				.with(BCrypt.Version.VERSION_2A)
				.hashToString(8, strToHash.toCharArray());
		
		return bcryptHashString;
	}

	/**
	 * Validate a hash string created by {@link #hashPassword(String, String)}
	 * @param userName is not case-sensitive.
	 * @param password is case-sensitive
	 * @param storedHash is hash string created by hashPassword(u, p)
	 * @return true if hash is valid, false otherwise.
	 */
	public static boolean validatePassword(String userName, String password, String storedHash) {
		if (storedHash == null || storedHash.isEmpty()) {
			return false;
		}
		
		String strToHash = strToHash(userName, password);
		
		BCrypt.Result result = BCrypt.verifyer().verify(strToHash.toCharArray(), storedHash);
		return result.verified;
	}

}
