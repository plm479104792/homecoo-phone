package object.p2pipcam.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class CustomAudioRecorder {

	private AudioRecordResult audioResult = null;
	private Thread recordThread = null;
	private boolean bRecordThreadRuning = false;
	private int m_in_buf_size = 0;
	private AudioRecord m_in_rec = null;
	private byte[] m_in_bytes = null;

	public interface AudioRecordResult {
		abstract public void AudioRecordData(byte[] data, int len);
	}

	public CustomAudioRecorder(AudioRecordResult result) {
		audioResult = result;
		initRecorder();
	}

	public void StartRecord() {
		synchronized (this) {
			Log.d("tag", "startRecord");
			if (bRecordThreadRuning) {
				Log.d("tag", "is startRecord");
				return;
			}
			Log.d("tag", "begin startRecord");
			bRecordThreadRuning = true;
			recordThread = new Thread(new RecordThread());
			recordThread.start();
		}
	}

	public void StopRecord() {
		synchronized (this) {
			if (!bRecordThreadRuning) {
				return;
			}

			bRecordThreadRuning = false;
			try {
				recordThread.join();

				Log.d("tag", "stopRecord()");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public void releaseRecord() {
		Log.d("tag", "releaseRecord");
		if (m_in_rec != null) {
			m_in_rec.release();
			m_in_rec = null;
		}

	}

	class RecordThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// if (!initRecorder()) {
			// return;
			// }

			m_in_rec.startRecording();
			while (bRecordThreadRuning) {
				int nRet = m_in_rec.read(m_in_bytes, 0, m_in_buf_size);
				// System.out.println("bAudioRecording runing.... nRet: " +
				// nRet);
				if (nRet == 0) {
					return;
				}
				Log.d("tag", "read audio");
				if (audioResult != null) {
					audioResult.AudioRecordData(m_in_bytes, nRet);
				}
			}
			m_in_rec.stop();
		}

	}

	public boolean initRecorder() {
		// TODO Auto-generated method stub
		m_in_buf_size = AudioRecord.getMinBufferSize(8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		// System.out.println("m_in_buf_size: " + m_in_buf_size);
		m_in_rec = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, m_in_buf_size);
		if (m_in_rec == null) {
			return false;
		}

		m_in_bytes = new byte[m_in_buf_size];
		return true;
	}

}