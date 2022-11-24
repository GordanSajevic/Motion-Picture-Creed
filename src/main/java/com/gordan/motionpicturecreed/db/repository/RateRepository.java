package com.gordan.motionpicturecreed.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gordan.motionpicturecreed.db.record.RateRecord;

public interface RateRepository extends JpaRepository<RateRecord, Integer> {
	
	@Query(value = "select r.id, r.value, r.valid, r.created_date, "
			+ "r.modified_date, r.source_id from rates as r\r\n"
			+ "join screen_show_rates as ssr on r.id = ssr.rate_id\r\n"
			+ "where ssr.screen_show_id = ?1", nativeQuery = true)
	public List<RateRecord> findRatesforScreenShowId(Integer screenShowId);
}
