package object.dbnewgo.client;

import object.p2pipcam.nativecaller.NativeCaller;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class BaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void showToast(String content) {
		Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
	}

	public void showToast(int rid) {
		Toast.makeText(this, getResources().getString(rid), Toast.LENGTH_SHORT)
				.show();
	}

	public void showToastLong(int rid) {
		Toast.makeText(this, getResources().getString(rid), Toast.LENGTH_LONG)
				.show();
	}

	public String returnString(int rid) {
		return getResources().getString(rid);
	}
	public static boolean hasSdcard() {

		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	/****
	 * �˳�ȷ��dialog
	 * */
	public void showSureDialog(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.homecoo_mini_logo);
		builder.setTitle(getResources().getString(R.string.exit)
				+ getResources().getString(R.string.app_name));
		builder.setMessage(R.string.exit_chenxu_show);
		builder.setPositiveButton(R.string.str_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Process.killProcess(Process.myPid());
						finish();
					}
				});
		builder.setNegativeButton(R.string.str_cancel, null);
		builder.show();
	}
	
	public void StartPPPP(String did, String user, String pwd) {
		String server = "EBGAEIBIKHJJGFJJEEHOFAENHLNBHGNMHMFDAADAAOJNKNKGDNAPDJPIGAKOIHLBBJNMKLDIPENJBKDE";
		String serTag = did.substring(0, 3);
		if (serTag.equalsIgnoreCase("GBE")) {
			server = "EBGAEIBIKHJJGFJKEOGIFKEFHCMCHMNBGLFNBBCFBJJBLKKODOAMDKPLGFKLIELCANNHKHDPOJNJBMCJJEMA";
			Log.d("server", "server-GBE");
		} else if (serTag.equalsIgnoreCase("OBJ")) {
			Log.d("server", "server-OBJ");
			server = "EBGAEOBOKHJMHMJMENGKFIEEHBMDHNNEGNEBBCCCBIIHLHLOCIACCJOFHHLLJEKHBFMPLMCHPHMHAGDHJNNHIFBAMC";
		} else {
			Log.d("server", "server-OBJ");
			server = "EBGAEIBIKHJJGFJJEEHOFAENHLNBHGNMHMFDAADAAOJNKNKGDNAPDJPIGAKOIHLBBJNMKLDIPENJBKDE";
		}
		Log.d("test", "server:" + serTag);
		NativeCaller.StartPPPP(did, user, pwd,server);
	}

}
