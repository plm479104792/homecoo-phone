package object.p2pipcam.bean;

import java.io.Serializable;

public class Song implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String filename;// 文件名
	private String title;// 歌曲名
	private int duration;// 时长
	private String singer;// 歌手
	private String album;// 专辑
	private String year;// 年份
	private String type;// 歌曲格式
	private String size;// 文件大小
	private String fileUrl;// 文件路径

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getSinger() {
		return singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
}
