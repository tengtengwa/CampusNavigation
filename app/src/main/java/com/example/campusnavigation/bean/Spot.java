package com.example.campusnavigation.bean;

/**
 * 景点JavaBean
 */
public class Spot {

    /**
     * 景点编号
     */
    private Integer spotId;
    /**
     * 景点名称
     */
    private String spotName;
    /**
     * 景点横坐标
     */
    private Double coordX;
    /**
     * 景点纵坐标
     */
    private Double coordY;
    /**
     * 景点信息
     */
    private Coord[] coords;

    private String spotInfo;

    public Spot() {
    }

    public Integer getSpotId() {
        return spotId;
    }

    public void setSpotId(Integer spotId) {
        this.spotId = spotId;
    }

    public String getSpotName() {
        return spotName;
    }

    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }

    public Double getCoordX() {
        return coordX;
    }

    public void setCoordX(Double coordX) {
        this.coordX = coordX;
    }

    public Double getCoordY() {
        return coordY;
    }

    public void setCoordY(Double coordY) {
        this.coordY = coordY;
    }

    public String getSpotInfo() {
        return spotInfo;
    }

    public void setSpotInfo(String spotInfo) {
        this.spotInfo = spotInfo;
    }
}
