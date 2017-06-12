package object.p2pipcam.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import object.p2pipcam.content.ContentCommon;
import object.p2pipcam.nativecaller.NativeCaller;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class TakeVideoThread extends Thread {
	private int tag = 0;
	private VideoBuffer videoBuffer = null;
	private AudioBuffer audioBuffer = null;
	private IorPBuffer iorPBuffer = null;
	private String strDID = "";
	private boolean isStart = false;
	private int frame = 5;
	public int width = 0;
	public int height = 0;
	private int video_i = 0;

	private int widthRun = 0;
	private int heightRun = 0;

	private byte[] audioByte, videoByte;
	private int thisIoP = 0;

	private long blockSize;
	private long totalBlocks;
	private long availableBlocks;
	private String name;
	private String szDid = "";
	private int sleepTime = 0;
	private boolean isone = false;

	public TakeVideoThread(int tag, String did, int frame, int width, int height) {
		// TODO Auto-generated constructor stub
		/** 0 jepg 1 h264 **/
		Log.e("test", "video tag:" + tag + "  did:" + did + "  frame:" + frame
				+ "  width:" + width + "  height:" + height);
		this.szDid = did;
		this.tag = tag;
		this.frame = 15;
		if (frame == 0) {
			this.frame = 15;
		}
		sleepTime = 1000 / this.frame;
		this.width = width;
		this.height = height;
		videoBuffer = new VideoBuffer();
		audioBuffer = new AudioBuffer();
		if (tag != 0) {
			iorPBuffer = new IorPBuffer();
		}
		strDID = did;
		isStart = true;
		isone = true;
	}

	public boolean addAudio(byte[] data) {
		return audioBuffer.addAudioData(data);
	}

	public boolean addVideo(byte[] data, int IorP, int width, int heigh) {
		Log.e("test", "video data width:" + width + "  heigh:" + heigh);
		this.width = width;
		this.height = heigh;
		if (tag == 0) {
			return videoBuffer.addData(data);
		} else {
			boolean isADD = false;
			if (videoBuffer.addData(data) && iorPBuffer.addData(IorP)) {
				if (videoBuffer.size() != iorPBuffer.size()) {
					videoBuffer.ClearAll();
					iorPBuffer.ClearAll();
					isADD = false;
				} else {
					isADD = true;
				}
			} else {
				videoBuffer.ClearAll();
				iorPBuffer.ClearAll();
				isADD = false;
			}
			return isADD;
		}
	}

	public boolean isRun() {
		return isStart;
	}

	public void clearAll() {
		if (tag != 0) {
			videoBuffer.ClearAll();
			iorPBuffer.ClearAll();
			audioBuffer.ClearAudioAll();
		} else {
			videoBuffer.ClearAll();
			audioBuffer.ClearAudioAll();
		}
	}

	public void stopThread() {
		// TODO Auto-generated method stub
		isStart = false;
		video_i = 0;
		NativeCaller.CloseAvi(szDid);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (isStart) {
			Log.e("test", "video start:" + isStart);
			video_i++;
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (video_i == 1) {
				name = getDateTime();
				if (tag > 0) {
					File div = new File(
							Environment.getExternalStorageDirectory(),
							ContentCommon.SDCARD_PATH + "/video");
					if (!div.exists()) {
						div.mkdirs();
					}
					NativeCaller.OpenAviFileName(szDid,
							Environment.getExternalStorageDirectory() + "/"
									+ ContentCommon.SDCARD_PATH + "/video/"
									+ name + ".avi", "h264", height, width,
							frame);

					widthRun = width;
					heightRun = height;
				} else {
					File div = new File(
							Environment.getExternalStorageDirectory(),
							ContentCommon.SDCARD_PATH + "/video");
					if (!div.exists()) {
						div.mkdirs();
					}
					NativeCaller.OpenAviFileName(szDid,
							Environment.getExternalStorageDirectory() + "/"
									+ ContentCommon.SDCARD_PATH + "/video/"
									+ name + ".avi", "mjpg", height, width,
							frame);
					widthRun = width;
					heightRun = height;
				}

			}
			if (video_i > 1 && video_i < 120000 / sleepTime) {
				if (widthRun != width || heightRun != height) {
					Log.e("test", "video writedata width:" + width + "  heigh:"
							+ height + "   widthRun:" + widthRun
							+ "   heightRun:" + heightRun);
					NativeCaller.CloseAvi(szDid);
					videoBuffer.ClearAll();
					if (video_i < 20) {
						File delFile = new File(
								Environment.getExternalStorageDirectory() + "/"
										+ ContentCommon.SDCARD_PATH + "/video/"
										+ name + ".avi");
						boolean b = delFile.delete();
						Log.e("tagdel", "if delect====" + b);
					}
					video_i = 0;
					continue;
				}
				audioByte = audioBuffer.RemoveAudioData();
				videoByte = videoBuffer.RemoveData();
				Log.e("test", "video start write1");
				if (tag != 0) {
					thisIoP = iorPBuffer.RemoveData();
					if (videoByte == null) {
						videoByte = new byte[0];
					}
					NativeCaller.WriteData(szDid, videoByte, videoByte.length,
							thisIoP);
					Log.e("test", "kkkkkWriteVideoData width:" + width
							+ "  heigh:" + height);

				} else {
					if (videoByte == null) {
						videoByte = new byte[0];
					}
					NativeCaller.WriteData(szDid, videoByte, videoByte.length,
							1);

				}
				if (audioByte != null) {
					NativeCaller.WriteAudioData(szDid, audioByte,
							audioByte.length);
					Log.i("test", "kkkkkWriteAudioData");
				}
				audioByte = audioBuffer.RemoveAudioData();
				if (audioByte != null) {
					NativeCaller.WriteAudioData(szDid, audioByte,
							audioByte.length);
					Log.i("test", "kkkkkWriteAudioData");
				}
				// if (isone) {
				// isone = false;
				// new Thread() {
				// public void run() {
				// while (isStart) {
				// if (video_i > 1
				// && video_i < 120000 / sleepTime + 20) {
				// try {
				// Thread.sleep(sleepTime);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				//
				// }
				// }
				// };
				// }.start();
				// }
			}

			if (video_i == 120000 / sleepTime) {
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				availableBlocks = stat.getAvailableBlocks();
				blockSize = stat.getBlockSize();
				if ((availableBlocks * blockSize) / (1024 * 1024) < 50) {
					NativeCaller.CloseAvi(szDid);
					clearAll();
					isStart = false;
					video_i = 0;
					break;
				}
				NativeCaller.CloseAvi(szDid);
				video_i = 0;
			}

		}
		super.run();
	}

	@SuppressWarnings("unused")
	private String getDateTime() {
		Date d = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String strDate = f.format(d);
		Log.d("tag", "record strDate:" + strDate);
		return strDID + "!" + "LOD_" + strDate;
	}
}
