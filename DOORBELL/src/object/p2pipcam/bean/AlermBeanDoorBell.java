package object.p2pipcam.bean;

public class AlermBeanDoorBell {
	int on_delay_time=30;
	int alarm_delay_time=30;
	int record_size=30;
	int record_cover;
	int record_enable;
	int motion_enable;
	int pir_enable;
	int motion_level;
	public int getMotion_level() {
		return motion_level;
	}
	public void setMotion_level(int motion_level) {
		this.motion_level = motion_level;
	}
	public int getOn_delay_time() {
		return on_delay_time;
	}
	public void setOn_delay_time(int on_delay_time) {
		this.on_delay_time = on_delay_time;
	}
	public int getAlarm_delay_time() {
		return alarm_delay_time;
	}
	public void setAlarm_delay_time(int alarm_delay_time) {
		this.alarm_delay_time = alarm_delay_time;
	}
	public int getRecord_size() {
		return record_size;
	}
	public void setRecord_size(int record_size) {
		this.record_size = record_size;
	}
	public int getRecord_cover() {
		return record_cover;
	}
	public void setRecord_cover(int record_cover) {
		this.record_cover = record_cover;
	}
	public int getRecord_enable() {
		return record_enable;
	}
	public void setRecord_enable(int record_enable) {
		this.record_enable = record_enable;
	}
	public int getMotion_enable() {
		return motion_enable;
	}
	public void setMotion_enable(int motion_enable) {
		this.motion_enable = motion_enable;
	}
	public int getPir_enable() {
		return pir_enable;
	}
	public void setPir_enable(int pir_enable) {
		this.pir_enable = pir_enable;
	}
}
