package com.example.sachin.fms.dataSets;

/**
 * Created by Sachin on 24,September,2016
 * Hawkinski,
 * Dubai, UAE.
 */
public class TaskListData {

    public String task_id, reported_dt, call_no, date, location, des, priority, time, building, datetime, color, priority_code, status_code, ppm_no;

    public String location_code, building_code;

    public TaskListData() {

    }

    public TaskListData(String task_id, String call_no, String date, String location, String des, String priority, String time, String building, String color) {

        this.task_id = task_id;
        this.call_no = call_no;
        this.date = date;
        this.location = location;
        this.des = des;
        this.priority = priority;
        this.time = time;
        this.building = building;
        this.color = color;


    }

    public TaskListData(String status_code, String priority_code) {


        this.status_code = status_code;
        this.priority_code = priority_code;


    }

    public TaskListData(String task_id, String call_no, String date, String location, String des, String priority, String time, String building, String status_code, String priority_code, String ppm_no, String reported_dt, String location_code, String building_code) {

        this.task_id = task_id;
        this.call_no = call_no;
        this.date = date;
        this.location = location;
        this.des = des;
        this.priority = priority;
        this.time = time;
        this.building = building;
        this.status_code = status_code;
        this.priority_code = priority_code;
        this.ppm_no = ppm_no;
        this.reported_dt = reported_dt;
        this.location_code = location_code;
        this.building_code = building_code;


    }
}
