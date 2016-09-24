package com.example.sachin.fms.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.sachin.fms.R;
import com.example.sachin.fms.adapterPackage.SafetyAdapter;
import com.example.sachin.fms.classes.Logout;
import com.example.sachin.fms.dataSets.AllSafetyData;
import com.example.sachin.fms.dataSets.SavedSafetyData;
import com.example.sachin.fms.others.WebServiceConnection;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SafetyRulesActivity extends AppCompatActivity implements SafetyAdapter.SetOnItemClick {


    String taskId, call_no;
    Parcelable mListState;
    private SafetyAdapter adapter;
    private List<String> data = new ArrayList<>();
    private HashMap<String, String> code_list = new HashMap<>();
    private List<String> checked = new ArrayList<>();
    private List<String> saved = new ArrayList<>();
    private List<Integer> ID = new ArrayList<>();
    private RecyclerView rview;
    private Button submit;
    private FloatingActionButton fab, fab1, fab2;
    private Animation show_fab1, hide_fab1, show_fab2, hide_fab2;
    private ProgressDialog pdialog;
    private boolean FAB_status = false;
    private Bundle b;
    private SharedPreferences sp;
    private List<CheckedTextView> ctv = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    private List<SavedSafetyData> savedSafetyList;
    private List<AllSafetyData> allSafetyList;
    /**
     * objects of Web services
     *
     * @param savedInstanceState
     */

    private String NameSpace;
    private String Soap_Action;
    private String URL;
    private WebServiceConnection connection;

    private SoapSerializationEnvelope envelope, envelope2;
    private SoapObject request, request2;
    private SoapPrimitive response;
    private HttpTransportSE transportSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_rules);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar;
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Risk Assessment");
        }
        assert toolbar != null;
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        connection = new WebServiceConnection();
        NameSpace = connection.NameSpace;
        Soap_Action = connection.Soap_Action;
        URL = connection.URL;

        sp = this.getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);


        taskId = sp.getString(getString(R.string.task_number), "null");
        call_no = sp.getString(getString(R.string.call_number), "null");

        //Log.e("CALL _ NO ", call_no);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_2);

        show_fab1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_fab_1);
        hide_fab1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_fab_1);
        show_fab2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_fab_2);
        hide_fab2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_fab_2);

        submit = (Button) findViewById(R.id.submit);

        pdialog = new ProgressDialog(this);
        pdialog.setMessage("Saving...");
        pdialog.setIndeterminate(true);
        pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdialog.setCancelable(false);

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout logout = new Logout(SafetyRulesActivity.this, sp);
                logout.execute();


                finish();

            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(SafetyRulesActivity.this, LandingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!FAB_status) {
                    expandFAB();
                    FAB_status = true;


                } else {
                    hideFAB();
                    FAB_status = false;
                }

            }
        });

        rview = (RecyclerView) findViewById(R.id.rules);


        Fill_List fill = new Fill_List();
        fill.execute();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Save save = new Save();
                save.execute();
                //Toast.makeText(getApplication(),Integer.toString(Integer.parseInt(taskId)),Toast.LENGTH_LONG).show();

                //for(int i =0;i<checked.size();i++){
                //Toast.makeText(getApplication(),checked.get(i),Toast.LENGTH_LONG).show();

                // }
            }
        });


    }

    @Override
    public void itemClick(View v, int position, boolean check) {


        /*if(!saved.isEmpty()){
            for(int i=0;i<saved.size();i++) {
                checked.add(saved.get(i));
            }
        }*/

        if (check) {
            checked.add(code_list.get(data.get(position)));

        }
        if (!check) {

            int i = checked.indexOf(code_list.get(data.get(position)));
            checked.remove(i);


        }


    }

    public boolean parseSaved(String xml) throws XmlPullParserException, IOException {

        ArrayList<SavedSafetyData> products = null;
        SavedSafetyData current = null;
        boolean isSuccess = false;
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();

        InputStream is = new ByteArrayInputStream(xml.getBytes());

        parser.setInput(is, "UTF-8");

        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name = null;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    products = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();

                    //  Log.e("tag name", name);


                    if (name.equals("Table")) {
                        current = new SavedSafetyData();
                    } else if (current != null) {
                        if (name.equals("TDS_SFT_CODE")) {
                            current.s_code = (parser.nextText());

                        }

                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("Table") && current != null) {
                        assert products != null;
                        products.add(current);
                    }
            }
            eventType = parser.next();
            isSuccess = printSaved(products);
        }

        return isSuccess;
    }

    public boolean printSaved(ArrayList<SavedSafetyData> list) {
        boolean isSuccess = false;

        savedSafetyList = new ArrayList<>();
        for (SavedSafetyData current : list) {
            savedSafetyList.add(new SavedSafetyData(current.s_code));
            isSuccess = true;

        }

        return isSuccess;

    }

    public boolean parseGet(String xml) throws XmlPullParserException, IOException {

        ArrayList<AllSafetyData> products = null;
        AllSafetyData current = null;
        boolean isSuccess = false;
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();

        InputStream is = new ByteArrayInputStream(xml.getBytes());

        parser.setInput(is, "UTF-8");

        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name = null;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    products = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();

                    //   Log.e("tag name", name);


                    if (name.equals("Table")) {
                        current = new AllSafetyData();
                    } else if (current != null) {
                        switch (name) {
                            case "SFT_UID":
                                current.s_uid = (parser.nextText());

                                break;
                            case "SFT_CODE":
                                current.s_code = (parser.nextText());

                                break;
                            case "SFT_DESC":
                                current.s_des = (parser.nextText());

                                break;
                        }

                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("Table") && current != null) {
                        assert products != null;
                        products.add(current);
                    }
            }
            eventType = parser.next();
            isSuccess = printAll(products);
        }

        return isSuccess;
    }

    public boolean printAll(ArrayList<AllSafetyData> list) {
        boolean isSuccess = false;

        allSafetyList = new ArrayList<>();

        for (AllSafetyData current : list) {
            allSafetyList.add(new AllSafetyData(current.s_uid, current.s_code, current.s_des));
            isSuccess = true;

        }

        return isSuccess;

    }

    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        // Save list state
        mListState = mLayoutManager.onSaveInstanceState();
        state.putParcelable("myState", mListState);
    }

    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // Retrieve list state and list/item positions
        if (state != null)
            mListState = state.getParcelable("myState");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mListState != null) {
            mLayoutManager.onRestoreInstanceState(mListState);
        }
    }

    public void expandFAB() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        params.rightMargin += (int) (fab1.getWidth() * 1.7);
        params.bottomMargin += (int) (fab1.getHeight() * 0.25);
        fab1.setLayoutParams(params);
        fab1.startAnimation(show_fab1);
        fab1.setClickable(true);


        FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        params1.rightMargin += (int) (fab2.getWidth() * 1.5);
        params1.bottomMargin += (int) (fab2.getHeight() * 1.5);
        fab2.setLayoutParams(params1);
        fab2.setAnimation(show_fab2);
        fab2.setClickable(true);

    }

    public void hideFAB() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        params.rightMargin -= (int) (fab1.getWidth() * 1.7);
        params.bottomMargin -= (int) (fab1.getHeight() * 0.25);
        fab1.setLayoutParams(params);
        fab1.setAnimation(hide_fab1);
        fab1.setClickable(false);

        FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        params1.rightMargin -= (int) (fab2.getWidth() * 1.5);
        params1.bottomMargin -= (int) (fab2.getHeight() * 1.5);
        fab2.setLayoutParams(params1);
        fab2.setAnimation(hide_fab2);
        fab2.setClickable(false);

    }

    public class Save extends AsyncTask<String, String, String> {

        String z = "";

        boolean isSuccess = false;

        public void onPreExecute() {
            pdialog.show();

        }

        public void onPostExecute(String s) {
            pdialog.dismiss();
            if (isSuccess) {
                Toast.makeText(getApplication(), "Saved!", Toast.LENGTH_LONG).show();
                onBackPressed();

            } else {
                Toast.makeText(getApplication(), s, Toast.LENGTH_LONG).show();

            }

        }

        @Override
        protected String doInBackground(String... params) {

            PropertyInfo id_, empcode, task_id, call_, check;

            if (checked.size() != 0) {
                int id = Integer.parseInt(taskId);
                String ID = Integer.toString(id);


                for (int i = 0; i < checked.size(); i++) {
                    request = new SoapObject(NameSpace, "saveSafety");

                    id_ = new PropertyInfo();
                    id_.setName("id");
                    id_.setType(String.class);
                    id_.setValue(ID);
                    request.addProperty(id_);


                    call_ = new PropertyInfo();
                    call_.setName("call_no");
                    call_.setType(String.class);
                    call_.setValue(call_no);
                    request.addProperty(call_);


                    check = new PropertyInfo();
                    check.setName("check");
                    check.setType(String.class);
                    check.setValue(checked.get(i));
                    request.addProperty(check);


                    task_id = new PropertyInfo();
                    task_id.setName("task_id");
                    task_id.setType(String.class);
                    task_id.setValue(taskId);
                    request.addProperty(task_id);

                    empcode = new PropertyInfo();
                    empcode.setName("empcode");
                    empcode.setType(String.class);
                    empcode.setValue(sp.getString(getString(R.string.employee_code), ""));
                    request.addProperty(empcode);

                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);


                    transportSE = new HttpTransportSE(URL);
                    transportSE.debug = true;


                    try {

                        transportSE.call(Soap_Action + "saveSafety", envelope);
                        SoapObject result = (SoapObject) envelope.getResponse();

                        isSuccess = result.getPropertyCount() != 0;


                    } catch (XmlPullParserException | IOException e) {
                        e.printStackTrace();
                    }
                }


            }


            return null;
        }
    }

    public class Fill_List extends AsyncTask<String, String, String> {

        List<String> list = new ArrayList<>();
        HashMap<String, String> list_code = new HashMap<>();
        List<String> saved_1 = new ArrayList<>();

        String xml;
        String z = "";
        boolean isSuccess = false;
        boolean isSuccess_1 = false;


        public void onPreExecute() {
            pdialog.show();
        }

        public void onPostExecute(String r) {
            pdialog.dismiss();

            if (isSuccess && isSuccess_1) {

                for (int i = 0; i < allSafetyList.size(); i++) {
                    data.add(allSafetyList.get(i).s_des);
                }


                for (int i = 0; i < allSafetyList.size(); i++) {
                    code_list.put(allSafetyList.get(i).s_des, allSafetyList.get(i).s_code);
                }

                for (int i = 0; i < savedSafetyList.size(); i++) {
                    saved.add(savedSafetyList.get(i).s_code);
                }

                adapter = new SafetyAdapter(SafetyRulesActivity.this, data, saved, code_list);
                rview.setAdapter(adapter);
                adapter.SetOnClick(SafetyRulesActivity.this);
                mLayoutManager = new LinearLayoutManager(SafetyRulesActivity.this);
                rview.setLayoutManager(mLayoutManager);


            } else if (!isSuccess_1 && isSuccess) {

                for (int i = 0; i < allSafetyList.size(); i++) {
                    data.add(allSafetyList.get(i).s_des);
                }


                for (int i = 0; i < allSafetyList.size(); i++) {
                    code_list.put(allSafetyList.get(i).s_des, allSafetyList.get(i).s_code);
                }
                saved = Collections.emptyList();
                adapter = new SafetyAdapter(SafetyRulesActivity.this, data, saved, code_list);
                rview.setAdapter(adapter);
                adapter.SetOnClick(SafetyRulesActivity.this);
                mLayoutManager = new LinearLayoutManager(SafetyRulesActivity.this);
                rview.setLayoutManager(mLayoutManager);

            } else if (!isSuccess_1) {
                Toast.makeText(getApplication(), "No data found", Toast.LENGTH_LONG).show();

            }
        }

        @Override
        protected String doInBackground(String... params) {


            PropertyInfo empcode, task_id;

            request = new SoapObject(NameSpace, "savedSafety");


            task_id = new PropertyInfo();
            task_id.setName("task_id");
            task_id.setType(String.class);
            task_id.setValue(taskId);
            request.addProperty(task_id);

            empcode = new PropertyInfo();
            empcode.setName("empcode");
            empcode.setType(String.class);
            empcode.setValue(sp.getString(getString(R.string.employee_code), ""));
            request.addProperty(empcode);

            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);


            transportSE = new HttpTransportSE(URL);
            transportSE.debug = true;


            try {

                transportSE.call(Soap_Action + "savedSafety", envelope);
                //SoapObject result = (SoapObject) envelope.getResponse();


                xml = transportSE.responseDump;

                if (xml != null) {

                    isSuccess_1 = parseSaved(xml);

                } else {
                    isSuccess_1 = false;

                }

                request2 = new SoapObject(NameSpace, "getSafety");

                envelope2 = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope2.dotNet = true;
                envelope2.setOutputSoapObject(request2);

                transportSE.call(Soap_Action + "getSafety", envelope2);

                if (transportSE.responseDump != null) {
                    isSuccess = parseGet(transportSE.responseDump);
                } else {
                    isSuccess = false;

                }


            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
