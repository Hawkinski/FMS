package com.example.sachin.fms.dataSets;

/**
 * Created by Sachin on 24,September,2016
 * Hawkinski,
 * Dubai, UAE.
 */
public class SavedFilterData {

    public String checked;
    public String code;
    public String type;
    private String desc;

    public SavedFilterData() {

    }

    public SavedFilterData(String code, String desc, String type, String checked) {
        this.code = code;
        this.type = type;
        this.desc = desc;
        this.checked = checked;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;

    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getChecked() {
        return checked;
    }


    public void setChecked(String checked) {
        this.checked = checked;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
