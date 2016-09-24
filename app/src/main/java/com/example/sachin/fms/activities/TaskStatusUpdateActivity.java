package com.example.sachin.fms.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sachin.fms.R;
import com.example.sachin.fms.adapterPackage.RVAdapter;
import com.example.sachin.fms.classes.FormatDate;
import com.example.sachin.fms.classes.Logout;
import com.example.sachin.fms.dataSets.TaskListData;
import com.example.sachin.fms.fragments.FilterFragment;
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

public class TaskStatusUpdateActivity extends AppCompatActivity implements RVAdapter.itemClick, FilterFragment.OnFragmentInteractionListener {
    private RecyclerView recyclerView;
    private RVAdapter radapter;
    private ArrayList<String> taskId;
    private ArrayList<String> call_no;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<String> date;
    private List<String> id_list;
    private ArrayList<String> location;
    private ArrayList<String> priority;
    private ArrayList<String> des;
    private ArrayList<String> items;
    private boolean FAB_status = false;
    private FloatingActionButton fab, fab1, fab2, fab_filter;
    private Animation show_fab1, hide_fab1, show_fab2, hide_fab2;
    private SharedPreferences sp;
    private ProgressDialog pdialog;
    private ArrayList<String> allTaskId = new ArrayList<>();
    private ArrayList<String> allCallNo = new ArrayList<>();
    private ArrayList<String> allDes = new ArrayList<>();
    private ArrayList<String> allLocations = new ArrayList<>();
    private ArrayList<String> allPriority = new ArrayList<>();
    private ArrayList<String> allDate = new ArrayList<>();

    private List<TaskListData> data;
    private List<TaskListData> data_2;

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
    private TaskListData listData = new TaskListData();
    private android.support.v4.app.FragmentTransaction ft;

    private Spinner filterSpinner;
    private HashMap<String, String> filterHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_status_update);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout cl = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        ActionBar actionBar;
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Task Status Update");
        }

        assert toolbar != null;
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        connection = new WebServiceConnection();
        NameSpace = connection.NameSpace;
        Soap_Action = connection.Soap_Action;
        URL = connection.URL;

        filterSpinner = (Spinner) findViewById(R.id.filter_spinner);


       /* swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                b1 = new Bundle();
                refreshBundle();
                for (int j = 0; j < id_list.size(); j++) {
                    b1.putString("taskId" + j, id_list.get(j).toString());
                }


                List<Data> data = fill_data(b1);
                recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
                radapter = new RVAdapter(getApplication(), data);
                recyclerView.setAdapter(radapter);
                radapter.SetOnClick(TaskStatusUpdateActivity.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(TaskStatusUpdateActivity.this));

                swipeRefreshLayout.setRefreshing(false);
            }
        });*/


        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        pdialog = new ProgressDialog(this);
        pdialog.setMessage("Loading...");
        pdialog.setIndeterminate(true);
        pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdialog.setCancelable(false);

        sp = this.getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_2);
        //fab_filter=(FloatingActionButton) findViewById(R.id.filter);

        show_fab1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_fab_1);
        hide_fab1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_fab_1);
        show_fab2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_fab_2);
        hide_fab2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_fab_2);


        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout logout = new Logout(TaskStatusUpdateActivity.this, sp);
                logout.execute();


                finish();

            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(TaskStatusUpdateActivity.this, LandingActivity.class);
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
      /* fab_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                Intent i = new Intent(TaskStatusUpdateActivity.this,FilterActivity.class);
                startActivity(i);





               /* FilterFragment frag;
                FragmentManager manager =TaskStatusUpdateActivity.this.getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();

                frag = new FilterFragment();

                transaction.replace(R.id.filter_fragment,frag);
                transaction.commit();

            }
        });*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //List<Data> data = fill_data(b);
        final Fill_Data fill = new Fill_Data(2);
        fill.execute();

        List<String> filterData = new ArrayList<>();
        filterData.add("Filters");
        filterData.add("All");
        filterData.add("PPM Task");
        filterData.add("Completed Task");
        filterData.add("Open Task");
        filterData.add("Maintenance Task");

        filterHash = new HashMap<>();
        filterHash.put("All", "999");
        filterHash.put("PPM Task", "0");
        filterHash.put("Completed Task", sp.getString(getString(R.string.work_completed_code), ""));
        filterHash.put("Open Task", sp.getString(getString(R.string.work_progress_code), ""));

        filterHash.put("Maintenance Task", "M");


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, filterData) {

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

        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        filterSpinner.setAdapter(arrayAdapter);


    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onStart() {

        super.onStart();
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {


                    switch (position) {


                        case 1:
                            data.clear();
                            data.addAll(data_2);

                            radapter = new RVAdapter(getApplication(), data, 2);
                            radapter.SetOnClick(TaskStatusUpdateActivity.this);
                            recyclerView.swapAdapter(radapter, false);
                            radapter.notifyDataSetChanged();


                            break;
                        case 2:

                            String text = parent.getItemAtPosition(position).toString();
                            String code = filterHash.get(text);

                            List<TaskListData> filteredList = new ArrayList<TaskListData>();
                            for (int i = 0; i < data_2.size(); i++) {
                                if (!data_2.get(i).ppm_no.equalsIgnoreCase("0")) {
                                    filteredList.add(data_2.get(i));
                                }


                            }

                            if (filteredList.size() > 0) {
                                data.clear();
                                data.addAll(filteredList);


                                radapter = new RVAdapter(getApplication(), filteredList, 2);
                                radapter.SetOnClick(TaskStatusUpdateActivity.this);
                                recyclerView.swapAdapter(radapter, false);
                                radapter.notifyDataSetChanged();


                            } else {
                                filteredList.clear();
                                data.clear();

                                Toast.makeText(TaskStatusUpdateActivity.this, "No PPM task available", Toast.LENGTH_LONG).show();
                                radapter = new RVAdapter(getApplication(), filteredList, 2);
                                radapter.SetOnClick(TaskStatusUpdateActivity.this);
                                recyclerView.swapAdapter(radapter, false);
                                radapter.notifyDataSetChanged();


                            }
                            break;


                        case 3:

                            text = parent.getItemAtPosition(position).toString();
                            code = filterHash.get(text);
                            filteredList = new ArrayList<TaskListData>();
                            for (int i = 0; i < data_2.size(); i++) {
                                if (data_2.get(i).status_code.equalsIgnoreCase(sp.getString(getString(R.string.work_completed_code), ""))) {
                                    filteredList.add(data_2.get(i));
                                }

                            }

                            if (filteredList.size() > 0) {
                                data.clear();
                                data.addAll(filteredList);

                                radapter = new RVAdapter(getApplication(), filteredList, 2);
                                radapter.SetOnClick(TaskStatusUpdateActivity.this);
                                recyclerView.swapAdapter(radapter, false);
                                radapter.notifyDataSetChanged();

                            } else {
                                filteredList.clear();
                                data.clear();

                                Toast.makeText(TaskStatusUpdateActivity.this, "No Completed task available", Toast.LENGTH_LONG).show();
                                radapter = new RVAdapter(getApplication(), filteredList, 2);
                                radapter.SetOnClick(TaskStatusUpdateActivity.this);
                                recyclerView.swapAdapter(radapter, false);
                                radapter.notifyDataSetChanged();


                            }
                            break;

                        case 4:

                            text = parent.getItemAtPosition(position).toString();
                            code = filterHash.get(text);

                            filteredList = new ArrayList<TaskListData>();
                            for (int i = 0; i < data_2.size(); i++) {
                                if (data_2.get(i).status_code.equalsIgnoreCase(sp.getString(getString(R.string.work_progress_code), "")) || data_2.get(i).status_code.equalsIgnoreCase(sp.getString(getString(R.string.assigned_task), ""))) {
                                    filteredList.add(data_2.get(i));
                                }

                            }

                            if (filteredList.size() > 0) {
                                data.clear();
                                data.addAll(filteredList);
                                radapter = new RVAdapter(getApplication(), filteredList, 2);

                                radapter.SetOnClick(TaskStatusUpdateActivity.this);
                                recyclerView.swapAdapter(radapter, false);
                                radapter.notifyDataSetChanged();

                            } else {
                                filteredList.clear();
                                data.clear();

                                Toast.makeText(TaskStatusUpdateActivity.this, "No Open task available", Toast.LENGTH_LONG).show();
                                radapter = new RVAdapter(getApplication(), filteredList, 2);
                                radapter.SetOnClick(TaskStatusUpdateActivity.this);
                                recyclerView.swapAdapter(radapter, false);
                                radapter.notifyDataSetChanged();


                            }
                            break;


                        case 5:

                            text = parent.getItemAtPosition(position).toString();
                            code = filterHash.get(text);

                            filteredList = new ArrayList<TaskListData>();
                            for (int i = 0; i < data_2.size(); i++) {
                                if (data_2.get(i).ppm_no.equalsIgnoreCase("0")) {
                                    filteredList.add(data_2.get(i));
                                }

                            }

                            if (filteredList.size() > 0) {
                                data.clear();
                                data.addAll(filteredList);
                                radapter = new RVAdapter(getApplication(), filteredList, 2);

                                radapter.SetOnClick(TaskStatusUpdateActivity.this);
                                recyclerView.swapAdapter(radapter, false);
                                radapter.notifyDataSetChanged();

                            } else {
                                filteredList.clear();
                                data.clear();

                                Toast.makeText(TaskStatusUpdateActivity.this, "No Maintenance task available", Toast.LENGTH_LONG).show();
                                radapter = new RVAdapter(getApplication(), filteredList, 2);
                                radapter.SetOnClick(TaskStatusUpdateActivity.this);
                                recyclerView.swapAdapter(radapter, false);
                                radapter.notifyDataSetChanged();


                            }
                            break;
                    }


                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public boolean parse(String xml) throws XmlPullParserException, IOException {

        ArrayList<TaskListData> products = null;
        TaskListData current = null;
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
                        current = new TaskListData();
                    } else if (current != null) {
                        switch (name) {
                            case "TASK_NO":
                                current.task_id = parser.nextText();

                                break;
                            case "TH_CALL_NO":
                                current.call_no = parser.nextText();

                                break;
                            case "REPORT_DATETIME":
                                current.date = parser.nextText();
                                break;
                            case "BUILDING":
                                current.building = parser.nextText();
                                break;
                            case "LOCATION":
                                current.location = parser.nextText();
                                break;
                            case "COMPLAINTS":
                                current.des = parser.nextText();
                                break;
                            case "PRIORITY":
                                current.priority = parser.nextText();
                                break;
                            case "TH_SEVERITY":
                                current.priority_code = parser.nextText();
                                break;
                            case "TH_STATUS":
                                current.status_code = parser.nextText();
                                break;
                            case "TH_WODOCNO":
                                current.ppm_no = parser.nextText();
                                break;
                            case "TH_LOCATION":
                                current.location_code = parser.nextText();
                                break;
                            case "TH_BUILDING":
                                current.building_code = parser.nextText();
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

    public boolean printProducts(ArrayList<TaskListData> xmlList) {

        data = new ArrayList<>();
        data_2 = new ArrayList<>();

        boolean isSuccess = false;
        for (TaskListData current : xmlList) {


            FormatDate f = new FormatDate();
            String newDate = f.FormatDate(current.date.substring(0, 10));

            if (current.ppm_no != null && !current.ppm_no.isEmpty()) {
                data.add(new TaskListData(current.task_id, current.call_no, newDate, current.location, current.des, current.priority, current.date.substring(11, 16), current.building, current.status_code, current.priority_code, current.ppm_no, current.date, current.location_code, current.building_code));

                data_2.add(new TaskListData(current.task_id, current.call_no, newDate, current.location, current.des, current.priority, current.date.substring(11, 16), current.building, current.status_code, current.priority_code, current.ppm_no, current.date, current.location_code, current.building_code));

            } else {
                data.add(new TaskListData(current.task_id, current.call_no, newDate, current.location, current.des, current.priority, current.date.substring(11, 16), current.building, current.status_code, current.priority_code, "0", current.date, current.location_code, current.building_code));

                data_2.add(new TaskListData(current.task_id, current.call_no, newDate, current.location, current.des, current.priority, current.date.substring(11, 16), current.building, current.status_code, current.priority_code, "0", current.date, current.location_code, current.building_code));

            }

            isSuccess = true;


        }


        return isSuccess;
    }

    @Override
    public void itemClick(View v, int position) {


        Consume c = new Consume(position);
        c.execute();
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

    /**
     * Asynchronous class to fill the recycler view list with task data
     */


    public class Fill_Data extends AsyncTask<String, String, String> {

        int x = 0;
        boolean isSuccess = false;
        List<TaskListData> d = new ArrayList<>();
        private String xml;

        public Fill_Data(int i) {
            this.x = i;
        }

        public void onPreExecute() {
            pdialog.show();

        }

        public void onPostExecute(String s) {
            pdialog.dismiss();
            if (isSuccess) {

                radapter = new RVAdapter(getApplication(), data, 2);
                radapter.setHasStableIds(true);

                recyclerView.setAdapter(radapter);
                radapter.SetOnClick(TaskStatusUpdateActivity.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(TaskStatusUpdateActivity.this));


            } else {
                Toast.makeText(TaskStatusUpdateActivity.this, "There is no data", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            PropertyInfo task, acode, completedcode, workprogresscode, empcode, compcode;

            request = new SoapObject(NameSpace, "getTaskList");

            task = new PropertyInfo();
            task.setName("option");
            task.setType(Integer.class);
            task.setValue(x);
            request.addProperty(task);

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

                transportSE.call(Soap_Action + "getTaskList", envelope);


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

    public class Consume extends AsyncTask<String, String, Integer> {


        int start = 0;
        int position = 0;

        public Consume(int position) {
            this.position = position;
        }

        public void onPreExecute() {
            pdialog.show();
        }

        public void onPostExecute(Integer s) {
            pdialog.dismiss();

            if (s == 1) {
                Intent i = new Intent(TaskStatusUpdateActivity.this, TaskStartActivity.class);

                SharedPreferences.Editor edit = sp.edit();
                edit.putString(getString(R.string.task_number), data.get(position).task_id);
                edit.putString(getString(R.string.reported_date), data.get(position).date);
                edit.putString(getString(R.string.reported_time), data.get(position).time);
                edit.putString(getString(R.string.building), data.get(position).building);
                edit.putString(getString(R.string.location), data.get(position).location);
                edit.putString(getString(R.string.call_number), data.get(position).call_no);
                edit.putString(getString(R.string.ppm_work_order_no), data.get(position).ppm_no);
                edit.putString(getString(R.string.location_code), data.get(position).location_code);
                edit.putString(getString(R.string.building_code), data.get(position).building_code);

                edit.putString(getString(R.string.reported_datetime), data.get(position).reported_dt);


                edit.apply();

                startActivity(i);
            } else {
                Intent i = new Intent(TaskStatusUpdateActivity.this, TaskDetailsActivity.class);

                SharedPreferences.Editor edit = sp.edit();
                edit.putString(getString(R.string.task_number), data.get(position).task_id);
                edit.putString(getString(R.string.reported_date), data.get(position).date);
                edit.putString(getString(R.string.reported_time), data.get(position).time);
                edit.putString(getString(R.string.building), data.get(position).building);
                edit.putString(getString(R.string.location), data.get(position).location);
                edit.putString(getString(R.string.call_number), data.get(position).call_no);
                edit.putString(getString(R.string.ppm_work_order_no), data.get(position).ppm_no);
                edit.putString(getString(R.string.location_code), data.get(position).location_code);
                edit.putString(getString(R.string.building_code), data.get(position).building_code);
                edit.putString(getString(R.string.reported_datetime), data.get(position).reported_dt);

                edit.apply();

                startActivity(i);
            }
        }


        @Override
        protected Integer doInBackground(String... params) {
            PropertyInfo task_no, compcode, call_;

            request = new SoapObject(NameSpace, "getWorkStatus");

            task_no = new PropertyInfo();
            task_no.setName("task_no");
            task_no.setType(String.class);
            task_no.setValue(data.get(position).task_id);
            request.addProperty(task_no);


            compcode = new PropertyInfo();
            compcode.setName("compcode");
            compcode.setType(String.class);
            compcode.setValue(sp.getString(getString(R.string.company_code), ""));
            request.addProperty(compcode);


            call_ = new PropertyInfo();
            call_.setName("call_no");
            call_.setType(String.class);
            call_.setValue(data.get(position).call_no);
            request.addProperty(call_);


            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            transportSE = new HttpTransportSE(URL);
            transportSE.debug = true;

            try {

                transportSE.call(Soap_Action + "getWorkStatus", envelope);
                response = (SoapPrimitive) envelope.getResponse();
                start = Integer.parseInt(response.toString());


            } catch (Exception e) {

            }
            return start;
        }
    }


}
