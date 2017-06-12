

package object.dbnewgo.client;

import object.p2pipcam.system.SystemValue;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.e("test", "reboot-startserver");
		Log.e("TAG", "zhao-服务检查中。。。");
		if (!SystemValue.ISRUN) {
			Intent service = new Intent(context, BridgeService.class);
			context.startService(service);
		}

	}

}
