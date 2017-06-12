package object.p2pipcam.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhaogenghuai
 * @creation 2013-1-7ÏÂÎç5:37:26
 */
public class MovieInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	public String displayName;
	public String camerName;
	public String path;
	public String videoName;
	public String size;
	public Date date;

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public String getCamerName() {
		return camerName;
	}

	public void setCamerName(String camerName) {
		this.camerName = camerName;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
}
