package com.im.backend.dto.poi;

/**
 * 热门POI DTO
 */
public class HotPoiDTO {
    
    private String poiId;
    private String poiName;
    private String poiAddress;
    private Double longitude;
    private Double latitude;
    private Integer checkinCount;
    private Double distance;

    // Getters and Setters
    public String getPoiId() { return poiId; }
    public void setPoiId(String poiId) { this.poiId = poiId; }

    public String getPoiName() { return poiName; }
    public void setPoiName(String poiName) { this.poiName = poiName; }

    public String getPoiAddress() { return poiAddress; }
    public void setPoiAddress(String poiAddress) { this.poiAddress = poiAddress; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Integer getCheckinCount() { return checkinCount; }
    public void setCheckinCount(Integer checkinCount) { this.checkinCount = checkinCount; }

    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
}
