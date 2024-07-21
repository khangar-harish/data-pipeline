package com.hash.datapipeline.service.unit;

import com.hash.datapipeline.exception.OutlierDetectedException;
import com.hash.datapipeline.repository.CSVFileRepository;
import com.hash.datapipeline.service.CSVFileService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class CSVFileServiceUnitTest {

    @Mock
    private CSVFileRepository csvFileRepository;

    @InjectMocks
    private CSVFileService csvFileService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDetectOutliers_NoOutliers() throws Exception {
        List<String> lines = List.of(
                "timestamp,value,category",
                "2022-01-01 00:00:00,10,A",
                "2022-01-01 01:00:00,12,B",
                "2022-01-01 02:00:00,11,C",
                "2022-01-01 03:00:00,13,A",
                "2022-01-01 04:00:00,9,B",
                "2022-01-01 05:00:00,10,C",
                "2022-01-01 06:00:00,12,A",
                "2022-01-01 07:00:00,11,B",
                "2022-01-01 08:00:00,13,C",
                "2022-01-01 09:00:00,10,A"
        );

        assertFalse(csvFileService.detectOutliers(lines));
    }

    @Test
    public void testDetectOutliers_OutlierDetected() throws Exception {
        List<String> lines = List.of(
                "timestamp,value,category",
                "2022-01-01 00:00:00,10,A",
                "2022-01-01 01:00:00,12,B",
                "2022-01-01 02:00:00,11,C",
                "2022-01-01 03:00:00,13,A",
                "2022-01-01 04:00:00,9,B",
                "2022-01-01 05:00:00,10,C",
                "2022-01-01 06:00:00,12,A",
                "2022-01-01 07:00:00,11,B",
                "2022-01-01 08:00:00,100,C",
                "2022-01-01 09:00:00,10,A"
        );

        assertTrue(csvFileService.detectOutliers(lines));
    }

    @Test
    public void testProcessFile_FileContainsOutlier() {
        List<String> lines = List.of(
                "timestamp,value,category",
                "2022-01-01 00:00:00,10,A",
                "2022-01-01 01:00:00,12,B",
                "2022-01-01 02:00:00,11,C",
                "2022-01-01 03:00:00,13,A",
                "2022-01-01 04:00:00,9,B",
                "2022-01-01 05:00:00,10,C",
                "2022-01-01 06:00:00,12,A",
                "2022-01-01 07:00:00,11,B",
                "2022-01-01 08:00:00,100,C",
                "2022-01-01 09:00:00,10,A"
        );

        String content = String.join("\n", lines);
        MultipartFile file = new MockMultipartFile("file", "example.csv", "text/csv", content.getBytes(StandardCharsets.UTF_8));

        Exception exception = assertThrows(OutlierDetectedException.class, () -> {
            csvFileService.processFile(file);
        });

        assertEquals("Data contains outliers.", exception.getMessage());
    }


    @Test
    public void testProcessFile_FileContainsNoOutlier() {
        List<String> lines = Arrays.asList(
                "timestamp,value,category",
                "2022-01-01 00:00:00,10,A",
                "2022-01-01 01:00:00,12,B",
                "2022-01-01 02:00:00,11,C",
                "2022-01-01 03:00:00,13,A",
                "2022-01-01 04:00:00,9,B",
                "2022-01-01 05:00:00,10,C",
                "2022-01-01 06:00:00,12,A",
                "2022-01-01 07:00:00,11,B",
                "2022-01-01 08:00:00,13,C",
                "2022-01-01 09:00:00,10,A"
        );

        String content = String.join("\n", lines);
        MultipartFile file = new MockMultipartFile("file", "example.csv", "text/csv", content.getBytes(StandardCharsets.UTF_8));

        Assertions.assertDoesNotThrow(() -> csvFileService.processFile(file));

    }
}
