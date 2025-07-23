package com.company.weatherreporter.service;

import com.company.weatherreporter.entity.Location;
import com.company.weatherreporter.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationDataService {
    
    private final LocationRepository locationRepository;
    private final ExcelDataService excelDataService;
    
    @Transactional
    public void importLocationDataFromExcel(String filePath) throws IOException {
        log.info("Excel 파일에서 위치 데이터 가져오기 시작: {}", filePath);
        
        List<Location> locations = excelDataService.readLocationDataFromExcel(filePath);
        
        log.info("총 {} 개의 위치 데이터를 읽었습니다.", locations.size());
        
        // 기존 데이터 삭제 (필요한 경우)
        // locationRepository.deleteAll();
        
        // 배치로 저장
        locationRepository.saveAll(locations);
        
        log.info("위치 데이터 저장 완료");
    }
    
    public List<Location> searchLocations(String keyword) {
        return locationRepository.searchByKeyword(keyword);
    }
    
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }
}