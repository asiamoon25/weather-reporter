package com.company.weatherreporter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExcelAnalyzer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        analyzeExcelFile("LatLonList.xlsx");
    }

    public void analyzeExcelFile(String fileName) {
        try (FileInputStream fis = new FileInputStream(fileName);
             Workbook workbook = new XSSFWorkbook(fis)) {

            System.out.println("=== Excel 파일 분석 결과 ===");
            System.out.println("파일명: " + fileName);
            System.out.println("워크시트 개수: " + workbook.getNumberOfSheets());

            // 첫 번째 시트 분석
            Sheet sheet = workbook.getSheetAt(0);
            System.out.println("시트명: " + sheet.getSheetName());

            int totalRows = sheet.getPhysicalNumberOfRows();
            System.out.println("총 행 수: " + totalRows);

            if (totalRows == 0) {
                System.out.println("시트가 비어있습니다.");
                return;
            }

            // 첫 번째 행(헤더)을 읽어서 열 정보 분석
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                System.out.println("헤더 행이 없습니다.");
                return;
            }

            int totalColumns = headerRow.getPhysicalNumberOfCells();
            System.out.println("총 열 수: " + totalColumns);

            System.out.println("\n=== 열 정보 ===");
            List<String> columnNames = new ArrayList<>();
            for (int col = 0; col < totalColumns; col++) {
                Cell cell = headerRow.getCell(col);
                String columnName = getCellValueAsString(cell);
                columnNames.add(columnName);
                System.out.println("열 " + (col + 1) + ": " + columnName);
            }

            // 데이터 타입 분석을 위해 첫 몇 행 샘플 데이터 확인
            System.out.println("\n=== 각 열의 데이터 타입 및 샘플 데이터 ===");
            for (int col = 0; col < totalColumns; col++) {
                System.out.println("\n열 " + (col + 1) + " (" + columnNames.get(col) + "):");
                analyzeColumnData(sheet, col, Math.min(10, totalRows - 1)); // 최대 10개 샘플
            }

            // 전체 데이터 미리보기 (처음 5행)
            System.out.println("\n=== 데이터 미리보기 (처음 5행) ===");
            int previewRows = Math.min(6, totalRows); // 헤더 + 5행
            for (int row = 0; row < previewRows; row++) {
                Row currentRow = sheet.getRow(row);
                if (currentRow != null) {
                    StringBuilder rowData = new StringBuilder();
                    rowData.append("행 ").append(row + 1).append(": ");
                    
                    for (int col = 0; col < totalColumns; col++) {
                        Cell cell = currentRow.getCell(col);
                        String cellValue = getCellValueAsString(cell);
                        rowData.append(cellValue);
                        if (col < totalColumns - 1) {
                            rowData.append(" | ");
                        }
                    }
                    System.out.println(rowData.toString());
                }
            }

        } catch (IOException e) {
            System.err.println("파일을 읽는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void analyzeColumnData(Sheet sheet, int columnIndex, int sampleSize) {
        List<CellType> cellTypes = new ArrayList<>();
        List<String> sampleValues = new ArrayList<>();
        
        for (int row = 1; row <= sampleSize && row < sheet.getPhysicalNumberOfRows(); row++) {
            Row currentRow = sheet.getRow(row);
            if (currentRow != null) {
                Cell cell = currentRow.getCell(columnIndex);
                if (cell != null) {
                    cellTypes.add(cell.getCellType());
                    sampleValues.add(getCellValueAsString(cell));
                }
            }
        }

        // 가장 많이 나타나는 데이터 타입 결정
        CellType dominantType = findDominantCellType(cellTypes);
        System.out.println("  예상 데이터 타입: " + getDataTypeDescription(dominantType));
        
        System.out.println("  샘플 데이터 (" + sampleValues.size() + "개):");
        for (int i = 0; i < Math.min(5, sampleValues.size()); i++) {
            System.out.println("    - " + sampleValues.get(i));
        }
    }

    private CellType findDominantCellType(List<CellType> cellTypes) {
        if (cellTypes.isEmpty()) return CellType.BLANK;
        
        // 빈도수 계산
        int numericCount = 0, stringCount = 0, booleanCount = 0;
        
        for (CellType type : cellTypes) {
            switch (type) {
                case NUMERIC:
                    numericCount++;
                    break;
                case STRING:
                    stringCount++;
                    break;
                case BOOLEAN:
                    booleanCount++;
                    break;
            }
        }
        
        // 가장 많은 타입 반환
        if (numericCount >= stringCount && numericCount >= booleanCount) {
            return CellType.NUMERIC;
        } else if (stringCount >= booleanCount) {
            return CellType.STRING;
        } else {
            return CellType.BOOLEAN;
        }
    }

    private String getDataTypeDescription(CellType cellType) {
        switch (cellType) {
            case NUMERIC:
                return "숫자 (Numeric)";
            case STRING:
                return "텍스트 (String)";
            case BOOLEAN:
                return "논리값 (Boolean)";
            case FORMULA:
                return "수식 (Formula)";
            case BLANK:
                return "빈 셀 (Blank)";
            case ERROR:
                return "에러 (Error)";
            default:
                return "알 수 없음";
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    // 정수인지 확인
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            case ERROR:
                return "ERROR";
            default:
                return "";
        }
    }
}