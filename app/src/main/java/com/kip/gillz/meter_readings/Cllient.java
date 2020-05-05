package com.kip.gillz.meter_readings;


public class Cllient {

    private String fname;
    private String meterno;


    public Cllient(String fname,String meterno) {
        this.fname = fname;
        this.meterno = meterno;

    }

    

    public String getFname() {
        return fname;
    }

    public String getMeterno() {
        return meterno;
    }


}
