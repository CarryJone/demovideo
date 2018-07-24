package com.example.mds_user.demovideo.gcm;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import ndk.NdkUtility;

/** AES對稱加密解密類  **/
public class AESHelper {
	/***
	 *  創建密鑰: key 長度 128 位元
	 ***/
	private static SecretKeySpec createKey() {
		byte[] data = null;
		String keyPassword = NdkUtility.getKey();
			
		StringBuffer sb = new StringBuffer(16);
		sb.append(keyPassword);

		try {
			data = sb.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new SecretKeySpec(data, "AES");
	}

	/** 加密位元組資料 **/
	public static byte[] encrypt(byte[] content) {
		String CipherMode = NdkUtility.getMode();
		try {
			SecretKeySpec key = createKey();
			Cipher cipher = Cipher.getInstance(CipherMode);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] result = cipher.doFinal(content);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 加密(結果為16進制字串) **/
	public static String encrypt(String content) {
		
		String result = "";
		if (content.equalsIgnoreCase("")) {
			// 空值不需要加密
		} else {	
			byte[] data = null;
			try {
				data = content.getBytes("UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
			data = encrypt(data);
			result = convbyte2hex(data);
		}
		return result;
	}

	/** 解密位元組陣列 **/
	public static byte[] decrypt(byte[] content) {
		try {
			String CipherMode = NdkUtility.getMode();
			SecretKeySpec key = createKey();
			Cipher cipher = Cipher.getInstance(CipherMode);
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] result = cipher.doFinal(content);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 解密16進制的字串為字串 **/
	public static String decrypt(String content) {
		String result = "";
		if (content.equalsIgnoreCase("")) {
			// 空值不需要解密
		} else {	
			byte[] data = null;
			try {
				data = convhex2byte(content);
			} catch (Exception e) {
				e.printStackTrace();
			}
			data = decrypt(data);
			if (data == null)
				return "";
			try {
				result = new String(data, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/** 位元組陣列轉成16進制字串 **/
	public static String convbyte2hex(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length * 2);
		String tmp = "";
		for (int n = 0; n < b.length; n++) {
			// 整數轉成十六進位表示
			tmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (tmp.length() == 1) {
				sb.append("0");
			}
			sb.append(tmp);
		}
		return sb.toString().toUpperCase();
	}

	/** 將hex字串轉換成位元組陣列 **/
	public static byte[] convhex2byte(String inputString) {
		if (inputString == null || inputString.length() < 2) {
			return new byte[0];
		}
		inputString = inputString.toLowerCase();
		int l = inputString.length() / 2;
		byte[] result = new byte[l];
		for (int i = 0; i < l; ++i) {
			String tmp = inputString.substring(2 * i, 2 * i + 2);
			result[i] = (byte) (Integer.parseInt(tmp, 16) & 0xFF);
		}
		return result;
	}
}