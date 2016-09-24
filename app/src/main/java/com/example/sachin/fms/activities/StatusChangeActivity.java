package com.example.sachin.fms.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sachin.fms.R;
import com.example.sachin.fms.classes.Logout;
import com.example.sachin.fms.dataSets.StatusData;
import com.example.sachin.fms.others.WebServiceConnection;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
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

public class StatusChangeActivity extends AppCompatActivity {


    private String taskId, call_id;

    private boolean FAB_status = false;
    // private Bundle b;
    // private Bundle b1;
    private SharedPreferences sp;
    private Button btn1;


    private FloatingActionButton fab, fab1, fab2;
    private Animation show_fab1, hide_fab1, show_fab2, hide_fab2;
    /**
     * objects of Web services
     *
     * @param savedInstanceState
     */

    private String NameSpace;
    private String Soap_Action;
    private String URL;
    private WebServiceConnection connection;
    private SoapSerializationEnvelope envelope;
    private SoapObject request;
    private HttpTransportSE transportSE;
    private ProgressDialog pdialog;
    private List<StatusData> statusData;
    private Spinner spinner;
    private List<String> statusDesc;
    private HashMap<String, String> hashStatus;

    private Bundle b;
    private EditText remark_editText;
    private String current_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_change);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar;
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Status Update");
        }

        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }


        connection = new WebServiceConnection();
        NameSpace = connection.NameSpace;
        Soap_Action = connection.Soap_Action;
        URL = connection.URL;

        b = new Bundle();
        b = getIntent().getExtras();

        if (b != null) {
            current_status = b.getString("current_status");

        }
        pdialog = new ProgressDialog(this);
        pdialog.setMessage("Loading...");
        pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdialog.setIndeterminate(true);
        pdialog.setCancelable(false);

        TextView call_no = (TextView) findViewById(R.id.call_no);
        TextView task_no = (TextView) findViewById(R.id.task_no);
        TextView status_text = (TextView) findViewById(R.id.status_text);

        if (status_text != null) {
            status_text.setText(current_status);
        }


        remark_editText = (EditText) findViewById(R.id.remark);

        spinner = (Spinner) findViewById(R.id.status);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_2);
        btn1 = (Button) findViewById(R.id.update);

        show_fab1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_fab_1);
        hide_fab1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_fab_1);
        show_fab2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_fab_2);
        hide_fab2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_fab_2);


        sp = this.getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);
        taskId = sp.getString(getString(R.string.task_number), "");

        call_id = sp.getString(getString(R.string.call_number), "null");
        if (task_no != null) {
            task_no.setText(taskId);
        }
        if (call_no != null) {
            call_no.setText(call_id);
        }

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout logout = new Logout(StatusChangeActivity.this, sp);
                logout.execute();


                finish();

            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(StatusChangeActivity.this, LandingActivity.class);
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

        if (btn1 != null) {
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Check();

                }
            });
        }
        GetStatus get = new GetStatus();
        get.execute();


    }


    public void Check() {

        boolean cancel = false;

        if (spinner.getSelectedItem() == "Select Status") {
            TextView errorText = (TextView) spinner.getSelectedView();
            errorText.setError("Please Select status");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Please select Status");//changes the selected item text to this
            cancel = true;

        }


        if (!cancel) {
            Update update = new Update();
            update.execute();
        }

    }

    public boolean parse(String xml) throws XmlPullParserException, IOException {

        ArrayList<StatusData> products = null;
        StatusData current = null;
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
                    if (name.equals("Table")) {
                        current = new StatusData();
                    } else if (current != null) {
                        switch (name) {
                            case "STCODE":
                                current.status_code = parser.nextText();

                                break;
                            case "STDESC":
                                current.status_desc = parser.nextText();

                                break;
                            case "STID":
                                current.status_id = parser.nextText();
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
            isSuccess = printProducts(products);

        }


        return isSuccess;
    }

    public boolean printProducts(ArrayList<StatusData> xmlList) {

        statusData = new ArrayList<>();
        boolean isSuccess = false;
        for (StatusData current : xmlList) {


            statusData.add(new StatusData(current.status_code, current.status_desc, current.status_id));


            isSuccess = true;


        }


        return isSuccess;
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

    public class GetStatus extends AsyncTask<String, String, String> {

        boolean isSuccess = false;
        List<StatusData> d = new ArrayList<>();

        public void onPreExecute() {
            pdialog.show();

        }

        public void onPostExecute(String s) {
            pdialog.dismiss();
            if (isSuccess) {
                hashStatus = new HashMap<>();
                statusDesc = new ArrayList<>();

                statusDesc.add("Select Status");


                for (int i = 0; i < statusData.size(); i++) {
                    hashStatus.put(statusData.get(i).status_desc, statusData.get(i).status_code);
                    statusDesc.add(statusData.get(i).status_desc);

                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(StatusChangeActivity.this, android.R.layout.simple_spinner_item, statusDesc) {

                    @Override
                    public boolean isEnabled(int position) {

                        return position != 0;


                    }

                    @Override
                    public View getDropDownView(int position, View currentView, ViewGroup parent) {
                        View v = super.getDropDownView(position, currentView, parent);
                        TextView tv = (TextView) v;
                        if (position == 0) {
                            tv.setTextColor(Color.GRAY);
                        } else {
                            tv.setTextColor(Color.BLACK);
                        }

                        return v;
                    }
                };

                adapter.setDropDownViewResource(R.layout.spinner_item);
                spinner.setAdapter(adapter);


            } else {
                Toast.makeText(StatusChangeActivity.this, "There is no data", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            PropertyInfo compcode;

            request = new SoapObject(NameSpace, "getAllStatus");


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
            //b = getIntent().getExtras();


            try {

                transportSE.call(Soap_Action + "getAllStatus", envelope);
                // SoapObject result_1 = (SoapObject) envelope.getResponse();
                //xml = result_1.toString();

                //Log.e("HHH", transportSE.responseDump);

                if (transportSE.responseDump != null) {
                    isSuccess = parse(transportSE.responseDump);
                } else {
                    isSuccess = false;
                }


            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    public class Update extends AsyncTask<String, String, String> {

        String remarks = remark_editText.getText().toString();

        boolean isSuccess = false;
        String z = "";
        String status_code = spinner.getSelectedItem().toString();


        public void onPreExecute() {
            pdialog.setMessage("Saving....");
            pdialog.setIndeterminate(true);
            pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pdialog.show();
            pdialog.setCancelable(false);


        }

        public void onPostExecute(String r) {

            pdialog.dismiss();
            if (isSuccess && z.equalsIgnoreCase("updated")) {

                Toast.makeText(StatusChangeActivity.this, "Status has been updated ", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(StatusChangeActivity.this, TaskStartActivity.class);
                i.putExtra(getString(R.string.task_number), taskId);

                startActivity(i);
                finish();


            } else {
                Toast.makeText(StatusChangeActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
            }


        }

        @Override
        protected String doInBackground(String... params) {

            PropertyInfo compcode, task_id, remark, status, call_no;


            String s = hashStatus.get(status_code);
            //Log.e("STATUSSSSS", s);

            request = new SoapObject(NameSpace, "updateStatus");
            compcode = new PropertyInfo();
            compcode.setName("compcode");
            compcode.setType(String.class);
            compcode.setValue(sp.getString(getString(R.string.company_code), ""));
            request.addProperty(compcode);


            task_id = new PropertyInfo();
            task_id.setName("task_id");
            task_id.setType(String.class);
            task_id.setValue(taskId);
            request.addProperty(task_id);

            call_no = new PropertyInfo();
            call_no.setName("call_no");
            call_no.setType(String.class);
            call_no.setValue(call_id);
            request.addProperty(call_no);


            status = new PropertyInfo();
            status.setName("status");
            status.setType(String.class);
            status.setValue(s);
            request.addProperty(status);

            remark = new PropertyInfo();
            remark.setName("remark");
            remark.setType(String.class);
            remark.setValue(remarks);
            request.addProperty(remark);

            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            transportSE = new HttpTransportSE(URL);
            transportSE.debug = true;
            //b = getIntent().getExtras();


            try {

                transportSE.call(Soap_Action + "updateStatus", envelope);
                SoapObject result_1 = (SoapObject) envelope.getResponse();
                //xml = result_1.toString();

                Log.e("HHH", transportSE.responseDump);
                if (result_1.getPropertyCount() != 0) {
                    z = result_1.getProperty(0).toString();
                    isSuccess = true;

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
