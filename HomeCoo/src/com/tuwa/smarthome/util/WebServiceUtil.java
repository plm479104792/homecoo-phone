package com.tuwa.smarthome.util;



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
  






import android.os.Handler;  
import android.os.Message; 
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
public class WebServiceUtil{

	
    // ����ռ�  
//	 public static   String ip="120.26.220.55";
	 public static   String ip="192.168.1.106";
  
	//���������
    
//	 public static String WEB_SERVER_URL = "http://120.26.220.55:8080/tuwa/webServices/devService";  //�豸service����
//	 public static String WEB_USER_SERVERURL = "http://120.26.220.55:8080/tuwa/webServices/userService";  //�豸service����
//	 public static String WEB_SCENESER_URL = "http://120.26.220.55:8080/tuwa/webServices/sceneService";  //�豸service����
	 
	 //���������
	 
	 public static String WEB_SERVER_URL = "http://192.168.2.106:8080/tuwa/webServices/devService";  //�豸service����
	 public static String WEB_USER_SERVERURL = "http://192.2.106.100:8080/tuwa/webServices/userService";  //�豸service����
	 public static String WEB_SCENESER_URL = "http://192.168.2.106:8080/tuwa/webServices/sceneService";  //�豸service����
	 
	 public  final static String WEB_NAMESPACE = "http://webservice.dev.controller.tuwa.com/";       //���ݷ����� 
	 public static String WEB_MUSIC_URL = "http://120.26.220.55:8080/tuwa/webServices/musicService";
//    public static String WEB_SERVER_URL = "http://192.168.2.103:8080/TUWA/webServices/sceneService";    //ʵ���ҷ�����
//     public static String WEB_SERVER_URL0 = "http://192.168.2.103:8080/TUWA/webServices/devService";    //ʵ���ҷ�����
	
	 
	 
	 
	    // ����3���̵߳��̳߳�  
	    private static final ExecutorService executorService = Executors  
	            .newFixedThreadPool(3);  
	  
	 /** 
	     *  
	     * @param <T>
	 * @param url 
	     *            WebService��������ַ 
	     * @param methodName 
	     *            WebService�ĵ��÷����� 
	     * @param properties 
	     *            WebService�Ĳ��� 
	     * @param webServiceCallBack 
	     *            �ص��ӿ� 
	     */  
	    public static  void callWebService(String url,  String methodName,  
	            HashMap<String, Object> properties,  
	            final WebServiceCallBack webServiceCallBack) {  
	        // ����HttpTransportSE���󣬴���WebService��������ַ  
	        final HttpTransportSE httpTransportSE = new HttpTransportSE(url);  
	        // ����SoapObject����  
	        SoapObject soapObject = new SoapObject(WEB_NAMESPACE, methodName);  
	  
	        // SoapObject��Ӳ���  
	        if (properties != null) {  
	            for (Iterator<Map.Entry<String, Object>> it = properties.entrySet()  
	                    .iterator(); it.hasNext();) {  
	                Map.Entry<String, Object> entry = it.next();  
	                soapObject.addProperty(entry.getKey(), entry.getValue());  
	            }  
	        }  
	  
	        // ʵ��SoapSerializationEnvelope������WebService��SOAPЭ��İ汾��  
	        final SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(  
	                SoapEnvelope.VER11);  
	        // �����Ƿ���õ���.Net������WebService  
//	        soapEnvelope.setOutputSoapObject(soapObject);  
//	        soapEnvelope.dotNet = true;  
	       
	        httpTransportSE.debug = true;  
	        soapEnvelope.bodyOut=soapObject;
	  
	        // �������߳������߳�ͨ�ŵ�Handler  
	        final Handler mHandler = new Handler() {  
	  
	            @Override  
	            public void handleMessage(Message msg) {  
	                super.handleMessage(msg);  
	                // ������ֵ�ص���callBack�Ĳ�����  
	                webServiceCallBack.callBack((SoapObject) msg.obj);  
	            }  
	  
	        };  
	  
	        // �����߳�ȥ����WebService  
	        executorService.submit(new Runnable() {  
	  
	            @Override  
	            public void run() {  
	                SoapObject resultSoapObject = null;  
	                try {  
	                    httpTransportSE.call(null, soapEnvelope);  
	                    if (soapEnvelope.getResponse() != null) {  
	                        // ��ȡ��������Ӧ���ص�SoapObject  
	                        resultSoapObject = (SoapObject) soapEnvelope.bodyIn;  
	                    }  
	                } catch (HttpResponseException e) {  
	                    e.printStackTrace();  
	                } catch (IOException e) {  
	                    e.printStackTrace();  
	                } catch (XmlPullParserException e) {  
	                    e.printStackTrace();  
	                } finally {  
	                    // ����ȡ����Ϣ����Handler���͵����߳�  
	                    mHandler.sendMessage(mHandler.obtainMessage(0,  
	                            resultSoapObject));  
	                }    
	            }      
	        });  
	    }  
	  
	    /** 
	     *  
	     *  ����һ���ص��ӿ� 
	     * @author xiaanming 
	     *  
	     */  
	    public interface WebServiceCallBack {  
	        public void callBack(SoapObject result);  
	    }  
	}  