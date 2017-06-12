package com.homecoolink.global;

import java.lang.Thread.UncaughtExceptionHandler;

import com.homecoolink.startService;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


public class UnCeHandler implements UncaughtExceptionHandler{
	private Thread.UncaughtExceptionHandler mDefaultHandler;    
    public static final String TAG = "CatchExcep";  
    MyApp application;  
      
    public UnCeHandler(MyApp application){  
         //获取系统默认的UncaughtException处理器    
         mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
         this.application = application;  
         System.out.println(">>>UnCeHandler初始化异常捕获");
    }  
      
    @Override  
    public void uncaughtException(Thread thread, Throwable ex) {      
        if(!handleException(ex) && mDefaultHandler != null){   
            //如果用户没有处理则让系统默认的异常处理器来处理    
        	 System.out.println("系统捕获到了异常》》》");
            mDefaultHandler.uncaughtException(thread, ex);                
        }else{    
        	 System.out.println("捕获到了异常");
             
            Intent intent = new Intent(application.getApplicationContext(), startService.class);  
//            PendingIntent restartIntent = PendingIntent.getActivity(    
//                    application.getApplicationContext(), 0, intent,    
//                    Intent.FLAG_ACTIVITY_NEW_TASK); 
            PendingIntent restartIntent = PendingIntent.getService(application.getApplicationContext(), 0, intent,  Intent.FLAG_ACTIVITY_NEW_TASK);
            //退出程序                                          
            AlarmManager mgr = (AlarmManager)application.getSystemService(Context.ALARM_SERVICE);    
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, restartIntent); // 1秒钟后重启应用   
           
            application.finishActivity();  
        }    
    }  
      
    /**  
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.  
     *   
     * @param ex  
     * @return true:如果处理了该异常信息;否则返回false.  
     */    
    private boolean handleException(Throwable ex) {    
        if (ex == null) {    
            return false;    
        }    
        //使用Toast来显示异常信息    
//        new Thread(){    
//            @Override    
//            public void run() {    
//                Looper.prepare();    
//                Toast.makeText(application.getApplicationContext(), "很抱歉,程序出现异常,即将退出.",   
//                        Toast.LENGTH_SHORT).show();    
//                Looper.loop();    
//            }   
//        }.start();    
        return true;    
    }    
}  
