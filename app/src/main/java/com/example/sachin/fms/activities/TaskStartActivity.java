package com.example.sachin.fms.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sachin.fms.R;
import com.example.sachin.fms.classes.Logout;
import com.example.sachin.fms.others.WebServiceConnection;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;


public class TaskStartActivity extends AppCompatActivity {

    private CardView btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    private boolean clicks = false;
    private boolean update = false;
    private String taskId, call_no, wo_no, reported_date, reported_time;
    private int x = 0;
    private boolean edit_before = false;
    private boolean completed = false;
    private SharedPreferences sp;

    private boolean FAB_status = false;
    private ProgressDialog pbar;


    private ArrayList<String> imageStringC;
    private FloatingActionButton fab, fab1, fab2;
    private Animation show_fab1, hide_fab1, show_fab2, hide_fab2;

    private String NameSpace;
    private String Soap_Action;
    private String URL;
    private WebServiceConnection connection;

    private SoapSerializationEnvelope envelope;
    private SoapObject request;
    private SoapPrimitive response;
    private HttpTransportSE transportSE;

    private String signatureString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connection = new WebServiceConnection();
        NameSpace = connection.NameSpace;
        Soap_Action = connection.Soap_Action;
        URL = connection.URL;

        sp = this.getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);

        taskId = sp.getString(getString(R.string.task_number), "null");
        call_no = sp.getString(getString(R.string.call_number), "null");
        wo_no = sp.getString(getString(R.string.ppm_work_order_no), "null");

        btn1 = (CardView) findViewById(R.id.btn1);
        btn2 = (CardView) findViewById(R.id.btn2);
        btn3 = (CardView) findViewById(R.id.btn3);
        btn4 = (CardView) findViewById(R.id.btn4);
        btn5 = (CardView) findViewById(R.id.btn5);
        btn6 = (CardView) findViewById(R.id.btn6);
        btn7 = (CardView) findViewById(R.id.btn7);
        btn8 = (CardView) findViewById(R.id.btn8);
        btn9 = (CardView) findViewById(R.id.btn9);

        //Log.e("WORK ORDER NO ",wo_no);
        if (wo_no.equalsIgnoreCase("null") || wo_no.equalsIgnoreCase("0")) {
            btn8.setVisibility(View.GONE);

        } else {
            btn8.setVisibility(View.VISIBLE);
        }


        // Log.e("CALL _ NO ", call_no);
        reported_date = sp.getString(getString(R.string.reported_date), "");
        reported_time = sp.getString(getString(R.string.reported_time), "");

        CollapsingToolbarLayout cl = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        assert cl != null;
        cl.setTitle("Task No: " + taskId);


        pbar = new ProgressDialog(this);
        pbar.setMessage("Loading... ");
        pbar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pbar.setIndeterminate(true);
        pbar.setCancelable(false);

        Check check = new Check();
        check.execute();


        ActionBar actionBar;
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("");
        }

        assert toolbar != null;
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //clicks = intent.getBooleanExtra("count",false);


        TextView txt = (TextView) findViewById(R.id.txt0);
        assert txt != null;


        txt.setText(reported_date + "/ " + reported_time.substring(0, 5));


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                {

                    Intent i = new Intent(TaskStartActivity.this, BeforeTaskActivity.class);
                    i.putExtra("activity", 1);
                    // i.putExtra(getString(R.string.task_number),taskId);
                    //i.putExtra(getString(R.string.call_number),call_no);
                    startActivity(i);

                }


            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                {
                    Intent i = new Intent(TaskStartActivity.this, BeforeTaskActivity.class);
                    i.putExtra("activity", 2);
                    // i.putExtra(getString(R.string.task_number),taskId);
                    //i.putExtra(getString(R.string.call_number),call_no);

                    startActivity(i);
                }


            }
        });
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_2);
        //fab3=(FloatingActionButton) findViewById(R.id.fab_3);

        show_fab1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_fab_1);
        hide_fab1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_fab_1);
        show_fab2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_fab_2);
        hide_fab2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_fab_2);

        //  show_fab3= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.show_fab_3);
        // hide_fab3= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.hide_fab_3);

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout logout = new Logout(TaskStartActivity.this, sp);
                logout.execute();


                finish();

            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(TaskStartActivity.this, LandingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplication(), TaskDetailsActivity.class);
                //i.putExtra(getString(R.string.task_number),taskId);
                // i.putExtra(getString(R.string.call_number),call_no);
                i.putExtra("test", "hide");

                startActivity(i);
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
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TaskStartActivity.this, CloseActivity.class);
                i.putExtra("activity", 3);
                // i.putExtra(getString(R.string.task_number),taskId);
                //  i.putExtra(getString(R.string.call_number),call_no);
                startActivity(i);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fill_Images fill = new Fill_Images();
                fill.execute();

            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TaskStartActivity.this, SafetyRulesActivity.class);
                i.putExtra("activity", 5);
                // i.putExtra(getString(R.string.task_number),taskId);
                //i.putExtra(getString(R.string.call_number),call_no);
                startActivity(i);
            }
        });

        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TaskStartActivity.this, PPMWorkOrderListActivity.class);
                i.putExtra("activity", 5);
                // i.putExtra(getString(R.string.task_number),taskId);
                //i.putExtra(getString(R.string.call_number),call_no);
                startActivity(i);
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                GetDoc_No doc = new GetDoc_No();
                doc.execute();

            }
        });

        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                GetStatus get = new GetStatus();
                get.execute();


            }
        });


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

       /* FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin += (int) (fab3.getWidth() * 0.25);
        layoutParams3.bottomMargin += (int) (fab3.getHeight() * 1.7);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(show_fab3);
        fab3.setClickable(true);*/

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

       /* FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin -= (int) (fab3.getWidth() * 0.25);
        layoutParams3.bottomMargin -= (int) (fab3.getHeight() * 1.7);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(hide_fab3);
        fab3.setClickable(false);*/

    }

    public class GetStatus extends AsyncTask<String, String, String> {

        String current_status = "";
        String z = "";
        boolean isSuccess = false;

        public void onPreExecute() {
            pbar.show();
        }

        public void onPostExecute(String s) {
            pbar.dismiss();

            if (isSuccess) {
                Intent i = new Intent(TaskStartActivity.this, StatusChangeActivity.class);
                i.putExtra("activity", 5);
                i.putExtra("current_status", current_status);
                startActivity(i);
            } else {
                Toast.makeText(TaskStartActivity.this, s, Toast.LENGTH_SHORT).show();

            }


        }

        @Override
        protected String doInBackground(String... params) {
            PropertyInfo compcode, task_id, call_id;

            request = new SoapObject(NameSpace, "getUpdatedStatus");

            compcode = new PropertyInfo();
            compcode.setName("compcode");
            compcode.setType(String.class);
            compcode.setValue(sp.getString(getString(R.string.company_code), ""));
            request.addProperty(compcode);

            call_id = new PropertyInfo();
            call_id.setName("call_no");
            call_id.setType(String.class);
            call_id.setValue(call_no);
            request.addProperty(call_id);

            task_id = new PropertyInfo();
            task_id.setName("task_id");
            task_id.setType(String.class);
            task_id.setValue(taskId);
            request.addProperty(task_id);

            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            transportSE = new HttpTransportSE(URL);
            transportSE.debug = true;


            try {

                transportSE.call(Soap_Action + "getUpdatedStatus", envelope);
                SoapObject object = (SoapObject) envelope.getResponse();

                if (object.getPropertyCount() != 0) {
                    current_status = object.getPropertyAsString(0);
                    z = current_status;
                    isSuccess = true;
                } else {
                    isSuccess = false;
                }


            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }


            return z;
        }
    }

    public class GetDoc_No extends AsyncTask<String, String, String> {

        String doc_no = "";
        String z = "";
        boolean isSuccess = false;

        public void onPreExecute() {
            pbar.show();
        }

        public void onPostExecute(String s) {
            pbar.dismiss();

            if (isSuccess) {
                //Toast.makeText(TaskStartActivity.this,s,Toast.LENGTH_SHORT).show();
                Intent i = new Intent(TaskStartActivity.this, MaterialActivity.class);
                i.putExtra("activity", 6);
                i.putExtra(getString(R.string.task_number), taskId);
                i.putExtra(getString(R.string.doc_number), doc_no);
                startActivity(i);
            } else {
                Toast.makeText(TaskStartActivity.this, s, Toast.LENGTH_SHORT).show();

            }


        }


        @Override
        protected String doInBackground(String... params) {

            PropertyInfo compcode, empcode, task_id;

            request = new SoapObject(NameSpace, "getDocumentNumber");

            compcode = new PropertyInfo();
            compcode.setName("compcode");
            compcode.setType(String.class);
            compcode.setValue(sp.getString(getString(R.string.company_code), ""));
            request.addProperty(compcode);

            empcode = new PropertyInfo();
            empcode.setName("empcode");
            empcode.setType(String.class);
            empcode.setValue(sp.getString(getString(R.string.employee_code), ""));
            request.addProperty(empcode);

            task_id = new PropertyInfo();
            task_id.setName("task_id");
            task_id.setType(String.class);
            task_id.setValue(taskId);
            request.addProperty(task_id);

            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            transportSE = new HttpTransportSE(URL);
            transportSE.debug = true;


            try {

                transportSE.call(Soap_Action + "getDocumentNumber", envelope);
                SoapObject object = (SoapObject) envelope.getResponse();

                if (object.getPropertyCount() != 0) {
                    doc_no = object.getPropertyAsString(0);
                    z = doc_no;
                    isSuccess = true;
                } else {
                    isSuccess = false;
                }


            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }


            return z;
        }
    }

    public class Fill_Images extends AsyncTask<String, String, String> {

        boolean isSuccess = false;
        String z = "";


        public void onPostExecute(String s) {
            pbar.dismiss();
            if (isSuccess) {

                Intent i = new Intent(TaskStartActivity.this, TaskCompleteActivity.class);
                i.putExtra("activity", 4);


                // tinyDB.putListString("imageStringA",imageStringA);
                // tinyDB.putListString("imageStringB",imageStringB);
                // tinyDB.putListString("imageStringC",imageStringC);
                //i.putExtra(getString(R.string.task_number),taskId);
                i.putExtra("string", signatureString);
                startActivity(i);

            } else {
                Intent i = new Intent(TaskStartActivity.this, TaskCompleteActivity.class);
                i.putExtra("activity", 4);


                // imageStringA.clear();
                // imageStringB.clear();
                // imageStringC.clear();

                // tinyDB.putListString("imageStringA",imageStringA);
                // tinyDB.putListString("imageStringB",imageStringB);
                //tinyDB.putListString("imageStringC",imageStringC);
                // i.putExtra(getString(R.string.task_number),taskId);
                i.putExtra("string", signatureString);
                startActivity(i);
            }
        }

        public void onPreExecute() {
            pbar.show();
        }

        @Override
        protected String doInBackground(String... params) {

            PropertyInfo compcode, task_id, call_;

            request = new SoapObject(NameSpace, "getImages");
            imageStringC = new ArrayList<>();

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

            call_ = new PropertyInfo();
            call_.setName("call_no");
            call_.setType(String.class);
            call_.setValue(call_no);
            request.addProperty(call_);

            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            transportSE = new HttpTransportSE(URL);
            transportSE.debug = true;


            try {

                transportSE.call(Soap_Action + "getImages", envelope);
                SoapObject object = (SoapObject) envelope.getResponse();

                if (object.getPropertyCount() != 0) {
                    // imageStringC.add(object.getPropertyAsString(0));
                    signatureString = object.getProperty(0).toString();
                    isSuccess = true;
                } else {
                    isSuccess = false;
                }


            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }


            return z;
        }
    }

    public class Check extends AsyncTask<String, String, String> {


        boolean isSuccess = false;
        String z = "";
        private String statusCode = "";


        public void onPreExecute() {
            pbar.show();

        }

        public void onPostExecute(String r) {

            pbar.dismiss();
            if (isSuccess) {

                if (statusCode.equals(sp.getString(getString(R.string.work_completed_code), ""))) {

                    // Log.e("WORK COMPLETED equal", statusCode);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putBoolean("task_completed_" + taskId, true);
                    edit.apply();
                    btn4.setVisibility(View.VISIBLE);

                    btn1.setVisibility(View.INVISIBLE);
                    btn2.setVisibility(View.INVISIBLE);
                    btn3.setVisibility(View.INVISIBLE);
                    btn6.setVisibility(View.INVISIBLE);
                    btn5.setVisibility(View.INVISIBLE);
                    btn7.setVisibility(View.INVISIBLE);
                    btn8.setVisibility(View.INVISIBLE);

                    // Toast.makeText(TaskStartActivity.this,statusCode.toString(),Toast.LENGTH_LONG).show();

                } else {
                    //Log.e("WORK COMPLETEDnot equal", statusCode);

                    btn1.setVisibility(View.VISIBLE);
                    btn2.setVisibility(View.VISIBLE);
                    btn3.setVisibility(View.VISIBLE);
                    btn6.setVisibility(View.VISIBLE);
                    btn5.setVisibility(View.VISIBLE);
                    //Toast.makeText(TaskStartActivity.this,sp.getString(getString(R.string.work_completed_code), ""),Toast.LENGTH_LONG).show();

                }

            } else {
                Toast.makeText(TaskStartActivity.this, "Data connection problem", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            PropertyInfo compcode, task_id;

            request = new SoapObject(NameSpace, "checkWorkStatus");

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

            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            transportSE = new HttpTransportSE(URL);
            transportSE.debug = true;


            try {

                transportSE.call(Soap_Action + "checkWorkStatus", envelope);
                SoapObject object = (SoapObject) envelope.getResponse();

                if (object.getPropertyCount() != 0) {
                    statusCode = object.getPropertyAsString(0);
                    isSuccess = true;
                } else {
                    isSuccess = false;
                }


            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }


            return z;
        }
    }


}
