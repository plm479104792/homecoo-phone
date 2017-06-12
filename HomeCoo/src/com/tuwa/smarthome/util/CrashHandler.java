package com.tuwa.smarthome.util;  
  
import java.lang.Thread.UncaughtExceptionHandler;  
import java.text.DateFormat;  
import java.text.SimpleDateFormat;  
import java.util.HashMap;  
import java.util.Map;  

import com.homecoolink.global.MyApp;
import com.tuwa.smarthome.activity.HomeActivity;
import com.tuwa.smarthome.activity.LoginActivity;
  
import android.content.Context;  
import android.content.Intent;
  
/** 
 * @ClassName: CrashHandler 
 * @author victor_freedom (x_freedom_reddevil@126.com) 
 * @createddate 2014-12-25 下午11:41:12 
 * @Description: UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告. 
 */  
public class CrashHandler implements UncaughtExceptionHandler {  
  
    public static final String TAG = "CrashHandler";  
  
    // CrashHandler 实例  
    private static CrashHandler INSTANCE = new CrashHandler();  
  
    // 程序的 Context 对象  
    private Context mContext;  // app对象  
    private MyApp app;  
  
    // 系统默认的 UncaughtException 处理类  
    private Thread.UncaughtExceptionHandler mDefaultHandler;  
  
    // 用来存储设备信息和异常信息  
    private Map<String, String> infos = new HashMap<String, String>();  
  
    // 用于格式化日期,作为日志文件名的一部分  
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");  
  
    /** 保证只有一个 CrashHandler 实例 */  
    public CrashHandler() {  
    }  
  
    /** 获取 CrashHandler 实例 ,单例模式 */  
    public static CrashHandler getInstance() {  
        return INSTANCE;  
    }  
  
    /** 
     * @Title: init 
     * @Description: 初始化 
     * @param context 
     * @param app  传入的app 
     * @throws 
     */  
    public void init(Context context, MyApp app) {  
        // 传入app对象，为完美终止app  
        this.app = app;  
        mContext = context;  
        // 获取系统默认的 UncaughtException 处理器  
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
        // 设置该 CrashHandler 为程序的默认处理器  
        Thread.setDefaultUncaughtExceptionHandler(this);  
        System.out.println("初始化异常退出捕获");
    }  
  
    /** 
     * 当 UncaughtException 发生时会转入该函数来处理 
     */  
    @Override  
    public void uncaughtException(Thread thread, Throwable ex) {  
        if (!handleException(ex) && mDefaultHandler != null) {  
            // 如果用户没有处理则让系统默认的异常处理器来处理  
            mDefaultHandler.uncaughtException(thread, ex);  
        } else {  
            // 释放资源不能像常规的那样在activity的onDestroy方法里面执行，因为如果出现全局异常捕获，activity的关闭有时候是不会再走相关的生命周期函数的（onDesktroy,onStop,onPause等）。  
            // 这里是博主在退出app之前需要释放掉的一些资源，通过之前讲的AppActivityManager来拿到对应的实例activity释放里面的资源，然后调用AppExit退出应用程序  
//            ProductActivity activitys = (ProductActivity) AppActivityManager  
//                    .getAppActivityManager().getActivity(ProductActivity.class);  
//            activitys.offline();  
//            Activity activity = (MainActivity) AppActivityManager  
//                    .getAppActivityManager().getActivity(MainActivity.class);  
//            activity.offline();  
//            // 当执行这一句的时候，其实APP有时候并没有完美的退出（方法详情可以查看博主之前的写的activity管理的文章）  
//            // 博主的项目里面有网络连接、有后台服务、多线程等各种。执行完这个方法之后，虽然能够闪退出去，但是，当再次进入APP的时候，是回出现ANR的，说明，这样还是没有的完美退出APP  
//            AppActivityManager.getAppActivityManager().AppExit(mContext);  
//            // 之前说application的时候说过，当app退出的时候，会执行onTerminate方法，但是有时候不会主动执行。那么，博主想，如果我们强制执行这个方法，能不能让app完美的终止呢?答案是肯定的。  
//            app.onTerminate();  
        	
        	
        	android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
        	
//        	 //发送广播清空当前的activity栈
//			Intent finishIntent = new Intent();  //Itent就是我们要发送的内容
//			finishIntent.setAction("ACTIVITY_FINISH");   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
//			mContext.sendBroadcast(finishIntent);   //发送广播
    		
        	
    		Intent intent = new Intent(mContext,HomeActivity.class);
    		mContext.startActivity(intent);
        	
        	
        	System.out.println("捕捉到系统异常退出，系统将自动重启！");
        }  
    }  
  
    /** 
     * 自定义错误处理，收集错误信息，发送错误报告等操作均在此完成 
     *  
     * @param ex 
     * @return true：如果处理了该异常信息；否则返回 false 
     */  
    private boolean handleException(Throwable ex) {  
        if (ex == null) {  
            return false;  
        }  
        // 收集设备参数信息  
        
        return true;  
    }  
   
}  