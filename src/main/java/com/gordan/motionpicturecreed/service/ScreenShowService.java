package com.gordan.motionpicturecreed.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.gordan.motionpicturecreed.db.record.RateRecord;
import com.gordan.motionpicturecreed.db.record.ScreenShowRateRecord;
import com.gordan.motionpicturecreed.db.record.ScreenShowRecord;
import com.gordan.motionpicturecreed.db.record.SourceRecord;
import com.gordan.motionpicturecreed.db.repository.RateRepository;
import com.gordan.motionpicturecreed.db.repository.ScreenShowRateRepository;
import com.gordan.motionpicturecreed.db.repository.ScreenShowRepository;
import com.gordan.motionpicturecreed.db.repository.SourceRepository;
import com.gordan.motionpicturecreed.model.Rate;
import com.gordan.motionpicturecreed.model.ScreenShow;
import com.gordan.motionpicturecreed.model.ScreenShowsPage;
import com.gordan.motionpicturecreed.model.Source;

@Service
@Transactional
public class ScreenShowService {
	
	@Value("${apiKey}")
	private String apiKey;
	
	@Value("${apiUrl}")
	private String apiUrl;
	
	@Autowired
	private OMDBService omdbService;

	@Autowired
	private ScreenShowRepository screenShowRepository;
	
	@Autowired
	private ScreenShowRateRepository screenShowRateRepository;
	
	@Autowired
	private SourceRepository sourceRepository;
	
	@Autowired
	private RateRepository rateRepository;
	
	private static final String NO_VALUE = "N/A";
	
	@Transactional
	public ScreenShow findAndInsertScreenShow(String title, String year) throws JsonMappingException, JsonProcessingException, ParseException {
		if(title == null) {
			return null;
		}

		ModelMapper modelMapper = new ModelMapper();
		var url = apiUrl.concat(title);
		ScreenShowRecord existingRecord;
		if(year != null) {
			url = url.concat("&y=").concat(year.toString());
			existingRecord = screenShowRepository.findByTitleAndYearAndValidTrue(title, year);
		}
		else {
			existingRecord = screenShowRepository.findByTitleAndValidTrue(title);
		}
		
		if(existingRecord != null) {
			return modelMapper.map(existingRecord, ScreenShow.class);
		}
		url = url.concat("&apikey=").concat(apiKey);
		String data = omdbService.getData(url);
		
		if(data.contains("Movie not found!")) {
			return null;
		}
		
		var rateList = createRateList(data);
		var screenShow = createScreenShowModel(data);
		existingRecord = screenShowRepository.findByTitleAndYearAndValidTrue(screenShow.getTitle(), screenShow.getYear());
		if(existingRecord != null) {
			return modelMapper.map(existingRecord, ScreenShow.class);
		}
		var screenShowRateList = createScreenShowRates(rateList, screenShow);
		
		screenShowRateRepository.saveAll(screenShowRateList);
		return modelMapper.map(screenShow, ScreenShow.class);
	}
	
	public ScreenShowsPage getAllScreenShows(String title, String year, String genre, String director, 
			String writer, String actors, String language, String country, String rated, String type, Integer pageNumber){
		ModelMapper modelMapper = new ModelMapper();
		pageNumber = pageNumber == null ? 1 : pageNumber;
		title = title == null ? "" : "%"+title.toLowerCase()+"%";
		year = year == null ? "" : year;
		genre = genre == null ? "" : "%"+genre.toLowerCase()+"%";
		director = director == null ? "" : "%"+director.toLowerCase()+"%";
		writer = writer == null ? "" : "%"+writer.toLowerCase()+"%";
		actors = actors == null ? "" : "%"+actors.toLowerCase()+"%";
		language = language == null ? "" : "%"+language.toLowerCase()+"%";
		country = country == null ? "" : "%"+country.toLowerCase()+"%";
		rated = rated == null ? "" : rated;
		type = type == null ? "" : type;
		var offset = (pageNumber - 1) * 10;
		var records = screenShowRepository.findAllScreenShows(title, year, genre, director, writer, actors, language, country, rated, type, offset);
		List<ScreenShow> screenShowList = modelMapper.map(records, new TypeToken<List<ScreenShow>>() {}.getType());
		
		for(var screenShow : screenShowList) {
			var rateRecords = rateRepository.findRatesforScreenShowId(screenShow.getId());
			List<Rate> rateList = modelMapper.map(rateRecords, new TypeToken<List<Rate>>() {}.getType());
			screenShow.setRates(rateList);
		}
		var count = screenShowRepository.getCount(title, year, genre, director, writer, actors, language, country, rated, type);
		ScreenShowsPage page = new ScreenShowsPage();
		page.setScreenShowList(screenShowList);
		page.setCurrentPage(pageNumber);
		page.setCount(count);
		return page;
	}
	
	private ScreenShowRecord createScreenShowModel(String data) throws JsonMappingException, JsonProcessingException, ParseException {
		ScreenShow screenShow = new ScreenShow();
		ObjectMapper mapper = new ObjectMapper();
		ModelMapper modelMapper = new ModelMapper();
		JsonNode jsonObject = mapper.readTree(data);
		
		screenShow.setTitle(parseToString(jsonObject.get("Title")));
		screenShow.setYear(parseToString(jsonObject.get("Year")));
		screenShow.setRated(parseToString(jsonObject.get("Rated")));
		var dateString = parseToString(jsonObject.get("Released"));
		screenShow.setReleased(dateString.equals(NO_VALUE) ? null : new SimpleDateFormat("dd MMM yyyy").parse(dateString.replace("\"", "")));
		screenShow.setGenre(parseToString(jsonObject.get("Genre")));
		screenShow.setDirector(parseToString(jsonObject.get("Director")));
		screenShow.setWriter(parseToString(jsonObject.get("Writer")));
		screenShow.setActors(parseToString(jsonObject.get("Actors")));
		screenShow.setPlot(parseToString(jsonObject.get("Plot")));
		screenShow.setLanguage(parseToString(jsonObject.get("Language")));
		screenShow.setCountry(parseToString(jsonObject.get("Country")));
		screenShow.setAwards(parseToString(jsonObject.get("Awards")));
		screenShow.setPoster(parseToString(jsonObject.get("Poster")));
		var runtime = parseToString(jsonObject.get("Runtime")).replace(" min", "");
		if(!runtime.equals("N/A")) {
			screenShow.setRuntime(Integer.parseInt(runtime));
		}
		String imdbVotes = parseToString(jsonObject.get("imdbVotes")).replace(",", "");
		screenShow.setImdbVotes(imdbVotes.equals(NO_VALUE) ? null : Integer.parseInt(imdbVotes));
		screenShow.setType(parseToString(jsonObject.get("Type")));
		String boxOffice = parseToString(jsonObject.get("BoxOffice")).replace("$", "").replace(",", "");
		screenShow.setBoxOffice(boxOffice.equals(NO_VALUE) ? null : Double.parseDouble(boxOffice));
		screenShow.setProduction(parseToString(jsonObject.get("Production")));
		screenShow.setValid(true);
		return modelMapper.map(screenShow, ScreenShowRecord.class);
	}
	
	private String parseToString(JsonNode node) {
		if(node == null) {
			return NO_VALUE;
		}
		return node.toString().replace("\"", "");
	}
	
	private List<RateRecord> createRateList(String data) throws JsonMappingException, JsonProcessingException{
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrayNode = (ArrayNode) mapper.readTree(data).get("Ratings");
		List<RateRecord> rateList = new ArrayList<RateRecord>();
		for(var i = 0; i < arrayNode.size(); i++) {
			var item = arrayNode.get(i);
			RateRecord rate = new RateRecord();
			
			var sourceString = parseToString(item.get("Source"));
			var source = createSource(sourceString);
			rate.setSource(source);
			rate.setValid(true);
			
			var value = parseToString(item.get("Value"));
			if(value.contains("/")) {
				var rateString = value.split("/")[0];
				var rateValue = rateString.contains(".") ? Double.parseDouble(rateString)*10 : Double.parseDouble(rateString);
				rate.setValue(rateValue);
			}
			else {
				rate.setValue(Double.parseDouble(value.split("%")[0]));
			}
			rateList.add(rate);
		}
		return rateList;
	}
	
	private List<ScreenShowRateRecord> createScreenShowRates(List<RateRecord> rates, ScreenShowRecord screenShow){
		List<ScreenShowRateRecord> screenShowRateList = new ArrayList<ScreenShowRateRecord>();
		for(var rate : rates) {
			ScreenShowRateRecord screenShowRate = new ScreenShowRateRecord();
			screenShowRate.setRate(rate);
			screenShowRate.setScreenShow(screenShow);
			screenShowRate.setValid(true);
			screenShowRateList.add(screenShowRate);
		}
		return screenShowRateList;
	}
	
	private SourceRecord createSource(String sourceString) {
		ModelMapper modelMapper = new ModelMapper();
		var existingRecord = sourceRepository.findByNameAndValidTrue(sourceString);
		if(existingRecord == null) {
			Source source = new Source();
			source.setName(sourceString);
			source.setValid(true);
			return modelMapper.map(source, SourceRecord.class);
		}
		return existingRecord;
	}
}
