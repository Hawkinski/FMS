package com.example.sachin.fms.classes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.sachin.fms.R;
import com.example.sachin.fms.activities.LoginActivity;
import com.example.sachin.fms.others.WebServiceConnection;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by Sachin on 24,September,2016
 * Hawkinski,
 * Dubai, UAE.
 */
public class Logout extends AsyncTask<String, String, String> {

    Context context;
    SharedPreferences sp;
    private String NameSpace;
    private String Soap_Action;
    private String URL;
    private WebServiceConnection connection;
    private SoapSerializationEnvelope envelope;
    private SoapObject request;
    private SoapPrimitive response;
    private HttpTransportSE transportSE;
    private String z = "";
    private boolean isSuccess = false;

    public Logout(Context context, SharedPreferences sp) {
        this.context = context;
        this.sp = sp;


    }

    public void onPreExecute() {


    }

    public void onPostExecute(String s) {


        if (isSuccess) {

            SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.preferences), Context.MODE_APPEND);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.apply();

            Intent i = new Intent(context, LoginActivity.class);
            i.putExtra("finish", true); // if you are checking for this in your other Activities
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);


        } else {

            Toast.makeText(context, "Internal error! Please contact Horizon", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected String doInBackground(String... params) {
        connection = new WebServiceConnection();
        NameSpace = connection.NameSpace;
        Soap_Action = connection.Soap_Action;
        URL = connection.URL;

        PropertyInfo empcode, compcode, flag;

        request = new SoapObject(NameSpace, "notificationLogout");

        empcode = new PropertyInfo();
        empcode.setName("empcode");
        empcode.setType(String.class);
        empcode.setValue(sp.getString(context.getString(R.string.employee_code), ""));
        request.addProperty(empcode);


        compcode = new PropertyInfo();
        compcode.setName("compcode");
        compcode.setType(String.class);
        compcode.setValue(sp.getString(context.getString(R.string.company_code), ""));
        request.addProperty(compcode);

        flag = new PropertyInfo();
        flag.setName("flag");
        flag.setType(String.class);
        flag.setValue("0");
        request.addProperty(flag);

        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        transportSE = new HttpTransportSE(URL);
        transportSE.debug = true;

        try {
            // Log.d("MOB>ID",token);

            transportSE.call(Soap_Action + "notificationLogout", envelope);
            SoapObject result = (SoapObject) envelope.getResponse();
            if (result.getPropertyCount() != 0 && result.getProperty(0).toString().equalsIgnoreCase("inserted")) {
                isSuccess = true;
            }


        } catch (Exception ex) {
            isSuccess = false;
            //   Log.e("MOB>ID","rooorrr");

        }

        return z;
    }
}




