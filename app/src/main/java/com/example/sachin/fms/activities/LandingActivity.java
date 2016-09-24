package com.example.sachin.fms.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sachin.fms.R;
import com.example.sachin.fms.classes.Logout;
import com.example.sachin.fms.dataSets.TaskCount;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LandingActivity extends AppCompatActivity {

    private static final int NOTE_ID = 100;

    private TextView notification, notification1;
    private TextView date;
    private TextView name;
    private int yy, dd;
    private Button taskAssigned;
    private Button statusUpdate;

    private ProgressBar pbbr;

    private boolean isSuccess = false;
    private HashMap<String, String> data = new HashMap<>();
    private ArrayList<String> id_list;

    private boolean FB_status = false;

    private FloatingActionButton fab, fab1, fab2;
    private Animation show_fab1, hide_fab1, show_fab2, hide_fab2;


    private String mm;
    private String q;
    private SharedPreferences sp;
    private CardView cd;

    private String NameSpace;
    private String Soap_Action;
    private String URL;
    private WebServiceConnection connection;

    private SoapSerializationEnvelope envelope;
    private SoapObject request;
    private SoapPrimitive response;
    private HttpTransportSE transportSE;
    private ProgressDialog pdialog;


    private TaskCount taskCount = new TaskCount();
    private List<TaskCount> taskcount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar;

        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("");
        }


        connection = new WebServiceConnection();
        NameSpace = connection.NameSpace;
        Soap_Action = connection.Soap_Action;
        URL = connection.URL;

        notification = (TextView) findViewById(R.id.notification);
        notification1 = (TextView) findViewById(R.id.notification1);
        name = (TextView) findViewById(R.id.usernameText);
        date = (TextView) findViewById(R.id.date);
        taskAssigned = (Button) findViewById(R.id.assigned_task);
        statusUpdate = (Button) findViewById(R.id.task_status_update);
        pbbr = (ProgressBar) findViewById(R.id.login_progress);
        cd = (CardView) findViewById(R.id.card);
        pdialog = new ProgressDialog(this);
        pdialog.setMessage("Loading...");
        pdialog.setIndeterminate(true);
        pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdialog.setCancelable(false);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_2);

        show_fab2 = AnimationUtils.loadAnimation(getApplication(), R.anim.show_fab_2);

        hide_fab2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_fab_2);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FB_status) {
                    expandFAB();
                    FB_status = true;
                } else {
                    hideFAB();
                    FB_status = false;

                }

            }
        });

        sp = LandingActivity.this.getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);


        TaskCount_1 count = new TaskCount_1();
        count.execute();

        // Notification bar content


        name.setText("Welcome " + sp.getString(getString(R.string.user_name), ""));
        final Calendar c = Calendar.getInstance();
        yy = c.get(Calendar.YEAR);
        mm = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        dd = c.get(Calendar.DAY_OF_MONTH);
        date.setText(new StringBuilder()

                .append(mm).append(" ").append("").append(dd).append(",")
                .append(yy));
        taskAssigned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(LandingActivity.this, AssignedTaskActivity.class);
                startActivity(i);


            }
        });
        statusUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(LandingActivity.this, TaskStatusUpdateActivity.class);
                startActivity(i);

            }
        });


        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Logout logout = new Logout(LandingActivity.this, sp);
                logout.execute();


                finish();

            }
        });


    }

    public boolean parse(String xml) throws XmlPullParserException, IOException {

        ArrayList<TaskCount> products = null;
        TaskCount current = null;
        boolean isSuccess = false;
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();

        InputStream is = new ByteArrayInputStream(xml.getBytes());

        parser.setInput(is, "UTF-8");

        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    products = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();

                    if (name.equals("Table")) {
                        current = new TaskCount();
                    } else if (current != null) {
                        if (name.equals("task1")) {
                            current.newTask = (parser.nextText());

                        } else if (name.equals("task2")) {
                            current.viewedTask = (parser.nextText());

                        }

                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("Table") && current != null) {
                        if (products != null) {
                            products.add(current);
                        }
                    }
            }
            eventType = parser.next();
            isSuccess = printProducts(products);
        }

        return isSuccess;
    }

    public boolean printProducts(ArrayList<TaskCount> list) {
        boolean isSuccess = false;

        taskcount = new ArrayList<>();

        for (TaskCount current : list) {
            taskcount.add(new TaskCount(current.newTask, current.viewedTask));
            isSuccess = true;

        }

        return isSuccess;

    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


    }

    private void expandFAB() {

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams.rightMargin += (int) (fab2.getWidth() * 1.7);
        layoutParams.bottomMargin += (int) (fab2.getHeight() * 0.25);
        fab2.setLayoutParams(layoutParams);
        fab2.startAnimation(show_fab2);
        fab2.setClickable(true);


    }

    private void hideFAB() {

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams.rightMargin -= (int) (fab2.getWidth() * 1.7);
        layoutParams.bottomMargin -= (int) (fab2.getHeight() * 0.25);
        fab2.setLayoutParams(layoutParams);
        fab2.startAnimation(hide_fab2);
        fab2.setClickable(false);


    }

    /**
     * Asynchronous class to get the count
     */

    public class TaskCount_1 extends AsyncTask<String, String, String> {


        int newTask;
        int viewedTask;
        boolean isSuccess = false;

        public void onPreExecute() {

            pdialog.show();
        }

        public void onPostExecute(String s) {

            pdialog.dismiss();

            if (isSuccess && taskcount.get(0).newTask != null) {
                newTask = Integer.parseInt(taskcount.get(0).newTask);
                viewedTask = Integer.parseInt(taskcount.get(0).viewedTask);
                if (newTask > 1) {
                    notification.setText(Integer.toString(newTask) + " New Tasks Received");

                } else if (newTask <= 1) {
                    notification.setText(Integer.toString(newTask) + " New Task Received");

                }

                if (viewedTask > 1) {
                    notification1.setText(Integer.toString(viewedTask) + " Tasks Need Status Update");

                } else if (viewedTask <= 1) {
                    notification1.setText(Integer.toString(viewedTask) + " Task Need Status Update");

                }

            } else {
                notification1.setText("0" + " Task Need Status Update");
                notification.setText("0" + " New Task Received");

            }


        }


        @Override
        protected String doInBackground(String... params) {

            PropertyInfo compcode, acode, completedcode, workprogresscode, empcode;

            request = new SoapObject(NameSpace, "TaskCountMethod");

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

                transportSE.call(Soap_Action + "TaskCountMethod", envelope);


                isSuccess = transportSE.responseDump != null && parse(transportSE.responseDump);


            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
