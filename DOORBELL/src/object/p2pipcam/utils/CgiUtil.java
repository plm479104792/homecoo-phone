package object.p2pipcam.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class CgiUtil implements HttpUtil.HttpResult{
	private static final String STR_LOG = "CgiUtil" ;
	private static final int MAX_LIST_SIZE = 50;
	
	private static final String STR_CGI = "cgi";
	private static final String STR_OPERATION = "operation";
	private static final String STR_RESULT = "result";
	
	public static final int GET_CAMERA_PARAMS = 1;
	public static final int DECODER_CONTROL = 2;
	public static final int SET_BRIGTHNESS = 3;
	public static final int SET_CONTRAST = 4;
	
	private String strIpAddr;
	private int nPort;
	private String strUser;
	private String strPwd;
	private boolean bCmdThreadRuning = false;	
	private List<Map<String,Object>> cmdList = new ArrayList<Map<String, Object>>();
	private HttpUtil httpUtil = new HttpUtil(this);	
	
	private CgiResult cgiResult = null;
	
	public interface CgiResult{
		public abstract void CameraParams(int resolution, int vbright, int vcontrast, int mode, int flip, int framerate);
	}
	
	public CgiUtil(String ipaddr, int port, String user, String pwd, CgiResult cgiresult){
		strIpAddr = ipaddr;
		nPort = port;
		strUser = user;
		strPwd = pwd;	
		cgiResult = cgiresult;
	}
	
	public void addCMD(String strCGI, int operation, boolean bResult){
		synchronized(this){
			if(cmdList.size() > MAX_LIST_SIZE)
				cmdList.clear();
			Map<String,Object> map = new HashMap<String,Object>();
			map.put(STR_CGI, strCGI);
			map.put(STR_OPERATION, operation);
			map.put(STR_RESULT, bResult);
			cmdList.add(map) ;
		}		
	}
	
	private Map<String,Object> getCMD(){		
		synchronized(this){
			if(cmdList.size() > 0){
				Map<String,Object> map = cmdList.get(0);
				Map<String,Object> mapItem = new HashMap<String,Object>() ;
				mapItem.put(STR_CGI, (String)map.get(STR_CGI));
				mapItem.put(STR_OPERATION, (Integer)map.get(STR_OPERATION));
				mapItem.put(STR_RESULT, (Boolean)map.get(STR_RESULT));
				cmdList.remove(0);
				return mapItem;
			}			
			return null;					
		}
	}
	
	public void start(){
		bCmdThreadRuning = true;
		new Thread(new CmdThread()).start();
	}
	
	public void stop(){
		bCmdThreadRuning = false;
	}
	
	private class CmdThread implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(bCmdThreadRuning){
				try{
					Map<String,Object> map = getCMD();					
					if(map == null){
						Thread.sleep(10);
						continue;
					}	
					String strCGI = (String)map.get(STR_CGI);
					int oper = (Integer)map.get(STR_OPERATION);
					boolean bRes = (Boolean)map.get(STR_RESULT);
					String strUrl = "http://" + strIpAddr + ":" + nPort + 
							"/" + strCGI + "user=" + strUser + "&pwd=" + strPwd + "&";
					Log.d(STR_LOG, "strUrl: " + strUrl) ;
					httpUtil.send_get_request(strUrl, oper, bRes);					
				}catch(Exception e){
					break;
				}
			}
		}		
	}	

	@Override
	public void httpResult(String strResult, int operation) {
		// TODO Auto-generated method stub
		Log.d(STR_LOG, "strResult: " + strResult) ;
		switch(operation){
		case GET_CAMERA_PARAMS:
			getCameraParams(strResult);
			break;			
		}
	}

	private void getCameraParams(String strResult) {
		// TODO Auto-generated method stub
		if(cgiResult == null)
			return;		
		
		String strPattern = "var resolution=([0-9]{1,3});var vbright=([0-9]{1,3});var vcontrast=([0-9]{1,3});var mode=([0-9]{1,3});var flip=([0-9]{1,3});var framerate=([0-9]{1,3});";        
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strResult);
        if (m.find()) { 
            String resolution = m.group(1); 
            System.out.println(resolution); 
            String vbright = m.group(2); 
            System.out.println(vbright); 
            String vcontrast = m.group(3); 
            System.out.println(vcontrast);
            String mode = m.group(4); 
            System.out.println(mode);
            String flip = m.group(5); 
            System.out.println(flip);
            String framerate = m.group(6); 
            System.out.println(framerate);       
            
            cgiResult.CameraParams(Integer.valueOf(resolution), 
            		Integer.valueOf(vbright), 
            		Integer.valueOf(vcontrast), 
            		Integer.valueOf(mode), 
            		Integer.valueOf(flip), 
            		Integer.valueOf(framerate));
        } 
	}
}