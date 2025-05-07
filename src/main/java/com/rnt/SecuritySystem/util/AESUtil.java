package com.rnt.SecuritySystem.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {

	private static final String ALGORITHM = "AES/ECB/PKCS5Padding";

	public static String decrypt(String encryptedText, String key) throws Exception {
		byte[] decodedKey = key.getBytes("UTF-8");
		SecretKeySpec secretKey = new SecretKeySpec(decodedKey, "AES");
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decodedText = Base64.getDecoder().decode(encryptedText);
		byte[] decryptedText = cipher.doFinal(decodedText);
		return new String(decryptedText, "UTF-8");
	}

	public static String encrypt(String plaintext, String key) throws Exception {
		byte[] decodedKey = key.getBytes("UTF-8");
		SecretKeySpec secretKey = new SecretKeySpec(decodedKey, "AES");
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedText = cipher.doFinal(plaintext.getBytes("UTF-8"));
		return Base64.getEncoder().encodeToString(encryptedText);
	}
}
