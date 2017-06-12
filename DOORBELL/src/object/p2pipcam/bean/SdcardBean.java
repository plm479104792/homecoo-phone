package object.p2pipcam.bean;

import java.io.Serializable;

public class SdcardBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String did;
	private int record_conver_enable;
	private int record_timer;
	private int record_size;
	private int record_time_enable;
	private int record_sd_status;
	private int sdtotal;
	private int sdfree;

	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}

	public int getRecord_conver_enable() {
		return record_conver_enable;
	}

	public void setRecord_conver_enable(int record_conver_enable) {
		this.record_conver_enable = record_conver_enable;
	}

	public int getRecord_timer() {
		return record_timer;
	}

	public void setRecord_timer(int record_timer) {
		this.record_timer = record_timer;
	}

	public int getRecord_size() {
		return record_size;
	}

	public void setRecord_size(int record_size) {
		this.record_size = record_size;
	}

	public int getRecord_time_enable() {
		return record_time_enable;
	}

	public void setRecord_time_enable(int record_time_enable) {
		this.record_time_enable = record_time_enable;
	}

	public int getRecord_sd_status() {
		return record_sd_status;
	}

	public void setRecord_sd_status(int record_sd_status) {
		this.record_sd_status = record_sd_status;
	}

	public int getSdtotal() {
		return sdtotal;
	}

	public void setSdtotal(int sdtotal) {
		this.sdtotal = sdtotal;
	}

	public int getSdfree() {
		return sdfree;
	}

	public void setSdfree(int sdfree) {
		this.sdfree = sdfree;
	}

}
