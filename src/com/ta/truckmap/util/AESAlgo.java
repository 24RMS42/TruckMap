package com.ta.truckmap.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;
import android.util.Log;

public class AESAlgo
{

	public static String Encrypt(String input, String passphrase)
	{
		if (input.equalsIgnoreCase("") || passphrase.equalsIgnoreCase(""))
			return "";
		else
		{
			byte[] key, iv;

			byte[] passphrasedata = null;
			try
			{
				passphrasedata = passphrase.getBytes("UTF-8");
			}
			catch (UnsupportedEncodingException e1)
			{
				e1.printStackTrace();
			}
			byte[] currentHash = new byte[0];
			MessageDigest md = null;
			try
			{
				md = MessageDigest.getInstance("SHA-256");
			}
			catch (NoSuchAlgorithmException e)
			{
				e.printStackTrace();
			}
			currentHash = md.digest(passphrasedata);

			iv = new byte[16];
			return Base64.encodeToString(EncryptStringToBytes(input, currentHash, iv), Base64.NO_WRAP);
		}
	}

	public static String Decrypt(String inputBase64, String passphrase)
	{
		if (inputBase64.equalsIgnoreCase("") || passphrase.equalsIgnoreCase(""))
			return "";
		else
		{

			byte[] key, iv = new byte[0];
			byte[] base64data = Base64.decode(inputBase64, Base64.NO_WRAP);
			byte[] passphrasedata = null;
			try
			{
				passphrasedata = passphrase.getBytes("UTF-8");
			}
			catch (UnsupportedEncodingException e1)
			{
				e1.printStackTrace();
			}
			byte[] currentHash = new byte[0];
			MessageDigest md = null;
			try
			{
				md = MessageDigest.getInstance("SHA-256");
			}
			catch (NoSuchAlgorithmException e)
			{
				e.printStackTrace();
			}
			currentHash = md.digest(passphrasedata);
			return DecryptStringFromBytes(base64data, currentHash, null);
		}
	}

	static byte[] EncryptStringToBytes(String plainText, byte[] Key, byte[] IV)
	{
		if (plainText == null || plainText.length() <= 0)
		{
			Log.e("error", "plain text empty");
		}
		if (Key == null || Key.length <= 0)
		{
			Log.e("error", "key is empty");
		}
		if (IV == null || IV.length <= 0)
		{
			Log.e("error", "IV key empty");
		}
		byte[] encrypted;

		try
		{
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKeySpec myKey = new SecretKeySpec(Key, "AES");
			IvParameterSpec IVKey = new IvParameterSpec(IV);
			cipher.init(Cipher.ENCRYPT_MODE, myKey, IVKey);

			encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
			return encrypted;
		}
		catch (InvalidKeyException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchPaddingException e)
		{
			e.printStackTrace();
		}
		catch (InvalidAlgorithmParameterException e)
		{
			e.printStackTrace();
		}
		catch (IllegalBlockSizeException e)
		{
			e.printStackTrace();
		}
		catch (BadPaddingException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	static String DecryptStringFromBytes(byte[] cipherText, byte[] Key, byte[] IV)
	{
		// Check arguments. 
		IV = new byte[16];
		if (cipherText == null || cipherText.length <= 0)
			Log.e("error", "cipherText empty");
		if (Key == null || Key.length <= 0)
			Log.e("error", "key empty");
		if (IV == null || IV.length <= 0)
			Log.e("error", "IV key empty");

		String plaintext = null;

		try
		{
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKeySpec myKey = new SecretKeySpec(Key, "AES");
			IvParameterSpec IVKey = new IvParameterSpec(IV);
			cipher.init(Cipher.DECRYPT_MODE, myKey, IVKey);
			byte[] plaintex = cipher.doFinal(cipherText);
			Log.e("plain text", new String(plaintex, "UTF-8"));
			return new String(plaintex, "UTF-8");
		}
		catch (InvalidKeyException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchPaddingException e)
		{
			e.printStackTrace();
		}
		catch (InvalidAlgorithmParameterException e)
		{
			e.printStackTrace();
		}
		catch (IllegalBlockSizeException e)
		{
			e.printStackTrace();
		}
		catch (BadPaddingException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return "";
	}
}
