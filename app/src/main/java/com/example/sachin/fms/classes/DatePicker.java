package com.example.sachin.fms.classes;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by Sachin on 24,September,2016
 * Hawkinski,
 * Dubai, UAE.
 */
public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    EditText textDate;

    @SuppressLint("ValidFragment")
    public DatePicker(View view) {
        super();
        textDate = (EditText) view;
    }

    public DatePicker() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);

    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        textDate.setText(date);
    }
}
