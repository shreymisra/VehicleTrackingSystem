package edu.kiet.www.blackbox.Activity;

/**
 * Created by shrey on 5/8/17.
 */

public class BusIds {
    public  String bus_id,bus_name;

    public BusIds(){

    }

    public  BusIds(String id,String number)
    {
        bus_id=id;
        bus_name=number;
    }

    public void setBus_id(String bus_id) {
        this.bus_id = bus_id;
    }

    public String getBus_id() {
        return bus_id;
    }

    public void setBus_name(String bus_name) {
        this.bus_name = bus_name;
    }

    public String getBus_name() {
        return bus_name;
    }

}
