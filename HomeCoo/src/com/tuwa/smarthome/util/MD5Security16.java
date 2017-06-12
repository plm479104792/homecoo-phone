package com.tuwa.smarthome.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * @类名    MD5Security16
 * @创建者   ppa
 * @创建时间 2016-4-14
 * @描述   16位的MD5加密算法
 */
public class MD5Security16 {

		private final static char[] hexDigits = { '0', '1', '2', '3', '4', '5',
				'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		private static String bytesToHex(byte[] bytes) {
			StringBuffer sb = new StringBuffer();
			int t;
			for (int i = 0; i < 16; i++) {
				t = bytes[i];
				if (t < 0)
					t += 256;
				sb.append(hexDigits[(t >>> 4)]);
				sb.append(hexDigits[(t % 16)]);
			}
			return sb.toString().toLowerCase();
		}
		
		public static String md5_161(String input) throws Exception {
			return code(input, 16);
		}
		
		public static String code(String input, int bit) throws Exception {
			try {
				MessageDigest md = MessageDigest.getInstance(System.getProperty(
						"MD5.algorithm", "MD5"));
				if (bit == 16)
					return bytesToHex(md.digest(input.getBytes("utf-8")))
							.substring(8, 24);
				return bytesToHex(md.digest(input.getBytes("utf-8")));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				throw new Exception("Could not found MD5 algorithm.", e);
			}
		}
		
		public static String md5_3(String b) throws Exception{
			MessageDigest md = MessageDigest.getInstance(System.getProperty(
					"MD5.algorithm", "MD5"));
			byte[] a = md.digest(b.getBytes());
			a = md.digest(a);
			a = md.digest(a);
			
			return bytesToHex(a);
		}
		
		
		/**
		 * MD5加密输出32位后截取前16位
		 * @param val
		 * @return
		 * @throws NoSuchAlgorithmException
		 */
	    
	    public static String md5_16(String info) throws NoSuchAlgorithmException{  
	        MessageDigest md5 = MessageDigest.getInstance("MD5");  
	        byte[] srcBytes = info.getBytes();  
	        //使用srcBytes更新摘要  
	        md5.update(srcBytes);  
	        //完成哈希计算，得到result  
	        byte[] resultBytes = md5.digest();  
	        return hexString(resultBytes);  
	    } 
	    
		 //byte字节转换成16进制的字符串MD5Utils.hexString  
		 public static String hexString(byte[] bytes){  
	       StringBuffer hexValue = new StringBuffer();  
	       for (int i = 0; i < bytes.length; i++) {  
	           int val = ((int) bytes[i]) & 0xff;  
	           if (val < 16)  
	               hexValue.append("0");  
	           hexValue.append(Integer.toHexString(val));  
	       }  
	   	String str32=hexValue.toString();
			String str16=str32.substring(0,16);
	       return str16;  
	   }
		
		
	}

