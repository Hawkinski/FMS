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
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.sachin.fms.R;
import com.example.sachin.fms.adapterPackage.PPMOrderAdapter;
import com.example.sachin.fms.classes.Logout;
import com.example.sachin.fms.dataSets.PPMOrderListData;
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
import java.util.HashMap;
import java.util.List;

public class PPMWorkOrderListActivity extends AppCompatActivity implements PPMOrderAdapter.SetOnItemClick {

    String taskId, call_no, wo_no;
    Parcelable mListState;
    private PPMOrderAdapter adapter;
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
    private List<PPMOrderListData> allSafetyList;
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
        setContentView(R.layout.activity_ppmwork_order_list);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar;
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("PPM Work Order Check List");
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
        wo_no = sp.getString(getString(R.string.ppm_work_order_no), "null");

        Log.e("WO_NO ", wo_no);

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
                Logout logout = new Logout(PPMWorkOrderListActivity.this, sp);
                logout.execute();


                finish();

            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(PPMWorkOrderListActivity.this, LandingActivity.class);
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

        rview = (RecyclerView) findViewById(R.id.ppm_order);


        Fill_List fill = new Fill_List();
        fill.execute();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checked.isEmpty()) {
                    Toast.makeText(getApplication(), "Please select an item before submitting", Toast.LENGTH_LONG).show();

                } else {
                    Save save = new Save();
                    save.execute();
                }

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

    public boolean parseGet(String xml) throws XmlPullParserException, IOException {

        ArrayList<PPMOrderListData> products = null;
        PPMOrderListData current = null;
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

                    Log.e("tag name", name);


                    if (name.equals("Table")) {
                        current = new PPMOrderListData();
                    } else if (current != null) {
                        switch (name) {
                            case "TSKD_ID":
                                current.id = (parser.nextText());

                                break;
                            case "TSKD_LNO":
                                current.lno = (parser.nextText());

                                break;
                            case "TSKD_DETAILS":
                                current.desc = (parser.nextText());

                                break;
                            case "TSKD_YNF":
                                current.checked = (parser.nextText());

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

    public boolean printAll(ArrayList<PPMOrderListData> list) {
        boolean isSuccess = false;

        allSafetyList = new ArrayList<>();

        for (PPMOrderListData current : list) {
            allSafetyList.add(new PPMOrderListData(current.id, current.lno, current.desc, current.checked));

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

            PropertyInfo tsk_id, compcode;

            if (checked.size() != 0) {
                int id = Integer.parseInt(taskId);
                String ID = Integer.toString(id);


                for (int i = 0; i < checked.size(); i++) {
                    request = new SoapObject(NameSpace, "savePPMWorkOrder");


                    tsk_id = new PropertyInfo();
                    tsk_id.setName("tskd_id");
                    tsk_id.setType(String.class);
                    tsk_id.setValue(checked.get(i));
                    request.addProperty(tsk_id);

                    compcode = new PropertyInfo();
                    compcode.setName("compcode");
                    compcode.setType(String.class);
                    compcode.setValue(sp.getString(getString(R.string.company_code), ""));
                    request.addProperty(compcode);

                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);


                    transportSE = new HttpTransportSE(URL);
                    transportSE.debug = true;


                    try {

                        transportSE.call(Soap_Action + "savePPMWorkOrder", envelope);
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

        String xml;
        String z = "";
        boolean isSuccess = false;


        public void onPreExecute() {
            pdialog.show();
        }

        public void onPostExecute(String r) {
            pdialog.dismiss();

            if (isSuccess) {

                for (int i = 0; i < allSafetyList.size(); i++) {
                    data.add(allSafetyList.get(i).desc);
                }


                for (int i = 0; i < allSafetyList.size(); i++) {
                    code_list.put(allSafetyList.get(i).desc, allSafetyList.get(i).id);
                }


                adapter = new PPMOrderAdapter(PPMWorkOrderListActivity.this, allSafetyList, code_list);
                rview.setAdapter(adapter);
                adapter.SetOnClick(PPMWorkOrderListActivity.this);
                mLayoutManager = new LinearLayoutManager(PPMWorkOrderListActivity.this);
                rview.setLayoutManager(mLayoutManager);


            } else {
                Toast.makeText(getApplication(), "No data found", Toast.LENGTH_LONG).show();

            }
        }

        @Override
        protected String doInBackground(String... params) {


            PropertyInfo work_order_no, compcode;

            request = new SoapObject(NameSpace, "getPPMWorkOrderList");


            work_order_no = new PropertyInfo();
            work_order_no.setName("work_order_no");
            work_order_no.setType(String.class);
            work_order_no.setValue(wo_no);
            request.addProperty(work_order_no);

            compcode = new PropertyInfo();
            compcode.setName("compcode");
            compcode.setType(String.class);
            compcode.setValue(sp.getString(getString(R.string.company_code), ""));
            request.addProperty(compcode);

            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);


            transportSE = new HttpTransportSE(URL);
            transportSE.debug = true;


            try {

                transportSE.call(Soap_Action + "getPPMWorkOrderList", envelope);
                //SoapObject result = (SoapObject) envelope.getResponse();

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
