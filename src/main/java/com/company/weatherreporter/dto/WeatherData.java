package com.company.weatherreporter.dto;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherData {
    private String regionName;       // 지역명
    private Double temperature;      // 기온
    private Double humidity;         // 습도
    private Double windSpeed;        // 풍속
    private Double rainProbability;  // 강수확률
    private Double rainfall;         // 강수량 (mm)
    private String rainfallText;     // 강수량 텍스트 (예 : 1mm 미만, 30.0 ~ 50.0mm)
    private Integer weatherCode;     // 날씨 코드 (1:맑음, 3:구름많음, 4:흐림, 5:비, 6:비/눈, 7:눈)

}
