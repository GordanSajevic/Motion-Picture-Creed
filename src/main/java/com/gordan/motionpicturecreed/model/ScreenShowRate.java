package com.gordan.motionpicturecreed.model;

public class ScreenShowRate {
	
	private Integer id;
	
	private ScreenShow screenShow; 
	
	private Rate rate;
	
	private boolean valid;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ScreenShow getScreenShow() {
		return screenShow;
	}

	public void setScreenShow(ScreenShow screenShow) {
		this.screenShow = screenShow;
	}

	public Rate getRate() {
		return rate;
	}

	public void setRate(Rate rate) {
		this.rate = rate;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
