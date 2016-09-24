package com.example.sachin.fms.dataSets;

/**
 * Created by Sachin on 24,September,2016
 * Hawkinski,
 * Dubai, UAE.
 */
public class MaterialData {
    public String product_code, product_des, uom, quantity;
    public int count;


    public MaterialData() {

    }

    public MaterialData(String product_code, String product_des, String uom, String quantity, int count) {

        this.product_code = product_code;
        this.product_des = product_des;
        this.count = count;
        this.quantity = quantity;
        this.uom = uom;
    }
}
