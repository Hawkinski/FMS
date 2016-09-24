package com.example.sachin.fms.dataSets;

import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by Sachin on 24,September,2016
 * Hawkinski,
 * Dubai, UAE.
 */
public class employee {
    public String emp_name;

    public String cd;
    public String pwd;
    public String mail;
    public String emp_code;

    public employee() {
    }


    public employee(String name, String cd, String pwd, String mail, String emp_code) {

        this.emp_name = name;
        this.cd = cd;
        this.pwd = pwd;
        this.mail = mail;
        this.emp_code = emp_code;
    }


    public Object getProperty(int arg0) {

        switch (arg0) {
            case 0:
                return emp_name;
            case 1:
                return cd;
            case 2:
                return pwd;
            case 3:
                return mail;
            case 4:
                return emp_code;

        }

        return null;
    }

    public int getPropertyCount() {
        return 5;
    }

    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch (index) {
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "emp_name";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "cd";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "pwd";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "mail";
                break;

            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "emp_code";
                break;
            default:
                break;
        }
    }

    public void setProperty(int index, Object value) {
        switch (index) {
            case 0:
                emp_name = value.toString();
                break;
            case 1:
                cd = value.toString();
                break;
            case 2:
                pwd = value.toString();
                break;
            case 3:
                mail = value.toString();
                break;
            case 4:
                emp_code = value.toString();
            default:
                break;
        }
    }
}