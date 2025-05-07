package com.rnt.SecuritySystem.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha1Encriptor {

	public static String encryptThisString(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("sha1");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger no = new BigInteger(1, messageDigest);
			StringBuilder hashtext = new StringBuilder(no.toString(16));
			while (hashtext.length() < 32) {
				hashtext = hashtext.append("0" + hashtext);
			}
			return hashtext.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

}
