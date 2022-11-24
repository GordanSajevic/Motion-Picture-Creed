package com.gordan.motionpicturecreed.controller;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.gordan.motionpicturecreed.model.ScreenShow;
import com.gordan.motionpicturecreed.model.ScreenShowsPage;
import com.gordan.motionpicturecreed.service.ScreenShowService;

@RestController
@RequestMapping("/api/screen-shows")
public class ScreenShowController {
	
	@Autowired
	private ScreenShowService screenShowService;
	
	@PostMapping("")
	public ScreenShow findAndInsertScreenShow(@RequestParam String title, @RequestParam(required = false) String year) 
			throws JsonMappingException, JsonProcessingException, ParseException {
		return screenShowService.findAndInsertScreenShow(title, year);
	}

	@GetMapping("")
	public ScreenShowsPage getScreenShows(@RequestParam(required = false) String title, @RequestParam(required = false) String year, 
			@RequestParam(required = false) String genre, @RequestParam(required = false) String director, 
			@RequestParam(required = false) String writer, @RequestParam(required = false) String actors, 
			@RequestParam(required = false) String language, @RequestParam(required = false) String country, 
			@RequestParam(required = false) String rated, @RequestParam(required = false) String type, 
			@RequestParam(required = false) Integer pageNumber){
		return screenShowService.getAllScreenShows(title, year, genre, director, writer, actors, language, country, rated, type, pageNumber);
	}
}
