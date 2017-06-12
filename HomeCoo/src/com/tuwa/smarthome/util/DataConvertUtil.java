package com.tuwa.smarthome.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

import com.tuwa.smarthome.entity.Device;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.entity.Space;

public class DataConvertUtil {
	/**
	 * 将 int 类型数据转成二进制的字符串，不足 int 类型位数时在前面添“0”以凑足位数
	 * 
	 * @param num
	 * @return
	 */
	public static String toFullBinaryString(int num) {
		char[] chs = new char[Integer.SIZE];
		for (int i = 0; i < Integer.SIZE; i++) {
			chs[Integer.SIZE - 1 - i] = (char) (((num >> i) & 1) + '0');
		}
		return new String(chs);
	}

	/**
	 * 将byte[2]转换成short，长度表示为00 01
	 * 
	 * @param b
	 * @return
	 */
	public static short byte2Short(byte[] b) {
		return (short) (((b[0] & 0xff) << 8) | (b[1] & 0xff));
	}

	/**
	 * 将byte[2]转换成short,01 00
	 * 
	 * @param b
	 * @return
	 */
	public static short byteLH2Short(byte[] b) {
		return (short) (((b[1] & 0xff) << 8) | (b[0] & 0xff));
	}

	/**
	 * 基于位移的 32位int 转化成byte[]
	 * 
	 * @param byte[] bytes
	 * @return int number
	 */

	public static byte[] intToByte(int number) {
		byte[] abyte = new byte[4];
		// "&" 与（AND），对两个整型操作数中对应位执行布尔代数，两个位都为1时输出1，否则0。
		abyte[0] = (byte) (0xff & number);
		// ">>"右移位，若为正数则高位补0，若为负数则高位补1
		abyte[1] = (byte) ((0xff00 & number) >> 8);
		abyte[2] = (byte) ((0xff0000 & number) >> 16);
		abyte[3] = (byte) ((0xff000000 & number) >> 24);
		return abyte;
	}

	/**
	 * 基于位移的 byte[]转化成32位int
	 * 
	 * @param byte[] bytes
	 * @return int number
	 */
	public static int bytesToInt(byte[] bytes) {
		int number = bytes[0] & 0xFF;
		// "|="按位或赋值。
		number |= ((bytes[1] << 8) & 0xFF00);
		number |= ((bytes[2] << 16) & 0xFF0000);
		number |= ((bytes[3] << 24) & 0xFF000000);
		return number;
	}

	/**
	 * inputStream.read(要复制到得字节数组,起始位置下标,要复制的长度)
	 * 该方法读取后input的下标会自动的后移，下次读取的时候还是从上次读取后移动到的下标开始读取 所以每次读取后就不需要在制定起始的下标了
	 */
	public static byte[] streamToBytes(InputStream inputStream, int len) {

		byte[] bytes = new byte[len];
		try {
			inputStream.read(bytes, 0, len);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bytes;
	}

	/**
	 * 从offset处开始读数据
	 * 
	 * @param inputStream
	 * @param offset
	 * @param len
	 * @return
	 */
	public static byte[] streamToBytesOff(InputStream inputStream, int len) {

		byte[] bytes = new byte[len];
		try {
			inputStream.read(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bytes;
	}

	/**
	 * 接收的byte数组以16进制的形式打印到控制台
	 * 
	 * @param hint
	 *            String
	 * @param b
	 *            byte[]
	 * @return void
	 */
	public static void rprintHexString(byte[] b) {
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			System.out.print(hex + " ");
		}
		System.out.println("接收到报文的数据");
	}

	/**
	 * 发送的byte数组以16进制的形式打印到控制台
	 * 
	 * @param hint
	 *            String
	 * @param b
	 *            byte[]
	 * @return void
	 */
	public static void tprintHexString(byte[] b) {
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			System.out.print(hex + " ");
		}
		System.out.println("发送的数据");
	}

	/**
	 * 将16进制字节数组直接以string显示,字母小写
	 * 
	 * @param b
	 * @return
	 */
//	public static String hexToString(byte[] b) {
//		String strhex = "";
//		for (int i = 0; i < b.length; i++) {
//			String hex = Integer.toHexString(b[i] & 0xFF);
//			if (hex.length() == 1) {
//				hex = '0' + hex;
//			}
//			strhex += hex;
//		}
//		return strhex;
//	}

	/**
	 * 将长度为2的byte数组转换为16位int
	 * 
	 * @param res
	 *            byte[]
	 * @return int
	 * */
	public static int byte2int2(byte[] res) {
		// res = InversionByte(res);
		// 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000
		// int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00); // | 表示安位或
		int targets = (res[1] & 0xff) | ((res[0] << 8) & 0xff00); // | 表示安位或
		return targets;
	}
	
	public static int byte2int(byte[] res) {
		// res = InversionByte(res);
		// 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000
		 res[0]=0x00;
//		 int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00); // | 表示安位或
		int targets = (res[1] & 0xff) | ((res[0] << 8) & 0xff00); // | 表示安位或
		return targets;
	}

	/**
	 * 通过byte数组取到short
	 * 
	 * @param b
	 * @param index
	 *            第几位开始取
	 * @return
	 */
	public static short getShort(byte[] b, int index) {
		return (short) (((b[index + 1] << 8) | b[index + 0] & 0xff));
	}

	/**
	 * String 转换成byte[]形式，注意这个不改变实际内容 “00 01 30 02” 转 00 01 30 02
	 */
	public static byte[] toByteArray(String hexString) {

		final byte[] byteArray = new byte[hexString.length() / 2];
		int k = 0;
		for (int i = 0; i < byteArray.length; i++) {
			// 因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
			byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
			byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
			byteArray[i] = (byte) (high << 4 | low);
			k += 2;
		}
		return byteArray;
	}

	/**
	 * 16进制byte[]转String类型,字母转换为大写
	 */
	public static String toHexUpString(byte[] byteArray) {
		if (byteArray == null || byteArray.length < 1)
			throw new IllegalArgumentException(
					"this byteArray must not be null or empty");
		final StringBuilder hexString = new StringBuilder();
		for (int i = 0; i < byteArray.length; i++) {
			if ((byteArray[i] & 0xff) < 0x10)// 0~F前面不零
				hexString.append("0");
			hexString.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return hexString.toString();
	}
	
	/**
	 * 16进制byte[]转String类型,字母转换为小写
	 */
	public static String toHexDownString(byte[] byteArray) {
		if (byteArray == null || byteArray.length < 1)
			throw new IllegalArgumentException(
					"this byteArray must not be null or empty");
		final StringBuilder hexString = new StringBuilder();
		for (int i = 0; i < byteArray.length; i++) {
			if ((byteArray[i] & 0xff) < 0x10)// 0~F前面不零
				hexString.append("0");
			hexString.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return hexString.toString();
	}
	
	

	// 高位在前（dataLen定制）
	public static byte[] shortToByteArray2(short s) {
		byte[] targets = new byte[2];
		int offset1 = (targets.length - 1 - 1) * 8;
		int offset = (targets.length - 1 - 0) * 8;
		targets[0] = (byte) ((s >>> offset1) & 0xff);
		targets[1] = (byte) ((s >>> offset) & 0xff);
		return targets;
	}

	/**
	 * 将short转成byte[2]
	 * 
	 * @param a
	 * @return
	 */
	public static byte[] short2Byte(short a) {
		byte[] b = new byte[2];

		b[0] = (byte) (a >> 8);
		b[1] = (byte) (a);

		return b;
	}

	/**
	 * MD5加密算法
	 * 
	 * @param str
	 * @return
	 */
	public static String convertByMD5(String str) {
		MessageDigest instance;
		String strmd5 = "";
		byte[] digest = null;
		try {
			instance = MessageDigest.getInstance("MD5");
			digest = instance.digest(str.getBytes());
			
			for (int i = 0; i < digest.length; i++) {
				String hex = Integer.toHexString(digest[i] & 0xFF);
				strmd5+=hex;
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strmd5;
	}
	
	
	/**
     * <把字符串转换成字节数组然后在封装成字符串>
     * <功能详细描述>
     * @param chinese
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String chineseToString(String chinese)
    {
        if (chinese.isEmpty())
        {
            return "";
        }
        else
        {
            // 定义StringBuffer
            StringBuffer sb = new StringBuffer();
            
            // 把传进来的字符串转换成字节数组
            byte[] b = chinese.getBytes();
            
            byte[] temp = null;
            
            // 遍历字节数组，把字节数组转换成字符串
            for (int i = 0; i < b.length; i++)
            {
                temp = new byte[4];
                temp[0] = b[i];
                temp[1] = 0;
                temp[2] = 0;
                temp[3] = 0;
                sb.append(lBytesToInt(temp));
                if (i < b.length - 1)
                {
                    sb.append("@");
                }
            }
            
            return sb.toString();
        }
    }
    
    /**
     * <把字节数组封装成的字符串转换成原来的字符串>
     * <功能详细描述>
     * @param stc
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String stringToChinese(String stc)
    {
        // 如果传递的字符串为空则直接返回空
        if (stc.isEmpty())
        {
            return "";
        }
        else
        {
            // 分割字符串
            String[] s = stc.split("@");
            if (s.length > 0)
            {
                // 循环构造BYTE数组
                byte[] b = new byte[s.length];
                for (int i = 0; i < s.length; i++)
                {
                    b[i] = (byte)Integer.parseInt(s[i]);
                }
                
                // 根据BYTE数组构造字符串
                return new String(b);
            }
            else
            {
                return "";
            }
        }
    }
    
    /**
     * 将低字节数组转换为int
     * @param b byte[]
     * @return int
     */
    public static int lBytesToInt(byte[] b)
    {
        int s = 0;
        for (int i = 0; i < 3; i++)
        {
            if (b[3 - i] >= 0)
            {
                s = s + b[3 - i];
            }
            else
            {
                s = s + 256 + b[3 - i];
            }
            s = s * 256;
        }
        if (b[0] >= 0)
        {
            s = s + b[0];
        }
        else
        {
            s = s + 256 + b[0];
        }
        return s;
    }
    
    
 
}
