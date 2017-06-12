package com.tuwa.smarthome.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tuwa.smarthome.dao.SpaceDao;
import com.tuwa.smarthome.dao.ThemeDeviceDao;
import com.tuwa.smarthome.dao.VersionDao;
import com.tuwa.smarthome.entity.SocketPacket;
import com.tuwa.smarthome.entity.Version;
import com.tuwa.smarthome.global.SystemValue;

import android.R;
import android.os.Handler;
import android.test.AndroidTestCase;
import android.widget.ArrayAdapter;

public class JunitTest extends AndroidTestCase {
	public void checkVersion() {
		System.out.println("==单元测试===");

		Version version = new Version();
		version.setPhonenum("18679451786");
		version.setGatewayNo(SystemValue.gatewayid);
		version.setVersionType(SystemValue.VERSION_SPACE);

		boolean result = new VersionDao(null).isUseableVersion(version);
		System.out.println("==单元测试===" + result);
	}

	public void deleteTheme() {
		new ThemeDeviceDao(null)
				.deleteThemeDeviceAllByThemeNo("92881B5D3A449B65");
	}

	public void TestByteBuffer() {
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		byteBuffer.putInt(1024);
		byteBuffer.put("134".getBytes());
		byte[] bytes = byteBuffer.array();

		for (byte b : bytes) {
			System.out.println(b);
		}
	}
	
	
	public void convertChinese2Byte(){
//		String chinesestr="23study";
//		String str=DataConvertUtil.chineseToString(chinesestr);
//		System.out.println(chinesestr+"===转码==="+str);
//		
//		int nameLength=str.length();
//		System.out.println("===名称转码后长度==="+nameLength);
//		byte[] bNameLength = DataConvertUtil.intToByte(nameLength); 
//		System.out.println("===长度转字节==");
//		DataConvertUtil.rprintHexString(bNameLength);  //接收的数据打印
//		
//		byte[] bThemeName =str.getBytes();
//		System.out.println("===名称转字节==");
//		DataConvertUtil.rprintHexString(bThemeName);  //接收的数据打印
		
	    String str="313132403937403131344031313640313231";
	    String str1="3232394031353540313538403232394031373440313832";
	    
	    byte[] gwIdBytes = DataConvertUtil.toByteArray(str1);
	    
	    String strHexChine=new String(gwIdBytes);
		String sceneName=DataConvertUtil.stringToChinese(strHexChine);
	    
		String stringToChinese = DataConvertUtil.stringToChinese(str1);
		System.out.println("转换后"+stringToChinese);
		
//		byte[] bSpaceName=str.getBytes();
//		String strb=new String(bSpaceName);
//		
//		String chinese=DataConvertUtil.stringToChinese(strb);
//		System.out.println("===转换后的汉字为==="+chinese);
		
	}
	
	public void byte2int(){
//		byte[] head =new byte[]{0x05,0x19};
//		int i=DataConvertUtil.byte2int(head);
//		System.out.println("wendu"+i);
		
//		String gatewayNo="AADDAAD8";
//		byte[] bGateway=gatewayNo.getBytes();
//		String str=DataConvertUtil.toHexUpString(bGateway);
//		System.out.println("网关转码后为==="+str);
		
		byte[] bytepm25 = new byte[]{0x02,0x41};
		byte bpm25H=bytepm25[0];
		byte bpm25L=bytepm25[1];
		
		int ipm25h=bpm25H;
		int ipm25l=bpm25L;
		int pm25=ipm25h*10+ipm25l/10;
		
//		int temp = DataConvertUtil.byte2int(bytepm25);
		
		
//		System.out.println("=====junit====="+temp);
		
		
//		int pm25H=DataConvertUtil.lBytesToInt(bpm25H);
//		int pm25L=DataConvertUtil.lBytesToInt(bpm25L);
//		
//		int pm25=pm25H*10+pm25L/10;
//		String strtemp = Integer.toString(pm25);
//		System.out.println("底层反馈的pm25值为："+strtemp+"高:"+pm25H+"低:"+pm25L);
		
		
	}
	
	
	public void pm25(){
		byte[] bpm25 = new byte[]{0x03,0x04};
		byte bpm25H=bpm25[0];
		byte bpm25L=bpm25[1];
		
		int ipm25h=bpm25H;
		int ipm25l=bpm25L;
		int pm25=ipm25h*10+ipm25l/10;
		
		String strtemp = Integer.toString(pm25);
		System.out.println("底层反馈的pm25值为："+strtemp+"高:"+ipm25h+"低:"+ipm25l);
	}
	
	
	
//	public static int Length(String str)
//	{
//	    int len = 0;
//	    char[] arr = str.toCharArray();
//	    for (int i = 0; i < arr.length; i++)
//	    {
//	        //汉字
//	        if (Asc(arr[i].ToString) == -1)
//	            len += 2;
//	        else
//	            len += 1;
//	    }
//	 
//	    return len;
//	}
	
	public void threadDelay(){
		System.out.println("当前的时间为："+System.currentTimeMillis());
		new Handler().postDelayed(new Runnable(){    
		    public void run() {    
			   
		    	System.out.println("延迟3秒后的时间为："+System.currentTimeMillis());
		    }    
		 }, 3000);  
	}
	
	
	public void md5Test(){
		try {
			String mystr="123";
			String str32=MD5Security16.md5_16(mystr);
			System.out.println(mystr+"加密后的字符串为:"+str32);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
