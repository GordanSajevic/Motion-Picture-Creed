package com.gordan.motionpicturecreed.model;

import java.util.List;

public class ScreenShowsPage {

	private List<ScreenShow> screenShowList;
	private Integer count;
	private Integer currentPage;
	
	public List<ScreenShow> getScreenShowList() {
		return screenShowList;
	}
	public void setScreenShowList(List<ScreenShow> screenShowList) {
		this.screenShowList = screenShowList;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}
	
	
}
