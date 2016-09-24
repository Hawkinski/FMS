package com.example.sachin.fms.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sachin.fms.R;
import com.example.sachin.fms.classes.Logout;
import com.example.sachin.fms.dataSets.Data;
import com.example.sachin.fms.others.WebServiceConnection;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashMap;
import java.util.List;

public class TaskDetailsActivity extends AppCompatActivity {

    private Button btn;
    private Bundle b;
    private TextView text_00, text_0, text_1, text_2, text_3, text_4, text_5, text_6, text_7, text_8, text_9, text_10, text_11, text_12;
    private SharedPreferences sp;
    private List<HashMap<String, String>> list;
    private String query;
    private String taskId;
    private String call_no;

    private boolean FAB_status = false;

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
    private SoapPrimitive response;
    private HttpTransportSE transportSE;
    private Data details = new Data();
    private ProgressDialog pdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        connection = new WebServiceConnection();
        NameSpace = connection.NameSpace;
        Soap_Action = connection.Soap_Action;
        URL = connection.URL;
        b = getIntent().getExtras();

        sp = this.getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);

        taskId = sp.getString(getString(R.string.task_number), "null");
        call_no = sp.getString(getString(R.string.call_number), "null");

        //Log.e("CALL _ NO ", call_no);


        CollapsingToolbarLayout cl = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        assert cl != null;
        cl.setTitle("Task No:" + taskId);
        assert toolbar != null;
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        btn = (Button) findViewById(R.id.start_task_btn);
        text_00 = (TextView) findViewById(R.id.text0_00);

        text_0 = (TextView) findViewById(R.id.text0_0);
        text_1 = (TextView) findViewById(R.id.text1_1);
        text_2 = (TextView) findViewById(R.id.text2_2);
        text_3 = (TextView) findViewById(R.id.text3_3);
        text_4 = (TextView) findViewById(R.id.text4_4);
        text_5 = (TextView) findViewById(R.id.text5_5);
        text_6 = (TextView) findViewById(R.id.text6_6);
        text_7 = (TextView) findViewById(R.id.text7_7);
        text_8 = (TextView) findViewById(R.id.text8_8);
        text_9 = (TextView) findViewById(R.id.text9_9);
        text_10 = (TextView) findViewById(R.id.text10_10);
        text_11 = (TextView) findViewById(R.id.text11_11);
        text_12 = (TextView) findViewById(R.id.text12_12);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_2);

        show_fab1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_fab_1);
        hide_fab1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_fab_1);
        show_fab2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_fab_2);
        hide_fab2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_fab_2);

        pdialog = new ProgressDialog(this);
        pdialog.setMessage("Loading...");
        pdialog.setIndeterminate(true);
        pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdialog.setCancelable(false);

        if (b != null) {
            if (b.getString("test") != null && b.getString("test").equals("hide")) {

                btn.setVisibility(View.GONE);
            } else {
                btn.setVisibility(View.VISIBLE);
            }
        } else {
            btn.setVisibility(View.VISIBLE);
        }

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

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout logout = new Logout(TaskDetailsActivity.this, sp);
                logout.execute();


                finish();

            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(TaskDetailsActivity.this, LandingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Consume c = new Consume();
                c.execute();


            }
        });


        GetDetails get = new GetDetails();
        get.execute();


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /* @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.menu_other, menu);
         return true;
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         // Handle action bar item clicks here. The action bar will
         // automatically handle clicks on the Home/Up button, so long
         // as you specify a parent activity in AndroidManifest.xml.

         //noinspection SimplifiableIfStatement
         switch (item.getItemId()) {

             case R.id.logout:

                 SharedPreferences sp = TaskDetailsActivity.this.getSharedPreferences(getString(R.string.preferences), MODE_APPEND);
                 SharedPreferences.Editor editor = sp.edit();
                 editor.clear();
                 editor.commit();

                 Intent i = new Intent(TaskDetailsActivity.this, LoginActivity.class);
                 i.putExtra("finish", true); // if you are checking for this in your other Activities
                 i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                         Intent.FLAG_ACTIVITY_CLEAR_TASK |
                         Intent.FLAG_ACTIVITY_NEW_TASK);
                 startActivity(i);
                 finish();


                 break;
             case R.id.HOME:

                 Intent intent = new Intent(TaskDetailsActivity.this, LandingActivity.class);
                 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 startActivity(intent);
                 finish();

                 break;
             default:

                 return true;
         }

         return super.onOptionsItemSelected(item);
     }*/
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
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

    public class Consume extends AsyncTask<String, String, String> {


        boolean isSuccess = false;
        String z = "";


        public void onPreExecute() {
            pdialog.show();
        }

        public void onPostExecute(String s) {
            pdialog.dismiss();

            if (isSuccess && z.equalsIgnoreCase("updated")) {
                Intent i = new Intent(TaskDetailsActivity.this, TaskStartActivity.class);
                // i.putExtra(getString(R.string.task_number), taskId);
                //i.putExtra(getString(R.string.call_number), call_no);

                startActivity(i);
                finish();
            }
        }


        @Override
        protected String doInBackground(String... params) {
            PropertyInfo task_no, compcode, call_, workprogresscode;

            request = new SoapObject(NameSpace, "workStart");

            task_no = new PropertyInfo();
            task_no.setName("task_id");
            task_no.setType(String.class);
            task_no.setValue(taskId);
            request.addProperty(task_no);

            // Log.e("work progress", taskId);


            compcode = new PropertyInfo();
            compcode.setName("compcode");
            compcode.setType(String.class);
            compcode.setValue(sp.getString(getString(R.string.company_code), ""));
            request.addProperty(compcode);

            call_ = new PropertyInfo();
            call_.setName("call_no");
            call_.setType(String.class);
            call_.setValue(call_no);
            request.addProperty(call_);

            /// Log.e("work progress", call_no);


            workprogresscode = new PropertyInfo();
            workprogresscode.setName("workprogresscode");
            workprogresscode.setType(String.class);
            workprogresscode.setValue(sp.getString(getString(R.string.work_progress_code), ""));
            request.addProperty(workprogresscode);


            //Log.e("work progress", sp.getString(getString(R.string.work_progress_code), ""));

            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            transportSE = new HttpTransportSE(URL);
            transportSE.debug = true;

            try {

                transportSE.call(Soap_Action + "workStart", envelope);
                SoapObject result = (SoapObject) envelope.getResponse();
                isSuccess = true;

                if (result.getPropertyCount() != 0) {
                    z = result.getProperty(0).toString();
                    isSuccess = true;

                } else {
                    isSuccess = false;
                }


            } catch (Exception e) {
                // Log.e("error", e.getMessage());
                //Toast.makeText(TaskStatusUpdateActivity.this,e.getMessage()+"Connection Error, Please Check your Internet Connection",Toast.LENGTH_LONG).show();

            }
            return z;
        }
    }

    public class GetDetails extends AsyncTask<String, String, String> {

        boolean isSuccess = false;

        public void onPreExecute() {

            pdialog.show();

        }

        public void onPostExecute(String r) {

            pdialog.dismiss();
            if (isSuccess) {

                if (call_no != null) {
                    text_00.setText(call_no);
                } else {
                    text_00.setText("N/A");

                }
                if (details.date.equals("anyType{}")) {
                    text_0.setText("N/A");
                    text_1.setText("N/A");
                } else {
                    String str = details.date;
                    String[] str1 = str.split(" ");
                    String s1 = str1[0];
                    String s2 = str1[1];
                    text_0.setText(s1);
                    text_1.setText(s2.substring(0, 5));
                }
                if (details.s_date.equals("anyType{}")) {
                    text_11.setText("N/A");

                } else {
                    String string = details.s_date;
                    String[] x1 = string.split(" ");
                    String y1 = x1[0];
                    text_11.setText(y1);
                }
                if (details.d_date.equals("anyType{}")) {
                    text_12.setText("N/A");
                } else {
                    String string1 = details.d_date;

                    String[] x2 = string1.split(" ");

                    String y2 = x2[0];
                    text_12.setText(y2);
                }


                text_2.setText(details.location);
                text_3.setText(details.building);
                text_4.setText(details.unit);
                text_5.setText(details.contract);
                text_6.setText(details.person);
                text_7.setText(details.mobile_no);
                text_8.setText(details.landline_no);
                text_9.setText(details.complain);
                text_10.setText(details.priority);


            } else {
                Toast.makeText(TaskDetailsActivity.this, "There are no data", Toast.LENGTH_LONG).show();
            }


        }

        @Override
        protected String doInBackground(String... params) {
            PropertyInfo task_no, empcode, compcode, acode, completedcode, workprogresscode;


            request = new SoapObject(NameSpace, "TaskDetails");

            task_no = new PropertyInfo();
            task_no.setName("task_no");
            task_no.setType(String.class);
            task_no.setValue(taskId);
            request.addProperty(task_no);

            empcode = new PropertyInfo();
            empcode.setName("empcode");
            empcode.setType(String.class);
            empcode.setValue(sp.getString(getString(R.string.employee_code), ""));
            request.addProperty(empcode);

            compcode = new PropertyInfo();
            compcode.setName("compcode");
            compcode.setType(String.class);
            compcode.setValue(sp.getString(getString(R.string.company_code), ""));
            request.addProperty(compcode);

            acode = new PropertyInfo();
            acode.setName("assignedcode");
            acode.setType(String.class);
            acode.setValue(sp.getString(getString(R.string.assigned_code), ""));
            request.addProperty(acode);

            completedcode = new PropertyInfo();
            completedcode.setName("completedcode");
            completedcode.setType(String.class);
            completedcode.setValue(sp.getString(getString(R.string.work_completed_code), ""));
            request.addProperty(completedcode);

            workprogresscode = new PropertyInfo();
            workprogresscode.setName("workprogresscode");
            workprogresscode.setType(String.class);
            workprogresscode.setValue(sp.getString(getString(R.string.work_progress_code), ""));
            request.addProperty(workprogresscode);


            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            transportSE = new HttpTransportSE(URL);
            transportSE.debug = true;

            // b = getIntent().getExtras();


            try {

                transportSE.call(Soap_Action + "TaskDetails", envelope);
                SoapObject result = (SoapObject) envelope.getResponse();

                // Log.e("String", result.toString());
                if (result.getProperty("date").toString().equals("anyType{}")) {
                    details.date = "N/A";

                } else {
                    details.date = result.getProperty("date").toString();
                }
                if (result.getProperty("location").toString().equals("anyType{}")) {
                    details.location = "N/A";
                } else {
                    details.location = result.getProperty("location").toString();

                }
                if (result.getProperty("building").toString().equals("anyType{}")) {
                    details.building = "N/A";
                } else {
                    details.building = result.getProperty("building").toString();

                }
                if (result.getProperty("unit").toString().equals("anyType{}")) {

                    details.unit = "N/A";
                } else {
                    details.unit = result.getProperty("unit").toString();

                }
                if (result.getProperty("contract").toString().equals("anyType{}")) {
                    details.contract = "N/A";

                } else {
                    details.contract = result.getProperty("contract").toString();

                }

                if (result.getProperty("person").toString().equals("anyType{}")) {
                    details.person = "N/A";
                } else {
                    details.person = result.getProperty("person").toString();

                }
                if (result.getProperty("mobile_no").toString().equals("anyType{}")) {
                    details.mobile_no = "N/A";
                } else {
                    details.mobile_no = result.getProperty("mobile_no").toString();

                }
                if (result.getProperty("landline_no").toString().equals("anyType{}")) {
                    details.landline_no = "N/A";
                } else {
                    details.landline_no = result.getProperty("landline_no").toString();

                }
                if (result.getProperty("complain").toString().equals("anyType{}")) {
                    details.complain = "N/A";
                } else {
                    details.complain = result.getProperty("complain").toString();

                }
                if (result.getProperty("priority").toString().equals("anyType{}")) {
                    details.priority = "N/A";
                } else {
                    details.priority = result.getProperty("priority").toString();

                }
                if (result.getProperty("s_date").toString().equals("anyType{}")) {
                    details.s_date = "N/A";
                } else {
                    details.s_date = result.getProperty("s_date").toString();

                }
                if (result.getProperty("d_date").toString().equals("anyType{}")) {
                    details.d_date = "N/A";
                } else {
                    details.d_date = result.getProperty("d_date").toString();

                }
                isSuccess = true;
            } catch (Exception e) {

                //Log.e("ERROR", e.getMessage());
                isSuccess = false;
            }

            return null;
        }
    }


}
