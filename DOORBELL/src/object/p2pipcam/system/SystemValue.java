/**
 * 系统公用全局变量
 */
package object.p2pipcam.system;

import java.util.ArrayList;

import object.p2pipcam.bean.CameraParamsBean;

/**
 * @author zhaogenghuai
 * @creation 2012-12-17上午9:30:57
 */
public class SystemValue {
	public static ArrayList<CameraParamsBean> arrayList = new ArrayList<CameraParamsBean>();
	public static String deviceName = null;
	public static String usrName = null;
	public static String devicePass = null;
	public static String deviceId = null;
	public static int checkSDStatu = 0;
	public static int pictChange = 0;
	public static int NOTI = 0;
	public static boolean ISRUN = false;
	public static int ISPLAY = 0;
	public static int TAG_CAMERLIST = 0;
	public static String nowPushDID = "";
	// public static String SystemSerVer =
	// "SVTDEHAYLOSQTBHYPAARPCPFLKLQSTPNPDTAEIHUPLASEEPIPKAOSUPESXLXPHLUSQLVLSPALNLTLRLKLOLMLP-JBIOIWIHHXERFLFGFQEYFIIGELEGAQSSPCLMEQEHHWEG";
	public static String SystemSerVer = "EJTDHXEHIASQARSTEIPAPHATLKASPDEKPNLNLTAUHUHXSULVEESVSWAOEHPKLULXARSTPGSQPDLNPEPALRPFLKLOLQLP-$$";
	public static String doorBellAdmin = "admin";
	public static String doorBellPass = "";

	public static boolean isStartRun = false;

	public static CameraParamsBean getCameraParamsBean(String did) {
		CameraParamsBean bean = new CameraParamsBean();
		for (int i = 0; i < arrayList.size(); i++) {
			if (did.endsWith(arrayList.get(i).getDid())) {
				return arrayList.get(i);
			}
		}
		return null;
	}

}
