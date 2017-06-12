package com.tuwa.smarthome.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tuwa.smarthome.BaseActivity;
import com.tuwa.smarthome.R;
import com.tuwa.smarthome.dao.APPMusicDao;
import com.tuwa.smarthome.entity.APPMusic;
import com.tuwa.smarthome.entity.Music;
import com.tuwa.smarthome.entity.MusicOrder;
import com.tuwa.smarthome.entity.MusicSocket;
import com.tuwa.smarthome.entity.MusicSocketByte;
import com.tuwa.smarthome.entity.ResultMessage;
import com.tuwa.smarthome.entity.Volume;
import com.tuwa.smarthome.global.NetValue;
import com.tuwa.smarthome.global.SystemValue;
import com.tuwa.smarthome.network.DatagramSocketPhoneServer;
import com.tuwa.smarthome.util.MusicJpush;
import com.tuwa.smarthome.util.MusicUtil;
import com.tuwa.smarthome.util.ToastUtils;

public class MusicMainActivity extends BaseActivity {
    @Bind(R.id.tv_head_submit)  TextView tvExit;
    @Bind(R.id.tv_head_back) TextView tvBack;
    @Bind(R.id.tv_head_title) TextView tvTitle;
    @Bind(R.id.tv_bottom_network) TextView tvbttomNetwork;
	  
	private static String musicName;
	private static int MusicSize;
	private static int MusicPosition=-1;
	private ListView lv;
	private ImageButton btnpause,btnRandom,btnSingle,btnnextSong,btnformerSong;
	private TextView tv_songName;
	private static List<Music> mArrayList=new ArrayList<Music>();
	public static List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
	private static int flag=0;		//音乐 暂停/播放 图片切换   0表示暂停/1表示播放
	private MusicListAdapter adapter=null;
	/**
	 * socket私有属性
	 * */
	private boolean socketStatus=false;
	private Socket socket=null;
	private OutputStream outputStream=null;
	private InputStream inputStream=null;
	StringBuffer stringBuffer=new StringBuffer();
	private String Incodemusiclist="";
	private String data;
	//音量进度条
	private SeekBar seekBar;
	
	   /*辅线程动态刷新页面*/
    Handler handler=new Handler(){
	   	 @Override
	   	 public void handleMessage(Message msg){
	   		 switch(msg.what){
	   		 case 0x129:
	   				 adapter=new MusicListAdapter(MusicMainActivity.this,mArrayList);
	   				 lv.setAdapter(adapter);
	   				List<APPMusic> list=MusicUtil.GetAppMusicList(mArrayList);
		   			new APPMusicDao(MusicMainActivity.this).InsertAppMusic(list);
	   			 break;
	   		 case 1:
	   			 //得到七寸屏发送过来的  未解码的音乐列表字符串
	   			Incodemusiclist=msg.obj.toString();
	   			stringBuffer.setLength(0);
	   			/**
	   			 * 这里要做一个把收到的音乐列表 转码   之后存到sqlite上去  方便thememusic 调用
	   			 * */
//	   			List<APPMusic> list=MusicUtil.GetAppMusicList(mArrayList);
//	   			new APPMusicDao(MusicMainActivity.this).InsertAppMusic(list);
	   			MusicOrder musicOrder=MusicUtil.GetVolume();
				sendSocket(musicOrder);
				//情景这里获取到音乐的时候就要关闭手机端接收广播的
//				Intent intent1=new Intent(MusicMainActivity.this,DatagramSocketPhoneServer.class);
//				DatagramSocketPhoneServer.datasocketserverReceiveFlag=false;
//				DatagramSocketPhoneServer.datasocketserverSendFlag=false;
//				MusicMainActivity.this.stopService(intent1);
				
				MusicUtil.StopDatagramSocketServer(MusicMainActivity.this);
	   			 break;
	   		 case 2:
	   			 //情况socket中的stringBuffer
	   			Incodemusiclist=msg.obj.toString();
	   			stringBuffer.setLength(0);
	   			 break;
	   		 }
	   	 }
	   };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_main);
		ButterKnife.bind(MusicMainActivity.this);
		//初始化试图
		initViews();
		//这里完全测试版本需要注释掉
		tvTitle.setText(SystemValue.MUSIC_SCREEN_IP);
		
		if (NetValue.netFlag==NetValue.INTRANET) {
			//先建立socket连接，并获取音乐列表
			connectSocket();
		}else if (NetValue.netFlag==NetValue.OUTERNET) {
			//外网    加载音乐列表
			GetMusicFromServer();
			//获取七寸屏当前音量
			GetMusicolume();
		}
		//点击列表歌曲播放
		lv.setOnItemClickListener(CtrlMusicPlayListener);
		//点歌暂停按钮  暂停歌曲
		btnpause.setOnClickListener(CtrlMusicPauseListener);
		//点击随机按钮  从当前歌曲开始随机播放
		btnRandom.setOnClickListener(CtrlMusicRandomListener);
		//单曲循环
		btnSingle.setOnClickListener(CtrlMusicSingerListener);
		//上一首
		btnformerSong.setOnClickListener(CtrlMusicLastSongListener);
		//下一首
		btnnextSong.setOnClickListener(CtrlMusicNextSongListener);
		seekBar.setOnSeekBarChangeListener(SeekBarChangeListener);
	}
	
	
	/**
	 * @author xiaobai
	 * @Description:从服务器上获取七寸屏上的所有音乐
	 * @param:gatewayNo
	 * @Date:2016-05-21
	 * */
	private void GetMusicFromServer(){
		btnpause.setBackgroundResource(R.drawable.pause);
		RequestParams params=new RequestParams();
		params.addBodyParameter("gatewayNo", SystemValue.gatewayid);
		HttpUtils httpUtils=new HttpUtils();
		httpUtils.send(HttpMethod.POST, NetValue.MUSIC_GET_MUSIC_FROM_SERVER, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// 访问网络失败
				ToastUtils.showToast(MusicMainActivity.this,"请检查网络连接！", 1000);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				ResultMessage msg=JSONObject.parseObject(arg0.result, ResultMessage.class);
				if (msg.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
					mArrayList.clear();
					mArrayList=JSONArray.parseArray((String)msg.getObject(),Music.class);
					listems=MusicUtil.TolistMap(mArrayList);
					if (mArrayList.size()>0) {
						//  异步进程更新界面
						Message mesg=new Message();
						mesg.what=0x129;
						handler.sendMessage(mesg);
					}else{
						ToastUtils.showToast(MusicMainActivity.this, "请在七寸屏上同步歌曲！", 2000);
					}
				}else{
					//请求失败，相应的处理
					ToastUtils.showToast(MusicMainActivity.this, msg.getMessageInfo(), 1000);
				}
			}
		});
	}
	
	/**
	 * 获取七寸屏当前音量
	 * */
	private void GetMusicolume(){
		RequestParams params=new RequestParams();
		params.addBodyParameter("gatewayNo", SystemValue.gatewayid);
		HttpUtils httpUtils=new HttpUtils();
		httpUtils.send(HttpMethod.POST, NetValue.MUSIC_VOLUME_GET, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				seekBar.setProgress(0);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				System.out.println(arg0.result);
				ResultMessage msg=JSON.parseObject(arg0.result, ResultMessage.class);
				if (msg.getResult().equals(NetValue.SUCCESS_MESSAGE)) {
					//这里try 是为了解决解析报错
					try {
						
					Volume volume=JSONObject.parseObject((String)msg.getObject(), Volume.class);
					//进度条绑定当前音量
					if (volume.getVolume().equals("") || volume.getVolume()==null) {
						seekBar.setProgress(7);
					}else{
						seekBar.setProgress(Integer.valueOf(volume.getVolume()));
					}
					} catch (Exception e) {
						System.err.println(e);
						seekBar.setProgress(7);
					}
				}else{
					seekBar.setProgress(0);
				}
				
			}
		});
		
	}
	
	
	/**
	 * 点击列表歌曲  播放音乐(默认列表循环)
	 * */
	private OnItemClickListener CtrlMusicPlayListener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			MusicSize=listems.size()-1;
			MusicPosition=arg2;
			String songName=(String) listems.get(arg2).get("songName");
			btnpause.setBackgroundResource(R.drawable.play);
			System.out.println(SystemValue.gatewayid);
			MusicOrder order=MusicUtil.ToMusicOrder(songName, SystemValue.MUSIC_CTRL_PLAY, null);
			if (NetValue.netFlag==NetValue.INTRANET) {
				sendSocket(order);
//				devService.sendSocket(order);
			}else if (NetValue.netFlag==NetValue.OUTERNET) {
				MusicJpush.SendServer(order);
			}
//			MusicJpush.JpushMusicOrder(order);
			musicName=songName;
			tv_songName.setText(musicName);
			flag=1;
			Message mesg=new Message();
			mesg.what=0x129;
			handler.sendMessage(mesg);
		}
	};
	
	/**
	 * 点击暂停按钮，暂停歌曲
	 * */
	private OnClickListener CtrlMusicPauseListener=new OnClickListener() {
		@Override
		public void onClick(View arg0) {
//			MusicOrder order=MusicUtil.ToMusicOrder(musicName, SystemValue.MUSIC_CTRL_PAUSE,null);
			MusicOrder order=MusicUtil.ToMusicOrder("aa", SystemValue.MUSIC_CTRL_PAUSE,null);
//			MusicJpush.JpushMusicOrder(order);
			if (NetValue.netFlag==NetValue.INTRANET) {
				sendSocket(order);
//				devService.sendSocket(order);
			}else if (NetValue.netFlag==NetValue.OUTERNET) {
				MusicJpush.SendServer(order);
			}
			//flag 0表示暂停  1表示播放
			if (flag==0) {
				btnpause.setBackgroundResource(R.drawable.play);
				flag=1;
			}else{
				btnpause.setBackgroundResource(R.drawable.pause);
				flag=0;
			}
		}
	};
	
	/**
	 * 点击随机按钮  从当前歌曲开始随机播放
	 * */
	private OnClickListener CtrlMusicRandomListener=new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			MusicOrder order=MusicUtil.ToMusicOrder(musicName, SystemValue.MUSIC_STYLE_RANDOM, null);
//			MusicJpush.JpushMusicOrder(order);
			if (NetValue.netFlag==NetValue.INTRANET) {
				sendSocket(order);
//				devService.sendSocket(order);
			}else if (NetValue.netFlag==NetValue.OUTERNET) {
				MusicJpush.SendServer(order);
			}
			btnpause.setBackgroundResource(R.drawable.play);
			flag=1;
		}
	};
	
	/**
	 * 点击单曲按钮 开始单曲循环
	 * */
	private OnClickListener CtrlMusicSingerListener=new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			MusicOrder order=MusicUtil.ToMusicOrder(musicName, SystemValue.MUSIC_STYLE_SINGER, null);
//			MusicJpush.JpushMusicOrder(order);
			if (NetValue.netFlag==NetValue.INTRANET) {
				sendSocket(order);
//				devService.sendSocket(order);
			}else if (NetValue.netFlag==NetValue.OUTERNET) {
				MusicJpush.SendServer(order);
			}
			btnpause.setBackgroundResource(R.drawable.play);
			flag=1;
		}
	};
	
	/**
	 * 播放上一首歌曲
	 * */
	private OnClickListener CtrlMusicLastSongListener=new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			MusicPosition=MusicPosition-1;
			if(MusicPosition==-1){
				MusicPosition=MusicSize;
			}else if(MusicPosition>MusicSize){
				MusicPosition=MusicSize-1;
			}
			try {
				
			String songName=(String)listems.get(MusicPosition).get("songName");
			musicName=songName;
			tv_songName.setText(musicName);
			} catch (Exception e) {
				System.err.println(e);
			}
			btnpause.setBackgroundResource(R.drawable.play);
			MusicOrder order=MusicUtil.ToMusicOrder(musicName, SystemValue.MUSIC_CTRL_LAST_SONG, null);
//			MusicJpush.JpushMusicOrder(order);
			if (NetValue.netFlag==NetValue.INTRANET) {
				sendSocket(order);
//				devService.sendSocket(order);
			}else if (NetValue.netFlag==NetValue.OUTERNET) {
				MusicJpush.SendServer(order);
			}
			flag=1;
			Message mesg=new Message();
			mesg.what=0x129;
			handler.sendMessage(mesg);
		}
	};
	
	/**
	 * 播放 下一首歌曲
	 * */
	private OnClickListener CtrlMusicNextSongListener=new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			MusicPosition=MusicPosition+1;
			if(MusicPosition==0){
				MusicPosition=1;
			}else if(MusicPosition>MusicSize){
				MusicPosition=0;
			}
			try {
				String songName=(String)listems.get(MusicPosition).get("songName");
				musicName=songName;
				tv_songName.setText(musicName);
			} catch (Exception e) {
				System.err.println(e);
			}
			MusicOrder order=MusicUtil.ToMusicOrder(musicName, SystemValue.MUSIC_CTRL_NEXT_SONG, null);
			if (NetValue.netFlag==NetValue.INTRANET) {
				sendSocket(order);
			}else if (NetValue.netFlag==NetValue.OUTERNET) {
				MusicJpush.SendServer(order);
			}
			btnpause.setBackgroundResource(R.drawable.play);
			flag=1;
			Message mesg=new Message();
			mesg.what=0x129;
			handler.sendMessage(mesg);
		}
	};
	
	/**
	 * 音量调节
	 * */
	private OnSeekBarChangeListener SeekBarChangeListener=new OnSeekBarChangeListener() {
		
		//移动后放开事件  每次点击结束之后。推送音量大小到webserver  或者七寸屏
		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			//获取当前进度     
			int volume=arg0.getProgress();
//			ToastUtils.showToast(MusicMainActivity.this, String.valueOf(volume), 3000);
			if (NetValue.netFlag==NetValue.INTRANET) {
				MusicOrder musicOrder=MusicUtil.SetVolume(String.valueOf(volume));
				sendSocket(musicOrder);
			}else if (NetValue.netFlag==NetValue.OUTERNET) {
				MusicOrder order=MusicUtil.ToMusicOrder(String.valueOf(volume), SystemValue.MUSIC_VOLUME, null);
				MusicJpush.SendServer(order);
			}
		}
		//开始移动进度条触发事件
		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
		}
		//监听滑动条滑动过程的事件 
		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			// TODO Auto-generated method stub
		}
	};
	
	 /***退出系统***/
    @OnClick(R.id.tv_head_submit)
    public void systemExit(){
    	initExitDialog();
    }
    /***返回***/
    @OnClick(R.id.tv_head_back)
    public void back(){
    	Intent intent=new Intent(MusicMainActivity.this,HomeActivity.class);
		startActivity(intent);		
		finish();
    }
    /***空间***/
    @OnCheckedChanged(R.id.rb_navi_space)
    public void  spaceDeviceShow(){
    	Intent intent=new Intent(MusicMainActivity.this,SpaceDevicesActivity.class);
		startActivity(intent);		
		finish();
    }
    /***情景模式***/
    @OnCheckedChanged(R.id.rb_navi_scene)
	public void sceneMode(){
		Intent sceneIntent=new Intent(MusicMainActivity.this,SceneModelActivity.class);
		startActivity(sceneIntent);		
		finish();
	}
    /***网络切换***/
	@OnClick(R.id.tv_bottom_network)
	public void networkSwitchClick(){
		
//		netWorkSwitch(mService,tvbttomNetwork);
	}
	/***防区管理***/
	@OnCheckedChanged(R.id.rb_navi_alert)
	public void DefenceAreaClick(){
		Intent sceneIntent=new Intent(MusicMainActivity.this,DefenceAreaActivity.class);
		startActivity(sceneIntent);		
		finish();
	}
    /***系统设置***/
	@OnCheckedChanged(R.id.rb_navi_set)
    public void  systemSet(){
    	Intent intent=new Intent(MusicMainActivity.this,SetActivity.class);
		startActivity(intent);		
		finish();
    }
	@Override
	protected void initViews() {
		tvExit.setText("退出");
		tvTitle.setText("音乐");
		lv=(ListView) findViewById(R.id.myArrayList);
		tv_songName=(TextView) findViewById(R.id.tv_songName);
		btnpause=(ImageButton) findViewById(R.id.btnpause);
		btnnextSong=(ImageButton) findViewById(R.id.btnnextSong);
		btnformerSong=(ImageButton) findViewById(R.id.btnformerSong);
		btnRandom=(ImageButton) findViewById(R.id.btnRandom);
		btnSingle=(ImageButton) findViewById(R.id.btnSingle);
		seekBar=(SeekBar) findViewById(R.id.seekbar);
		seekBar.setMax(15);
	}
//	@Override
//	protected void initEvents() {
//	}
	public class MusicListAdapter extends BaseAdapter {
		//private int[] colors=new int[]{0x7f020001, 0x7f020035};
		private int[] colors=new int[]{0x30BCDFE3,0x30E1EFEF};
		private List<Music> musicList;
		private LayoutInflater mInflater;
		public MusicListAdapter(Context context, List<Music> vector) {
			this.musicList = vector;
			mInflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			return musicList.size();
		}
		@Override
		public Object getItem(int position) {
			return musicList.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder holder;
			if (view != null) {
			      holder = (ViewHolder) view.getTag();
			    } else {
			      view = mInflater.inflate(R.layout.include_music,parent,false);
			      holder = new ViewHolder(view);
			      view.setTag(holder);
			    }
			Music music=musicList.get(position);
			holder.tvmusicname.setText(music.getSongName());
			int colorPos = position % colors.length;  
			view.setBackgroundColor(colors[colorPos]);  
			if(MusicPosition==position){
				view.setBackgroundColor(getResources().getColor(R.color.listSelector));
			}
			return view;
		}
		class ViewHolder {
			@Bind(R.id.tv_music_name)  TextView tvmusicname;
			public ViewHolder(View view) {
				ButterKnife.bind(this,view);
			}
		}
		
	}
	
	@Override
	protected void initDatas() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @Description:建立socket
	 * @param IP
	 * */
	@SuppressLint("ShowToast")
	private void connectSocket(){
//		System.out.println("MusicMainActivity        通过socket连接七寸屏!");
		/*if (SystemValue.MUSIC_SCREEN_IP==null || SystemValue.MUSIC_SCREEN_IP.equals("")) {
			Toast.makeText(MusicMainActivity.this, "请检查七寸屏是否已经连上WIFI!", 2000).show();
		}*/
		Thread thread=new Thread(){
			
			public void run(){
				super.run();
				if (!socketStatus) {
					try {
						socket=new Socket(SystemValue.MUSIC_SCREEN_IP,8000);
//						socket=new Socket("192.168.0.111",8000);
//						socket=new Socket("192.168.0.108",8000);
//						System.out.println("已经连接上七寸屏!"+SystemValue.MUSIC_SCREEN_IP);
						if (socket!=null) {
							socketStatus=true;
						}
						outputStream=socket.getOutputStream();
						inputStream=socket.getInputStream();
						new ServerThread(socket,inputStream).start();
						MusicOrder order=MusicUtil.GetMusicList();
						sendSocket(order);
//						MusicOrder musicOrder=MusicUtil.GetVolume();
//						sendSocket(musicOrder);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}
			
		};
		thread.start();
	}

	class ServerThread extends Thread{
		private Socket socket;
		private InputStream inputStream;
		private StringBuffer stringBuffer=MusicMainActivity.this.stringBuffer;
		
		public ServerThread(Socket socket,InputStream inputStream){
			this.socket=socket;
			this.inputStream=inputStream;
		}
		
		public void run() {
			int len;
			byte[] bytes=new byte[20];
			boolean isString=false;
			
			// 在这里需要明白一下什么时候其会等于 -1，其在输入流关闭时才会等于 -1，
			// 并不是数据读完了，再去读才会等于-1，数据读完了，最结果也就是读不到数据为0而已；
			if (stringBuffer.length()>0) {
				stringBuffer.delete(0, stringBuffer.length()-1);
			}
			try {
				while ((len = inputStream.read(bytes))!=-1) {
					for (int i = 0; i < len; i++) {
						if (bytes[i] != '\0') {
							stringBuffer.append( (char) bytes[i]);
						}else{
							isString=true;
							break;
						}
					}
					if (isString) {
						String aa=stringBuffer.toString();
						System.out.println("=========Socket=========="+aa);
						MusicSocketByte byte1=JSONObject.parseObject(aa, MusicSocketByte.class);
						//根据order 判断进行什么样的操作
						if (byte1.getOrder().equals(SystemValue.MUSIC_LIST_GET)) {
							//音乐列表
							List<MusicSocket> musicSockets=MusicUtil.ToMusicSocketList(aa);
							//这里需要判断下传过来的是什么值
							mArrayList.clear();
							mArrayList=MusicUtil.ToMusicList(musicSockets);
							listems=MusicUtil.TolistMap(mArrayList);
							if (mArrayList.size()>0) {
								//  异步进程更新界面
								Message mesg=new Message();
								mesg.what=0x129;
								handler.sendMessage(mesg);
							}
							Message msg=handler.obtainMessage();
							msg.what=1;
							msg.obj=stringBuffer;
							handler.sendMessage(msg);
							
						}else if (byte1.getOrder().equals(SystemValue.MUSIC_VOLUME)) {
							//音量数值
							seekBar.setProgress(Integer.valueOf(byte1.getStyle()));
							Message msg=handler.obtainMessage();
							msg.what=2;
							msg.obj=stringBuffer;
							handler.sendMessage(msg);
						}
						isString=false;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	/**
	 * @Description:socket发送音乐控制  MusicOrder 到七寸屏
	 * */
	public void sendSocket(MusicOrder musicOrder){
		//在后面加'\0'是为了在服务端方便我们解析
//		data=MusicUtil.ToMusicOrderSocketJson(musicOrder)+'\0';
//		System.out.println("ccccccccccccccc "+musicOrder.toString());
		data=MusicUtil.ToMusicOrderSocketJson(musicOrder);
//		System.out.println("ddddddddddddddd "+data);
		Thread thread=new Thread(){																																						
			
			@Override
			public void run() {
				super.run();
				if (socketStatus) {
					try {
						outputStream.write(data.getBytes());
						outputStream.flush();
						System.out.println("MusicMainActivity  发送socket指令到七寸屏=========== "+data);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				
			}
			
		};
		thread.start();
	}
	
	@Override  
    protected void onDestroy() {  
//		unbindService(devconn);
        super.onDestroy(); //注意先后  
    }  
	
}
