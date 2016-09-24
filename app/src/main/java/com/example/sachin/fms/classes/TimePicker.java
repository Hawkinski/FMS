package com.example.sachin.fms.classes;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by Sachin on 24,September,2016
 * Hawkinski,
 * Dubai, UAE.
 */
public class TimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    EditText textTime;

    @SuppressLint("ValidFragment")
    public TimePicker(View view) {
        super();
        textTime = (EditText) view;
    }

    public TimePicker() {

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int hr = c.get(Calendar.HOUR);
        int mm = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hr, mm, false);

    }


    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {

        String time = hourOfDay + ":" + minute;
        textTime.setText(time);
    }
}
