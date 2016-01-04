/**
 *
 * Licensed Property to China UnionPay Co., Ltd.
 * 
 * (C) Copyright of China UnionPay Co., Ltd. 2010
 *     All Rights Reserved.
 *
 * 
 * Modification History:
 * =============================================================================
 *   Author         Date          Description
 *   ------------ ---------- ---------------------------------------------------
 *   xshu       2014-05-28     报文加密解密等操作的工具�?
 * =============================================================================
 */
package unionpayUtil.sdk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class SecureUtil {
	/**
	 * 算法常量�?MD5
	 */
	private static final String ALGORITHM_MD5 = "MD5";
	/**
	 * 算法常量�?SHA1
	 */
	private static final String ALGORITHM_SHA1 = "SHA-1";

	/**
	 * 算法常量：SHA1withRSA
	 */
	private static final String BC_PROV_ALGORITHM_SHA1RSA = "SHA1withRSA";

	/**
	 * md5计算.
	 * 
	 * @param datas
	 *            待计算的数据
	 * @return 计算结果
	 */
	public static byte[] md5(byte[] datas) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(ALGORITHM_MD5);
			md.reset();
			md.update(datas);
			return md.digest();
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.writeErrorLog("MD5计算失败", e);
			return null;
		}
	}

	/**
	 * sha1计算.
	 * 
	 * @param datas
	 *            待计算的数据
	 * @return 计算结果
	 */
	public static byte[] sha1(byte[] data) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(ALGORITHM_SHA1);
			md.reset();
			md.update(data);
			return md.digest();
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.writeErrorLog("SHA1计算失败", e);
			return null;
		}
	}

	/**
	 * md5计算后进�?6进制转换
	 * 
	 * @param datas
	 *            待计算的数据
	 * @param encoding
	 *            编码
	 * @return 计算结果
	 */
	public static byte[] md5X16(String datas, String encoding) {
		byte[] bytes = md5(datas, encoding);
		StringBuilder md5StrBuff = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			if (Integer.toHexString(0xFF & bytes[i]).length() == 1) {
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & bytes[i]));
			} else {
				md5StrBuff.append(Integer.toHexString(0xFF & bytes[i]));
			}
		}
		try {
			return md5StrBuff.toString().getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * sha1计算后进�?6进制转换
	 * 
	 * @param data
	 *            待计算的数据
	 * @param encoding
	 *            编码
	 * @return 计算结果
	 */
	public static byte[] sha1X16(String data, String encoding) {
		byte[] bytes = sha1(data, encoding);
		StringBuilder sha1StrBuff = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			if (Integer.toHexString(0xFF & bytes[i]).length() == 1) {
				sha1StrBuff.append("0").append(
						Integer.toHexString(0xFF & bytes[i]));
			} else {
				sha1StrBuff.append(Integer.toHexString(0xFF & bytes[i]));
			}
		}
		try {
			return sha1StrBuff.toString().getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * md5计算
	 * 
	 * @param datas
	 *            待计算的数据
	 * @param encoding
	 *            字符集编�?
	 * @return
	 */
	public static byte[] md5(String datas, String encoding) {
		try {
			return md5(datas.getBytes(encoding));
		} catch (UnsupportedEncodingException e) {
			LogUtil.writeErrorLog("MD5计算失败", e);
			return null;
		}
	}

	/**
	 * sha1计算
	 * 
	 * @param datas
	 *            待计算的数据
	 * @param encoding
	 *            字符集编�?
	 * @return
	 */
	public static byte[] sha1(String datas, String encoding) {
		try {
			return sha1(datas.getBytes(encoding));
		} catch (UnsupportedEncodingException e) {
			LogUtil.writeErrorLog("SHA1计算失败", e);
			return null;
		}
	}

	/**
	 * 软签�?
	 * 
	 * @param privateKey
	 *            私钥
	 * @param data
	 *            待签名数�?
	 * @param signMethod
	 *            签名方法
	 * @return 结果
	 * @throws Exception
	 */
	public static byte[] signBySoft(PrivateKey privateKey, byte[] data)
			throws Exception {
		byte[] result = null;
		Signature st = Signature.getInstance(BC_PROV_ALGORITHM_SHA1RSA);
		st.initSign(privateKey);
		st.update(data);
		result = st.sign();
		return result;
	}

	/**
	 * 软验证签�?
	 * 
	 * @param publicKey
	 *            公钥
	 * @param signData
	 *            签名数据
	 * @param srcData
	 *            摘要
	 * @param validateMethod
	 *            签名方法.
	 * @return
	 * @throws Exception
	 */
	public static boolean validateSignBySoft(PublicKey publicKey,
			byte[] signData, byte[] srcData) throws Exception {
		Signature st = Signature.getInstance(BC_PROV_ALGORITHM_SHA1RSA);
		st.initVerify(publicKey);
		st.update(srcData);
		return st.verify(signData);
	}

	/**
	 * 解压�?
	 * 
	 * @param inputByte
	 *            byte[]数组类型的数�?
	 * @return 解压缩后的数�?
	 * @throws IOException
	 */
	public static byte[] inflater(final byte[] inputByte) throws IOException {
		int compressedDataLength = 0;
		Inflater compresser = new Inflater(false);
		compresser.setInput(inputByte, 0, inputByte.length);
		ByteArrayOutputStream o = new ByteArrayOutputStream(inputByte.length);
		byte[] result = new byte[1024];
		try {
			while (!compresser.finished()) {
				compressedDataLength = compresser.inflate(result);
				if (compressedDataLength == 0) {
					break;
				}
				o.write(result, 0, compressedDataLength);
			}
		} catch (Exception ex) {
			System.err.println("Data format error!\n");
			ex.printStackTrace();
		} finally {
			o.close();
		}
		compresser.end();
		return o.toByteArray();
	}

	/**
	 * 压缩.
	 * 
	 * @param inputByte
	 *            �?��解压缩的byte[]数组
	 * @return 压缩后的数据
	 * @throws IOException
	 */
	public static byte[] deflater(final byte[] inputByte) throws IOException {
		int compressedDataLength = 0;
		Deflater compresser = new Deflater();
		compresser.setInput(inputByte);
		compresser.finish();
		ByteArrayOutputStream o = new ByteArrayOutputStream(inputByte.length);
		byte[] result = new byte[1024];
		try {
			while (!compresser.finished()) {
				compressedDataLength = compresser.deflate(result);
				o.write(result, 0, compressedDataLength);
			}
		} finally {
			o.close();
		}
		compresser.end();
		return o.toByteArray();
	}

	/**
	 * 密码加密,进行base64加密
	 * 
	 * @param pin
	 *            密码
	 * @param card
	 *            卡号
	 * @param encoding
	 *            字符编码
	 * @param key
	 *            公钥
	 * @return 转PIN结果
	 */
	public static String EncryptPin(String pin, String card, String encoding,
			PublicKey key) {
		/** 生成PIN Block **/
		byte[] pinBlock = pin2PinBlockWithCardNO(pin, card);
		/** 使用公钥对密码加�?**/
		byte[] data = null;
		try {
			data = encryptedPin(key, pinBlock);
			return new String(SecureUtil.base64Encode(data), encoding);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 对数据�?过公钥进行加密，并进行base64计算
	 * 
	 * @param dataString
	 *            待处理数�?
	 * @param encoding
	 *            字符编码
	 * @param key
	 *            公钥
	 * @return
	 */
	public static String EncryptData(String dataString, String encoding,
			PublicKey key) {
		/** 使用公钥对密码加�?**/
		byte[] data = null;
		try {
			data = encryptedPin(key, dataString.getBytes(encoding));
			return new String(SecureUtil.base64Encode(data), encoding);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 通过私钥解密
	 * 
	 * @param dataString
	 *            base64过的数据
	 * @param encoding
	 *            编码
	 * @param key
	 *            私钥
	 * @return 解密后的数据
	 */
	public static String DecryptedData(String dataString, String encoding,
			PrivateKey key) {
		byte[] data = null;
		try {
			data = decryptedPin(key, dataString.getBytes(encoding));
			return new String(data, encoding);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * BASE64解码
	 * 
	 * @param inputByte
	 *            待解码数�?
	 * @return 解码后的数据
	 * @throws IOException
	 */
	public static byte[] base64Decode(byte[] inputByte) throws IOException {
		return Base64.decodeBase64(inputByte);
	}

	/**
	 * BASE64编码
	 * 
	 * @param inputByte
	 *            待编码数�?
	 * @return 解码后的数据
	 * @throws IOException
	 */
	public static byte[] base64Encode(byte[] inputByte) throws IOException {
		return Base64.encodeBase64(inputByte);
	}

	/**
	 * 将字符串转换为byte数组
	 * 
	 * @param str
	 *            待转换的字符�?
	 * @return 转换结果
	 */
	public byte[] Str2Hex(String str) {
		char[] ch = str.toCharArray();
		byte[] b = new byte[ch.length / 2];
		for (int i = 0; i < ch.length; i++) {
			if (ch[i] == 0) {
				break;
			}
			if (ch[i] >= '0' && ch[i] <= '9') {
				ch[i] = (char) (ch[i] - '0');
			} else if (ch[i] >= 'A' && ch[i] <= 'F') {
				ch[i] = (char) (ch[i] - 'A' + 10);
			}
		}
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) (((ch[2 * i] << 4) & 0xf0) + (ch[2 * i + 1] & 0x0f));
		}
		return b;
	}

	/**
	 * 将byte数组转换为可见的字符�?
	 * 
	 * @param b
	 *            待转换的byte数组
	 * @return 转换后的字符�?
	 */
	public static String Hex2Str(byte[] b) {
		StringBuffer d = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			char hi = Character.forDigit((b[i] >> 4) & 0x0F, 16);
			char lo = Character.forDigit(b[i] & 0x0F, 16);
			d.append(Character.toUpperCase(hi));
			d.append(Character.toUpperCase(lo));
		}
		return d.toString();
	}

	public static String ByteToHex(byte[] bytes) {
		StringBuffer sha1StrBuff = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			if (Integer.toHexString(0xFF & bytes[i]).length() == 1) {
				sha1StrBuff.append("0").append(
						Integer.toHexString(0xFF & bytes[i]));
			} else {
				sha1StrBuff.append(Integer.toHexString(0xFF & bytes[i]));
			}
		}
		return sha1StrBuff.toString();
	}

	/**
	 * 将byte数组转换为可见的字符�?
	 * 
	 * @param b
	 *            待转换的byte数组
	 * @param len
	 *            转换长度
	 * @return
	 */
	public static String Hex2Str(byte[] b, int len) {
		String str = "";
		char[] ch = new char[len * 2];

		for (int i = 0; i < len; i++) {
			if ((((b[i] >> 4) & 0x0f) < 0x0a) && (((b[i] >> 4) & 0x0f) >= 0x0)) {
				ch[i * 2] = (char) (((b[i] >> 4) & 0x0f) + '0');
			} else {
				ch[i * 2] = (char) (((b[i] >> 4) & 0x0f) + 'A' - 10);
			}

			if ((((b[i]) & 0x0f) < 0x0a) && (((b[i]) & 0x0f) >= 0x0)) {
				ch[i * 2 + 1] = (char) (((b[i]) & 0x0f) + '0');
			} else {
				ch[i * 2 + 1] = (char) (((b[i]) & 0x0f) + 'A' - 10);
			}

		}
		str = new String(ch);
		return str;
	}

	/**
	 * 将byte数组转换为可见的大写字符�?
	 * 
	 * @param b
	 *            待转换的byte数组
	 * @return 转换后的结果
	 */
	public String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
			if (n < b.length - 1) {
				hs = hs + ":";
			}
		}
		return hs.toUpperCase();
	}

	/**
	 * 计算MAC
	 * 
	 * @param inputByte
	 *            待计算数�?
	 * @param inputkey
	 *            密钥
	 * @return 计算出的MAC�?
	 * @throws Exception
	 */
	public String genmac(byte[] inputByte, byte[] inputkey) throws Exception {
		try {
			Mac mac = Mac.getInstance("HmacMD5");
			SecretKey key = new SecretKeySpec(inputkey, "DES");
			mac.init(key);

			byte[] macCode = mac.doFinal(inputByte);
			String strMac = this.byte2hex(macCode);
			return strMac;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	/**
	 * MAC校验
	 * 
	 * @param inputByte
	 *            待计算的数据
	 * @param inputkey
	 *            密钥
	 * @param inputmac
	 *            比较MAC
	 * @return 校验结果
	 * @throws Exception
	 */
	public boolean checkmac(byte[] inputByte, byte[] inputkey, String inputmac)
			throws Exception {
		try {
			Mac mac = Mac.getInstance("HmacMD5");
			SecretKey key = new SecretKeySpec(inputkey, "DES");
			mac.init(key);

			byte[] macCode = mac.doFinal(inputByte);
			String strMacCode = this.byte2hex(macCode);

			if (strMacCode.equals(inputmac)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * 字符串填�?
	 * 
	 * @param string
	 *            源串
	 * @param filler
	 *            填充�?
	 * @param totalLength
	 *            填充总长�?
	 * @param atEnd
	 *            头尾填充表�?,true - 尾部填充;false - 头部填充
	 * @return
	 */
	public static String fillString(String string, char filler,
			int totalLength, boolean atEnd) {
		byte[] tempbyte = string.getBytes();
		int currentLength = tempbyte.length;
		int delta = totalLength - currentLength;

		for (int i = 0; i < delta; i++) {
			if (atEnd) {
				string += filler;
			} else {
				string = filler + string;
			}
		}
		return string;

	}

	/**
	 * 使用网关公钥对持卡人密码进行加密，并返回byte[]类型
	 * 
	 * @param publicKey
	 * @param plainPin
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptedPin(PublicKey publicKey, byte[] plainPin)
			throws Exception {
		try {
			// y
			// Cipher cipher = Cipher.getInstance("DES",
			// new org.bouncycastle.jce.provider.BouncyCastleProvider());

			// 本土�?
			Cipher cipher = CliperInstance.getInstance();
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			int blockSize = cipher.getBlockSize();
			int outputSize = cipher.getOutputSize(plainPin.length);
			int leavedSize = plainPin.length % blockSize;
			int blocksSize = leavedSize != 0 ? plainPin.length / blockSize + 1
					: plainPin.length / blockSize;
			byte[] raw = new byte[outputSize * blocksSize];
			int i = 0;
			while (plainPin.length - i * blockSize > 0) {
				if (plainPin.length - i * blockSize > blockSize) {
					cipher.doFinal(plainPin, i * blockSize, blockSize, raw, i
							* outputSize);
				} else {
					cipher.doFinal(plainPin, i * blockSize, plainPin.length - i
							* blockSize, raw, i * outputSize);
				}
				i++;
			}
			return raw;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * 
	 * @param publicKey
	 * @param plainData
	 * @return
	 * @throws Exception
	 */
	public byte[] encryptedData(PublicKey publicKey, byte[] plainData)
			throws Exception {
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding",
					new org.bouncycastle.jce.provider.BouncyCastleProvider());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			int blockSize = cipher.getBlockSize();
			int outputSize = cipher.getOutputSize(plainData.length);
			int leavedSize = plainData.length % blockSize;
			int blocksSize = leavedSize != 0 ? plainData.length / blockSize + 1
					: plainData.length / blockSize;
			byte[] raw = new byte[outputSize * blocksSize];
			int i = 0;
			while (plainData.length - i * blockSize > 0) {
				if (plainData.length - i * blockSize > blockSize) {
					cipher.doFinal(plainData, i * blockSize, blockSize, raw, i
							* outputSize);
				} else {
					cipher.doFinal(plainData, i * blockSize, plainData.length
							- i * blockSize, raw, i * outputSize);
				}
				i++;
			}
			return raw;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * 
	 * @param privateKey
	 * @param cryptPin
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptedPin(PrivateKey privateKey, byte[] cryptPin)
			throws Exception {

		try {
			/** 生成PIN Block **/
			byte[] pinBlock = SecureUtil.base64Decode(cryptPin);
			// 本土�?
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding",
					new org.bouncycastle.jce.provider.BouncyCastleProvider());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			int blockSize = cipher.getBlockSize();
			int outputSize = cipher.getOutputSize(pinBlock.length);
			int leavedSize = pinBlock.length % blockSize;
			int blocksSize = leavedSize != 0 ? pinBlock.length / blockSize + 1
					: pinBlock.length / blockSize;
			byte[] pinData = new byte[outputSize * blocksSize];
			int i = 0;
			while (pinBlock.length - i * blockSize > 0) {
				if (pinBlock.length - i * blockSize > blockSize) {
					cipher.doFinal(pinBlock, i * blockSize, blockSize, pinData,
							i * outputSize);
				} else {
					cipher.doFinal(pinBlock, i * blockSize, pinBlock.length - i
							* blockSize, pinData, i * outputSize);
				}
				i++;
			}
			return pinData;
		} catch (Exception e) {
			LogUtil.writeErrorLog("解密失败", e);
		}
		return null;
	}

	/**
	 * 
	 * @param aPin
	 * @return
	 */
	private static byte[] pin2PinBlock(String aPin) {
		int tTemp = 1;
		int tPinLen = aPin.length();

		byte[] tByte = new byte[8];
		try {
			/*******************************************************************
			 * if (tPinLen > 9) { tByte[0] = (byte) Integer.parseInt(new
			 * Integer(tPinLen) .toString(), 16); } else { tByte[0] = (byte)
			 * Integer.parseInt(new Integer(tPinLen) .toString(), 10); }
			 ******************************************************************/
//			tByte[0] = (byte) Integer.parseInt(new Integer(tPinLen).toString(),
//					10);
			tByte[0] = (byte) Integer.parseInt(Integer.toString(tPinLen), 10);
			if (tPinLen % 2 == 0) {
				for (int i = 0; i < tPinLen;) {
					String a = aPin.substring(i, i + 2);
					tByte[tTemp] = (byte) Integer.parseInt(a, 16);
					if (i == (tPinLen - 2)) {
						if (tTemp < 7) {
							for (int x = (tTemp + 1); x < 8; x++) {
								tByte[x] = (byte) 0xff;
							}
						}
					}
					tTemp++;
					i = i + 2;
				}
			} else {
				for (int i = 0; i < tPinLen - 1;) {
					String a;
					a = aPin.substring(i, i + 2);
					tByte[tTemp] = (byte) Integer.parseInt(a, 16);
					if (i == (tPinLen - 3)) {
						String b = aPin.substring(tPinLen - 1) + "F";
						tByte[tTemp + 1] = (byte) Integer.parseInt(b, 16);
						if ((tTemp + 1) < 7) {
							for (int x = (tTemp + 2); x < 8; x++) {
								tByte[x] = (byte) 0xff;
							}
						}
					}
					tTemp++;
					i = i + 2;
				}
			}
		} catch (Exception e) {
		}

		return tByte;
	}

	/**
	 * 
	 * @param aPan
	 * @return
	 */
	private static byte[] formatPan(String aPan) {
		int tPanLen = aPan.length();
		byte[] tByte = new byte[8];
		;
		int temp = tPanLen - 13;
		try {
			tByte[0] = (byte) 0x00;
			tByte[1] = (byte) 0x00;
			for (int i = 2; i < 8; i++) {
				String a = aPan.substring(temp, temp + 2);
				tByte[i] = (byte) Integer.parseInt(a, 16);
				temp = temp + 2;
			}
		} catch (Exception e) {
		}
		return tByte;
	}

	/**
	 * 
	 * @param aPin
	 * @param aCardNO
	 * @return
	 */
	public static byte[] pin2PinBlockWithCardNO(String aPin, String aCardNO) {
		byte[] tPinByte = pin2PinBlock(aPin);
		if (aCardNO.length() == 11) {
			aCardNO = "00" + aCardNO;
		} else if (aCardNO.length() == 12) {
			aCardNO = "0" + aCardNO;
		}
		byte[] tPanByte = formatPan(aCardNO);
		byte[] tByte = new byte[8];
		for (int i = 0; i < 8; i++) {
			tByte[i] = (byte) (tPinByte[i] ^ tPanByte[i]);
		}
		return tByte;
	}

	/**
	 * 
	 * @param aBytesText
	 * @param aBlockSize
	 * @return
	 */
	private static byte[] addPKCS1Padding(byte[] aBytesText, int aBlockSize) {
		if (aBytesText.length > (aBlockSize - 3)) {
			return null;
		}
		SecureRandom tRandom = new SecureRandom();
		byte[] tAfterPaddingBytes = new byte[aBlockSize];
		tRandom.nextBytes(tAfterPaddingBytes);
		tAfterPaddingBytes[0] = 0x00;
		tAfterPaddingBytes[1] = 0x02;
		int i = 2;
		for (; i < aBlockSize - 1 - aBytesText.length; i++) {
			if (tAfterPaddingBytes[i] == 0x00) {
				tAfterPaddingBytes[i] = (byte) tRandom.nextInt();
			}
		}
		tAfterPaddingBytes[i] = 0x00;
		System.arraycopy(aBytesText, 0, tAfterPaddingBytes, (i + 1),
				aBytesText.length);

		return tAfterPaddingBytes;
	}

	/**
	 * 
	 * @param tPIN
	 * @param iPan
	 * @param publicKey
	 * @return
	 */
	public String assymEncrypt(String tPIN, String iPan, RSAPublicKey publicKey) {

		System.out.println("SampleHashMap::assymEncrypt([" + tPIN + "])");
		System.out.println("SampleHashMap::assymEncrypt(PIN =[" + tPIN + "])");

		try {
			int tKeyLength = 1024;
			int tBlockSize = tKeyLength / 8;

			byte[] tTemp = null;

			tTemp = SecureUtil.pin2PinBlockWithCardNO(tPIN, iPan);
			tTemp = addPKCS1Padding(tTemp, tBlockSize);

			BigInteger tPlainText = new BigInteger(tTemp);
			BigInteger tCipherText = tPlainText.modPow(publicKey
					.getPublicExponent(), publicKey.getModulus());

			byte[] tCipherBytes = tCipherText.toByteArray();
			int tCipherLength = tCipherBytes.length;
			if (tCipherLength > tBlockSize) {
				byte[] tTempBytes = new byte[tBlockSize];
				System.arraycopy(tCipherBytes, tCipherLength - tBlockSize,
						tTempBytes, 0, tBlockSize);
				tCipherBytes = tTempBytes;
			} else if (tCipherLength < tBlockSize) {
				byte[] tTempBytes = new byte[tBlockSize];
				for (int i = 0; i < tBlockSize - tCipherLength; i++) {
					tTempBytes[i] = 0x00;
				}
				System.arraycopy(tCipherBytes, 0, tTempBytes, tBlockSize
						- tCipherLength, tCipherLength);
				tCipherBytes = tTempBytes;
			}
			String tEncryptPIN = new String(SecureUtil
					.base64Encode(tCipherBytes));

			System.out.println("SampleHashMap::assymEncrypt(EncryptCardNo =["
					+ tEncryptPIN + "])");

			return tEncryptPIN;
		} catch (Exception e) {
			e.printStackTrace(System.out);
			return tPIN;
		} catch (Error e) {
			e.printStackTrace(System.out);
			return tPIN;
		}
	}

	/**
	 * �?6进制对照的方式打印byte数组
	 * 
	 * @param inBytes
	 * @return
	 */
	public static String trace(byte[] inBytes) {
		int i, j = 0;
		byte[] temp = new byte[76];
		bytesSet(temp, ' ');
		StringBuffer strc = new StringBuffer("");
		strc
				.append("----------------------------------------------------------------------------"
						+ "\n");
		for (i = 0; i < inBytes.length; i++) {
			if (j == 0) {
				System.arraycopy(String.format("%03d: ", i).getBytes(), 0,
						temp, 0, 5);
				System.arraycopy(String.format(":%03d", i + 15).getBytes(), 0,
						temp, 72, 4);
			}
			System.arraycopy(String.format("%02X ", inBytes[i]).getBytes(), 0,
					temp, j * 3 + 5 + (j > 7 ? 1 : 0), 3);
			if (inBytes[i] == 0x00) {
				temp[j + 55 + ((j > 7 ? 1 : 0))] = '.';
			} else {
				temp[j + 55 + ((j > 7 ? 1 : 0))] = inBytes[i];
			}
			j++;
			if (j == 16) {
				strc.append(new String(temp)).append("\n");
				bytesSet(temp, ' ');
				j = 0;
			}
		}
		if (j != 0) {
			strc.append(new String(temp)).append("\n");
			bytesSet(temp, ' ');
		}
		strc
				.append("----------------------------------------------------------------------------"
						+ "\n");
		return strc.toString();
	}

	/**
	 * 
	 * @param inBytes
	 * @param fill
	 */
	private static void bytesSet(byte[] inBytes, char fill) {
		if (inBytes.length == 0) {
			return;
		}
		for (int i = 0; i < inBytes.length; i++) {
			inBytes[i] = (byte) fill;
		}
	}
}
