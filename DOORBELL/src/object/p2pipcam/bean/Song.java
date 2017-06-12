package object.p2pipcam.bean;

import java.io.Serializable;

public class Song implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String filename;// �ļ���
	private String title;// ������
	private int duration;// ʱ��
	private String singer;// ����
	private String album;// ר��
	private String year;// ���
	private String type;// ������ʽ
	private String size;// �ļ���С
	private String fileUrl;// �ļ�·��

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
