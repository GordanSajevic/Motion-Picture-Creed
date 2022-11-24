package com.gordan.motionpicturecreed.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gordan.motionpicturecreed.db.record.ScreenShowRecord;

public interface ScreenShowRepository extends JpaRepository<ScreenShowRecord, Integer>{

	@Query(value = "SELECT * FROM public.screen_shows WHERE lower(title) = lower(?1) and valid = true", nativeQuery = true)
	public ScreenShowRecord findByTitleAndValidTrue(String title);
	
	@Query(value = "SELECT * FROM public.screen_shows WHERE lower(title) = lower(?1) and year = ?2 and valid = true", nativeQuery = true)
	public ScreenShowRecord findByTitleAndYearAndValidTrue(String title, String year);
	
	@Query(value = "SELECT distinct(ss.id), title, year, genre, director, writer, actors, language, awards, "
			+ "country, rated, type, box_office, imdb_votes, plot, poster, released, runtime, "
			+ "production, ss.created_date, ss.modified_date, ss.valid, "
			+ "((select sum(value) from rates r2\r\n"
			+ "	join screen_show_rates ssr2 on ssr2.rate_id = r2.id\r\n"
			+ "  where ssr2.screen_show_id = ss.id) \r\n"
			+ " / \r\n"
			+ " (select count(*) from rates r3\r\n"
			+ "	join screen_show_rates ssr3 on ssr3.rate_id = r3.id\r\n"
			+ "  where ssr3.screen_show_id = ss.id) ) as averateRate \r\n"
			+ "FROM public.screen_shows ss \r\n"
			+ "join screen_show_rates ssr on ssr.screen_show_id = ss.id\r\n"
			+ "join rates r on ssr.rate_id = r.id \r\n"
			+ "WHERE "
			+ "(?1 = '' OR lower(title) LIKE ?1) AND (?2 = '' OR year = ?2) "
			+ "AND (?3 = '' OR lower(genre) LIKE ?3) AND (?4 = '' OR lower(director) LIKE ?4) "
			+ "AND (?5 = '' OR lower(writer) LIKE ?5) AND (?6 = '' OR lower(actors) LIKE ?6) "
			+ "AND (?7 = '' OR lower(language) LIKE ?7) AND (?8 = '' OR lower(country) LIKE ?8) "
			+ "AND (?9 = '' OR rated = ?9) AND (?10 = '' OR type = ?10) AND ss.valid = true "
			+ "ORDER BY averateRate DESC "
			+ "OFFSET ?11 ROWS \r\n"
			+ "FETCH FIRST 10 ROW ONLY",
			nativeQuery = true)
	public List<ScreenShowRecord> findAllScreenShows(String title, String year, String genre, String director, 
			String writer, String actors, String language, String country, String rated, String type, 
			Integer offset);
	
	@Query(value = "SELECT count(*) \r\n"
			+ "FROM public.screen_shows ss \r\n"
			+ "WHERE "
			+ "(?1 = '' OR lower(title) LIKE ?1) AND (?2 = '' OR year = ?2) "
			+ "AND (?3 = '' OR lower(genre) LIKE ?3) AND (?4 = '' OR lower(director) LIKE ?4) "
			+ "AND (?5 = '' OR lower(writer) LIKE ?5) AND (?6 = '' OR lower(actors) LIKE ?6) "
			+ "AND (?7 = '' OR lower(language) LIKE ?7) AND (?8 = '' OR lower(country) LIKE ?8) "
			+ "AND (?9 = '' OR rated = ?9) AND (?10 = '' OR type = ?10) AND ss.valid = true "
			, nativeQuery = true)
	public Integer getCount(String title, String year, String genre, String director, 
			String writer, String actors, String language, String country, String rated, String type);
}
