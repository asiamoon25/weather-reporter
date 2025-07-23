package com.company.weatherreporter.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherApiResponse {
    private Response response;

    public Response getResponse() { return response; }
    public void setResponse(Response response) { this.response = response; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private Header header;
        private Body body;

        public Header getHeader() { return header; }
        public void setHeader(Header header) { this.header = header; }
        public Body getBody() { return body; }
        public void setBody(Body body) { this.body = body; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Header {
        private String resultCode;
        private String resultMsg;

        public String getResultCode() { return resultCode; }
        public void setResultCode(String resultCode) { this.resultCode = resultCode; }
        public String getResultMsg() { return resultMsg; }
        public void setResultMsg(String resultMsg) { this.resultMsg = resultMsg; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        private String numOfRows;
        private String pageNo;
        private String totalCount;
        private String dataType;
        private Items items;

        public String getNumOfRows() { return numOfRows; }
        public void setNumOfRows(String numOfRows) { this.numOfRows = numOfRows; }
        public String getPageNo() { return pageNo; }
        public void setPageNo(String pageNo) { this.pageNo = pageNo; }
        public String getTotalCount() { return totalCount; }
        public void setTotalCount(String totalCount) { this.totalCount = totalCount; }
        public String getDataType() { return dataType; }
        public void setDataType(String dataType) { this.dataType = dataType; }
        public Items getItems() { return items; }
        public void setItems(Items items) { this.items = items; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        private List<Item> item;

        public List<Item> getItem() { return item; }
        public void setItem(List<Item> item) { this.item = item; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String baseDate;      // 발표일자
        private String baseTime;      // 발표시각
        private String nx;            // 예보지점 X 좌표
        private String ny;            // 예보지점 Y 좌표
        private String category;      // 자료구분코드
        private String obsrValue;     // 실황 값
        private String fcstDate;      // 예측일자 (예보에만 있음)
        private String fcstTime;      // 예측시간 (예보에만 있음)
        private String fcstValue;     // 예보 값 (예보에만 있음)

        // getters and setters
        public String getBaseDate() { return baseDate; }
        public void setBaseDate(String baseDate) { this.baseDate = baseDate; }
        public String getBaseTime() { return baseTime; }
        public void setBaseTime(String baseTime) { this.baseTime = baseTime; }
        public String getNx() { return nx; }
        public void setNx(String nx) { this.nx = nx; }
        public String getNy() { return ny; }
        public void setNy(String ny) { this.ny = ny; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getObsrValue() { return obsrValue; }
        public void setObsrValue(String obsrValue) { this.obsrValue = obsrValue; }
        public String getFcstDate() { return fcstDate; }
        public void setFcstDate(String fcstDate) { this.fcstDate = fcstDate; }
        public String getFcstTime() { return fcstTime; }
        public void setFcstTime(String fcstTime) { this.fcstTime = fcstTime; }
        public String getFcstValue() { return fcstValue; }
        public void setFcstValue(String fcstValue) { this.fcstValue = fcstValue; }
    }
}
