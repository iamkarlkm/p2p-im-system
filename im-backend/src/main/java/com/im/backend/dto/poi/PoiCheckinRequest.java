package com.im.backend.dto.poi;

/**
 * POI签到请求DTO
 */
public class PoiCheckinRequest {
    
    private String poiId;
    private String poiName;
    private String poiAddress;
    private Double longitude;
    private Double latitude;
    private String checkinContent;
    private String imageUrls;
    private Boolean isPublic = true;
    private String checkinType;
    private String deviceFingerprint;

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

    public String getCheckinContent() { return checkinContent; }
    public void setCheckinContent(String checkinContent) { this.checkinContent = checkinContent; }

    public String getImageUrls() { return imageUrls; }
    public void setImageUrls(String imageUrls) { this.imageUrls = imageUrls; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public String getCheckinType() { return checkinType; }
    public void setCheckinType(String checkinType) { this.checkinType = checkinType; }

    public String getDeviceFingerprint() { return deviceFingerprint; }
    public void setDeviceFingerprint(String deviceFingerprint) { this.deviceFingerprint = deviceFingerprint; }
}
