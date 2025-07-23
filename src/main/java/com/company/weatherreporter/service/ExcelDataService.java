package com.company.weatherreporter.service;

import com.company.weatherreporter.entity.Location;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelDataService {

    static {
        ZipSecureFile.setMinInflateRatio(0.001);
    }

    public List<Location> readLocationDataFromExcel(String filePath) throws IOException {
        List<Location> locations = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            // 헤더 행 건너뛰기
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                String level1 = getCellStringValue(row.getCell(2));
                String level2 = getCellStringValue(row.getCell(3));
                String level3 = getCellStringValue(row.getCell(4));
                
                String fullRegionName = level1;
                if (!level2.isEmpty()) {
                    fullRegionName += " " + level2;
                }
                if (!level3.isEmpty()) {
                    fullRegionName += " " + level3;
                }
                
                Location location = Location.builder()
                    .category(getCellStringValue(row.getCell(0)))
                    .adminCode(getCellStringValue(row.getCell(1)))
                    .regionName(fullRegionName.trim())
                    .level1(level1)
                    .level2(level2)
                    .level3(level3)
                    .gridX((int) getCellNumericValue(row.getCell(5)))
                    .gridY((int) getCellNumericValue(row.getCell(6)))
                    .longitude(convertToDecimalDegrees(
                        getCellNumericValue(row.getCell(7)), // 경도(시)
                        getCellNumericValue(row.getCell(8)), // 경도(분)
                        getCellNumericValue(row.getCell(9))  // 경도(초)
                    ))
                    .latitude(convertToDecimalDegrees(
                        getCellNumericValue(row.getCell(10)), // 위도(시)
                        getCellNumericValue(row.getCell(11)), // 위도(분)
                        getCellNumericValue(row.getCell(12))  // 위도(초)
                    ))
                    .build();
                
                locations.add(location);
            }
        }
        
        return locations;
    }
    
    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        return cell.getCellType() == CellType.STRING ? 
            cell.getStringCellValue() : String.valueOf(cell.getNumericCellValue());
    }
    
    private double getCellNumericValue(Cell cell) {
        if (cell == null) return 0;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            String stringValue = cell.getStringCellValue().trim();
            if (stringValue.isEmpty()) {
                return 0;
            }
            try {
                return Double.parseDouble(stringValue);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    private double convertToDecimalDegrees(double degrees, double minutes, double seconds) {
        return degrees + (minutes / 60.0) + (seconds / 3600.0);
    }
}