package com.tuwa.smarthome.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.client.HttpResponseException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.tuwa.smarthome.entity.TranObject;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.TranObjectType;

import android.os.Handler;
import android.os.Message;


public class WebService {
 
	//外网服务器
	 public static String WEB_SERVER_URL = "http://120.26.220.55:8080/tuwa/webServices/devService";  //设备service杭州
	
	 public static String WEB_USER_SERVERURL = "http://120.26.220.55:8080/tuwa/webServices/userService";  //设备service杭州
		
	 public static String WEB_SCENESER_URL = "http://120.26.220.55:8080/tuwa/webServices/sceneService";  //设备service杭州
	 public static String WEB_MUSIC_URL = "http://120.26.220.55:8080/tuwa/webServices/musicService";
	 
	 
	 public  final static String WEB_NAMESPACE = "http://webservice.dev.controller.tuwa.com/";       //杭州服务器 
	 
	 
	 
     // 含有3个线程的线程池  
     private static final ExecutorService executorService = Executors  
            .newFixedThreadPool(3);  
	  
	 /** 
	     *  
	     * @param <T>
	 * @param url 
	     *            WebService服务器地址 
	     * @param methodName 
	     *            WebService的调用方法名 
	     * @param properties 
	     *            WebService的参数 
	     * @param webServiceCallBack 
	     *            回调接口 
	     */  
	    public static  void callWebService(String url,  String methodName,  
	            HashMap<String, Object> properties,  
	            final WebServiceCallBack webServiceCallBack) {  
	        // 创建HttpTransportSE对象，传递WebService服务器地址  
	        final HttpTransportSE httpTransportSE = new HttpTransportSE(url);  
	        // 创建SoapObject对象  
	        SoapObject soapObject = new SoapObject(WEB_NAMESPACE, methodName);  
	  
	        // SoapObject添加参数  
	        if (properties != null) {  
	            for (Iterator<Map.Entry<String, Object>> it = properties.entrySet()  
	                    .iterator(); it.hasNext();) {  
	                Map.Entry<String, Object> entry = it.next();  
	                soapObject.addProperty(entry.getKey(), entry.getValue());  
	            }  
	        }  
	  
	        // 实例化SoapSerializationEnvelope，传入WebService的SOAP协议的版本号  
	        final SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(  
	                SoapEnvelope.VER11);  
	        // 设置是否调用的是.Net开发的WebService  
//	        soapEnvelope.setOutputSoapObject(soapObject);  
//	        soapEnvelope.dotNet = true;  
	       
	        httpTransportSE.debug = true;  
	        soapEnvelope.bodyOut=soapObject;
	  
	        // 用于子线程与主线程通信的Handler  
	        final Handler mHandler = new Handler() {  
	  
	            @Override  
	            public void handleMessage(Message msg) {  
	                super.handleMessage(msg);  
	                // 将返回值回调到callBack的参数中  
	                webServiceCallBack.callBack((TranObject) msg.obj);  
	            }  
	  
	        };  
	          
	        // 开启线程去访问WebService  
	        executorService.submit(new Runnable() {  
	  
	            @Override  
	            public void run() {  
	                SoapObject resultObject = null;  
	                try {  
	                    httpTransportSE.call(null, soapEnvelope);  
	                    if (soapEnvelope.getResponse() != null) {  
	                        // 获取服务器响应返回的SoapObject  
	                        resultObject = (SoapObject) soapEnvelope.bodyIn; 
	                        TranObject tranObject = new TranObject(resultObject, TranObjectType.DEVMSG);
	                        // 将获取的消息利用Handler发送到主线程  
		                    mHandler.sendMessage(mHandler.obtainMessage(0,  
		                              tranObject));  
	                    }  
	                } catch (HttpResponseException e) {  
	                    e.printStackTrace(); 
	                    System.out.println("========Http服务器返回异常！");
	                } catch (IOException e) {  
	                    e.printStackTrace(); 
	                 
	                    TranObject tranObject = new TranObject(NetValue.NONET, TranObjectType.NETMSG);
                        // 将获取的消息利用Handler发送到主线程  
	                    mHandler.sendMessage(mHandler.obtainMessage(0,  
	                              tranObject)); 
	                    System.out.println("========webservice链接异常！");
	                } catch (XmlPullParserException e) {  
	                    e.printStackTrace();  
	                    System.out.println("========Xml服务器返回异常！");
	                } 
	            }  
	        });  
	    }  
	     
	    /** 
	     *  
	     *  这是一个回调接口 
	     * @author ppa 
	     *  
	     */  
	    public interface WebServiceCallBack {  
	        public void callBack(TranObject tranObject);  
	    }  
	    
	    
}
