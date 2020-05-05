package com.kip.gillz.meter_readings;


public class Mreadings {

    private String vaccine;
    private String facility;
    private String aday;
    private int price;


    public Mreadings(String vaccine, String facility, String aday, int price) {
        this.vaccine = vaccine;
        this.facility = facility;
        this.aday= aday;
        this.price = price;

    }

    

    public String getTitle() {
        return vaccine;
    }

    public String getRating() {
        return facility;
    }

    public String getAday() {
        return aday;
    }

    public int getPrice() {
        return price;
    }


}
