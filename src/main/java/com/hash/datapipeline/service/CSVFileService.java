package com.hash.datapipeline.service;

import com.hash.datapipeline.exception.FileException;
import com.hash.datapipeline.exception.OutlierDetectedException;
import com.hash.datapipeline.model.CSVRecord;
import com.hash.datapipeline.repository.CSVFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CSVFileService {

    private static final Logger logger = LoggerFactory.getLogger(CSVFileService.class);
    @Autowired
    private CSVFileRepository csvFileRepository;

    public List<String> processFile(MultipartFile file) throws Exception {
        logger.info("Processing file: {}", file.getOriginalFilename());
        if (file.isEmpty()) {
            logger.error("File is empty");
            throw new FileException("File is empty");
        }

        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            lines = reader.lines().collect(Collectors.toList());
        } catch (Exception ex) {
            logger.error("Error reading file", ex);
            throw new FileException("Error reading file", ex);
        }

        validateCSVFormat(lines);
        if (detectOutliers(lines)) {
            logger.error("Data contains outliers");
            throw new OutlierDetectedException("Data contains outliers.");
        }
        return lines;
    }

    private void validateCSVFormat(List<String> lines) throws Exception {
        logger.debug("Validating CSV format");
        if (lines.isEmpty()) {
            logger.error("CSV file is empty");
            throw new FileException("CSV file is empty");
        }

        String header = lines.get(0);
        String[] columns = header.split(",");

        if (columns.length != 3 || !columns[0].equals("timestamp") || !columns[1].equals("value") || !columns[2].equals("category")) {
            logger.error("Invalid CSV format");
            throw new FileException("Invalid CSV format");
        }
    }

    public boolean detectOutliers(List<String> lines) throws Exception {
        logger.debug("Detecting outliers");
        List<Integer> values = lines.stream().skip(1)
                .map(line -> line.split(",")[1])
                .map(Integer::parseInt)
                .toList();

        if (values.isEmpty()) {
            logger.error("No values to process for outlier detection");
            throw new OutlierDetectedException("No values to process for outlier detection");
        }

        double mean = values.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        double variance = values.stream()
                .mapToDouble(value -> Math.pow(value - mean, 2))
                .average()
                .orElse(0.0);

        double stdDev = Math.sqrt(variance);

        double lowerBound = mean - 2 * stdDev;
        double upperBound = mean + 2 * stdDev;
        logger.debug("Mean: {}, StdDev: {}, LowerBound: {}, UpperBound: {}", mean, stdDev, lowerBound, upperBound);

        boolean hasOutliers = values.stream().anyMatch(value -> value < lowerBound || value > upperBound);
        if (hasOutliers) {
            logger.warn("Outlier detected");
        }
        return hasOutliers;
    }

    public void saveData(List<String> lines) {
        logger.debug("Saving data to database");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<CSVRecord> records = lines.stream().skip(1).map(line -> {
            String[] parts = line.split(",");
            CSVRecord record = new CSVRecord();
            record.setTimestamp(LocalDateTime.parse(parts[0], formatter));
            record.setValue(Integer.parseInt(parts[1]));
            record.setCategory(parts[2]);
            return record;
        }).collect(Collectors.toList());

        csvFileRepository.saveAll(records);
        logger.info("Data successfully saved to database");
    }
}
