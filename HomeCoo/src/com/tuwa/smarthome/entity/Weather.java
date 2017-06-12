package com.tuwa.smarthome.entity;

import java.util.List;

public class Weather {
	private String error;
	private String status;
	private String date;
	private List<Result> results;

	public Weather() {
	}

	public Weather(String error, String status, String date,
			List<Result> results) {
		this.error = error;
		this.status = status;
		this.date = date;
		this.results = results;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}

	@Override
	public String toString() {
		return "{date:" + date + ", error:" + error + ", results:" + results
				+ ", status:" + status + "}";
	}

}
