package et.song.remotestar;

import android.content.Context;
import android.util.Log;

import com.threeman.android.remote.comm.RemoteControlLib;

import et.song.tg.face.IFinish;
import et.song.tg.face.ITg;

public class ThreeManRemote implements ITg {
	String TAG = "threeman";
	RemoteControlLib remoteControlLib;
	Context content;
	public ThreeManRemote(Context context){
		this.content = context;
		remoteControlLib = RemoteControlLib.GetInstance(this.content);
//		registerServiceReceiver();
	}
	@Override
	public void open(IFinish finish) throws Exception {
		// TODO Auto-generated method stub
		int i = remoteControlLib.open();
		if(i!=0){
			finish.OpenCallbk(i);
		}else{
			finish.OpenCallbk(0);
			close();
		}
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		Log.e(TAG, "�ر��豸");
		remoteControlLib.close();
//		unregisterServiceReceiver();
		
	}

	@Override
	public int write(byte[] outBytes, int len) throws Exception {
		// TODO Auto-generated method stub
		Log.e(TAG, "����");
		int i = remoteControlLib.write(outBytes, len);
		return i;
	}

	@Override
	public int read(byte[] inBytes, int len) throws Exception {
		// TODO Auto-generated method stub
		Log.e(TAG, "��ȡ");
		int i = remoteControlLib.read(inBytes, len);
		return i;
	}

	@Override
	public void ioctl(int cmd) throws Exception {
		// TODO Auto-generated method stub
		
	}

	
	
	
}
