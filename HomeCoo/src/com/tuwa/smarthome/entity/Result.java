package com.tuwa.smarthome.entity;

import java.util.List;

public class Result {
	private String currentCity;
	private String pm25;
	private List<Index> index;
	private List<Weather_data> weather_data;

	public Result() {
	}

	public Result(String currentCity, String pm25, List<Index> index,
			List<Weather_data> weatherData) {
		this.currentCity = currentCity;
		this.pm25 = pm25;
		this.index = index;
		weather_data = weatherData;
	}

	public String getCurrentCity() {
		return currentCity;
	}

	public void setCurrentCity(String currentCity) {
		this.currentCity = currentCity;
	}

	public String getPm25() {
		return pm25;
	}

	public void setPm25(String pm25) {
		this.pm25 = pm25;
	}

	public List<Index> getIndex() {
		return index;
	}

	public void setIndex(List<Index> index) {
		this.index = index;
	}

	public List<Weather_data> getWeather_data() {
		return weather_data;
	}

	public void setWeather_data(List<Weather_data> weatherData) {
		weather_data = weatherData;
	}

	@Override
	public String toString() {
		return "{currentCity:" + currentCity + ", index:" + index + ", pm25:"
				+ pm25 + ", weather_data:" + weather_data + "}";
	}

}
