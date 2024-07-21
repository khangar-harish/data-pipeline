package com.hash.datapipeline.repository;

import com.hash.datapipeline.model.CSVRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CSVFileRepository extends JpaRepository<CSVRecord, Long> {
}
