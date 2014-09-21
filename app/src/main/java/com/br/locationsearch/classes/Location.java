package com.br.locationsearch.classes;

import java.io.Serializable;

/**
 * Created by BRUNO on 19/09/2014.
 *
 * Serializable concrete class Location
 *
 */
public class Location implements Serializable {

    private String formattedAddress;
    private double lat;
    private double lng;

    @Override
    public String toString(){
        return this.formattedAddress;
    }

    public String getFormattedAddress(){
        return this.formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress){
        this.formattedAddress = formattedAddress;
    }

    public double getLat(){
        return this.lat;
    }

    public void setLat(double lat){
        this.lat = lat;
    }

    public double getLng(){
        return this.lng;
    }

    public void setLng(double lng){
        this.lng = lng;
    }

}
