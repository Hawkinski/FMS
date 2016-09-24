package com.example.sachin.fms.dataSets;

/**
 * Created by Sachin on 24,September,2016
 * Hawkinski,
 * Dubai, UAE.
 */
public class AddedData {

    public String code;
    public String quantity;
    public int ib;
    public String des, uom;

    public AddedData(String code, String quantity, int ib, String des, String uom) {

        this.code = code;
        this.ib = ib;
        this.quantity = quantity;
        this.uom = uom;
        this.des = des;
    }
}
