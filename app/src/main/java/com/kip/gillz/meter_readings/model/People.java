package com.kip.gillz.meter_readings.model;

import android.graphics.drawable.Drawable;

public class People {

    public int image;
    public Drawable imageDrw;
    public String name;
    public String meterno;
    public String email;
    public boolean section = false;

    public People() {
    }
    public People(String name,String meterno, boolean section) {
        this.name = name;
        this.meterno = meterno;
        this.section = section;
    }

}
