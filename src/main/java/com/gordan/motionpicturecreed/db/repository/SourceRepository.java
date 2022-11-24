package com.gordan.motionpicturecreed.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gordan.motionpicturecreed.db.record.SourceRecord;

public interface SourceRepository extends JpaRepository<SourceRecord, Integer> {
	public SourceRecord findByNameAndValidTrue(String name);
}
