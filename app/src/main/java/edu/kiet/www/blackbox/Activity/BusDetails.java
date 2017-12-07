package edu.kiet.www.blackbox.Activity;

/**
 * Created by shrey on 6/8/17.
 */

public class BusDetails {
    String latitude,longitude,bus_speed,bus_id,alcoholic_state;
    BusDetails(){}

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getBus_speed() {
        return bus_speed;
    }

    public void setBus_speed(String bus_speed) {
        this.bus_speed = bus_speed;
    }

    public String getBus_id() {
        return bus_id;
    }

    public void setBus_id(String bus_id) {
        this.bus_id = bus_id;
    }

    public String getAlcoholic_state() {
        return alcoholic_state;
    }

    public void setAlcoholic_state(String alcoholic_state) {
        this.alcoholic_state = alcoholic_state;
    }

    BusDetails(String latitude, String longitude, String bus_speed, String bus_id, String alcoholic_state)
    {
        this.latitude=latitude;
        this.longitude=longitude;
        this.bus_speed=bus_speed;
        this.bus_id=bus_id;
        this.alcoholic_state=alcoholic_state;
    }

}
