package com.example.sachin.fms.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sachin.fms.R;
import com.example.sachin.fms.adapterPackage.RVAdapter;
import com.example.sachin.fms.classes.FormatDate;
import com.example.sachin.fms.classes.Logout;
import com.example.sachin.fms.dataSets.TaskListData;
import com.example.sachin.fms.others.WebServiceConnection;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
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

public class AssignedTaskActivity extends AppCompatActivity implements RVAdapter.itemClick {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RVAdapter radapter;
    private ArrayList<String> taskId;
    private ArrayList<String> call_no;

    private boolean FAB_status = false;
    private ArrayList<String> date;

    private ArrayList<String> location;
    private ArrayList<String> priority;
    private ArrayList<String> des;
    private RelativeLayout rl;
    private ArrayList<String> items;
    private List<String> id_list;

    // private Bundle b;
    // private Bundle b1;
    private SharedPreferences sp;


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
    private ProgressDialog pdialog;
    private List<TaskListData> data;
    private List<TaskListData> data_2;

    private Spinner filterSpinner;
    private HashMap<String, String> filterHash;

    public AssignedTaskActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assigned_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar;
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Assigned Task");
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


        sp = this.getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);


        pdialog = new ProgressDialog(this);
        pdialog.setMessage("Loading...");
        pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdialog.setIndeterminate(true);
        pdialog.setCancelable(false);
        filterSpinner = (Spinner) findViewById(R.id.filter_spinner);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_2);

        show_fab1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_fab_1);
        hide_fab1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_fab_1);
        show_fab2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_fab_2);
        hide_fab2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_fab_2);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);


        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout logout = new Logout(AssignedTaskActivity.this, sp);
                logout.execute();


                finish();

            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(AssignedTaskActivity.this, LandingActivity.class);
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
       /* swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                b1= new Bundle();
                refreshBundle();
                for(int j=0;j<id_list.size();j++){
                    b1.putString("taskId" + j, id_list.get(j).toString());
                }


                List<Data> data = fill_data(b1);
                recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
                radapter = new RVAdapter(getApplication(), data);
                recyclerView.setAdapter(radapter);
                radapter.SetOnClick(AssignedTaskActivity.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(AssignedTaskActivity.this));

                swipeRefreshLayout.setRefreshing(false);
            }
        });*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Fill_Data fill = new Fill_Data(1);
        fill.execute();


        List<String> filterData = new ArrayList<>();
        filterData.add("Filters");
        filterData.add("All");
        filterData.add("PPM Task");
        filterData.add("Maintenance Task");

        filterHash = new HashMap<>();
        filterHash.put("All", "999");
        filterHash.put("PPM Task", "PPM");
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
                            radapter.updateList(data);
                            radapter.SetOnClick(AssignedTaskActivity.this);

                            recyclerView.swapAdapter(radapter, false);


                            break;
                        case 2:

                            String text = parent.getItemAtPosition(position).toString();
                            //Log.e("SELECTED ", text);
                            String code = filterHash.get(text);
                            //Log.e("SELECTED code", code);

                            List<TaskListData> filteredList = new ArrayList<TaskListData>();
                            for (int i = 0; i < data_2.size(); i++) {
                                if (!data_2.get(i).ppm_no.equalsIgnoreCase("0")) {
                                    filteredList.add(data_2.get(i));
                                }
                                // Log.e("INDEID PPm", data_2.get(i).ppm_no);


                            }

                            if (filteredList.size() > 0) {
                                data.clear();
                                data.addAll(filteredList);


                                radapter = new RVAdapter(getApplication(), filteredList, 2);
                                radapter.SetOnClick(AssignedTaskActivity.this);
                                recyclerView.swapAdapter(radapter, false);
                                radapter.notifyDataSetChanged();


                            } else {
                                filteredList.clear();
                                data.clear();

                                Toast.makeText(AssignedTaskActivity.this, "No PPM task available", Toast.LENGTH_LONG).show();
                                radapter = new RVAdapter(getApplication(), filteredList, 2);
                                radapter.SetOnClick(AssignedTaskActivity.this);
                                recyclerView.swapAdapter(radapter, false);
                                radapter.notifyDataSetChanged();


                            }
                            break;


                        case 3:

                            text = parent.getItemAtPosition(position).toString();
                            /// Log.e("SELECTED ", text);
                            code = filterHash.get(text);
                            //  Log.e("SELECTED code", code);

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

                                radapter.SetOnClick(AssignedTaskActivity.this);
                                recyclerView.swapAdapter(radapter, false);
                                radapter.notifyDataSetChanged();

                            } else {
                                filteredList.clear();
                                data.clear();

                                Toast.makeText(AssignedTaskActivity.this, "No Maintenance task available", Toast.LENGTH_LONG).show();
                                radapter = new RVAdapter(getApplication(), filteredList, 2);
                                radapter.SetOnClick(AssignedTaskActivity.this);
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
                            case "COMPLAINTS":
                                current.des = parser.nextText();
                                break;
                            case "LOCATION":
                                current.location = parser.nextText();
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
            isSuccess = printProducts(products);
            eventType = parser.next();

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
                //Log.e("PPMMMM  ", current.ppm_no);

            } else {
                data.add(new TaskListData(current.task_id, current.call_no, newDate, current.location, current.des, current.priority, current.date.substring(11, 16), current.building, current.status_code, current.priority_code, "0", current.date, current.location_code, current.building_code));

                data_2.add(new TaskListData(current.task_id, current.call_no, newDate, current.location, current.des, current.priority, current.date.substring(11, 16), current.building, current.status_code, current.priority_code, "0", current.date, current.location_code, current.building_code));
                // Log.e("PPMMMM   AS", "0");

            }


            isSuccess = true;

           /* allTaskId.add(current.task_id);
            allCallNo.add(current.call_no);
            allDate.add(current.date);
            allDes.add(current.des);
            allLocations.add(current.location);
            allPriority.add(current.priority);
            allBuilding.add(current.building);*/


        }


        return isSuccess;
    }

    @Override
    public void itemClick(View v, int position) {


        Consume c = new Consume(position);
        c.execute();
    }
   /* private List<Data> fill_data(Bundle b) {

        date = new ArrayList<>();
        priority = new ArrayList<>();
        taskId = new ArrayList<>();
        call_no= new ArrayList<>();
        des = new ArrayList<>();
        items = new ArrayList<>();
        sp = AssignedTaskActivity.this.getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);
        location = new ArrayList<>();



      //  int b= Integer.parseInt(sp.getString(getString(R.string.assigned_count), "0"));
        List<Data> data = new ArrayList<>();
        if (b.size()== 0) {
            Log.e("ERROR", "There is no new task");
        } else {
            for (int i = 0; i < b.size(); i++) {
                items.add(b.getString("taskId" + i));
                try {
                    Connection con = connectionLanding.landingCONN();
                    if (con == null) {
                        Toast.makeText(AssignedTaskActivity.this, "Connection error!! Please check your Connection", Toast.LENGTH_LONG).show();

                    } else {
                        String q = "SELECT TH_TASK_NO AS TASK_NO,TH_CALL_NO,TH_DOC_DATE AS REPORT_DATETIME,TH_LOCATION AS LOCATION,TH_COMPLAINTS AS COMPLAINTS,PRDESC AS PRIORITY FROM FMTASKH H INNER JOIN FMTASKD D ON  H.TH_TASK_NO=D.TD_TASK_NO LEFT JOIN FMPRIOR P ON P.PRCODE=H.TH_SEVERITY WHERE TH_STATUS in('"+sp.getString(getString(R.string.assigned_code), "")+"','"+sp.getString(getString(R.string.work_completed_code),"")+"','"+sp.getString(getString(R.string.work_progress_code)," ")+"') AND ISNULL(D.TD_VIEWED,0)=0 AND TH_HID='"+items.get(i)+"' AND TD_EMP ='" + sp.getString(getString(R.string.employee_code), "") + "' ORDER  BY TH_TASK_NO DESC";

                        // String query="SELECT * FROM FMTASKH H INNER JOIN FMTASKD D ON  H.TH_TASK_NO=D.TD_TASK_NO WHERE th_compcd ='001' and TH_STATUS ='"+sp.getString(getString(R.string.assigned_code), "") + "' AND ISNULL(D.TD_VIEWED,0)=0 AND TD_EMP ='"+sp.getString(getString(R.string.employee_code), "")+"' AND TH_HID ='"+items.get(i)+"'";
                        Statement stm = con.createStatement();
                        ResultSet rs = stm.executeQuery(q);



                         while (rs.next()) {
                            taskId.add(rs.getString(1));
                             call_no.add(rs.getString(2));
                            date.add(rs.getString(3));
                            location.add(rs.getString(4));
                            des.add(rs.getString(5));
                            priority.add(rs.getString(6));

                        }

                    }


                } catch (SQLException q) {

                    Log.e("ERROR", q.getMessage());
                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage());
                }

                //data.add(new Data(location.get(i),des.get(i),priority.get(i),date.get(i).substring(0,10),date.get(i).substring(11,16)));


            }

        }
      //  for(int j= 0;j<b;j++){
       //     data.add(new Data(location.get(j), des.get(j), priority.get(j)));
      //  }


        return data;


    }*/

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

    public class Fill_Data extends AsyncTask<String, String, String> {

        int x = 0;
        List<TaskListData> d = new ArrayList<>();
        boolean isSuccess = false;
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

                //Log.e("Size of DAta", Integer.toString(data.size()));
                radapter = new RVAdapter(getApplication(), data, 1);
                recyclerView.setAdapter(radapter);
                radapter.SetOnClick(AssignedTaskActivity.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(AssignedTaskActivity.this));


            } else {
                Toast.makeText(AssignedTaskActivity.this, "There is not data", Toast.LENGTH_SHORT).show();
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

            // b = getIntent().getExtras();


            try {

                transportSE.call(Soap_Action + "getTaskList", envelope);
                //SoapObject result_1 = (SoapObject) envelope.getResponse();
                //xml = result_1.toString();


                if (transportSE.responseDump != null) {
                    isSuccess = parse(transportSE.responseDump);
                } else {
                    isSuccess = false;
                }
                // Log.e("TAskList",xml);
                //Log.e("count",Integer.toString(result_1.getPropertyCount()));
                /*for (int j = 0; j < result_1.getPropertyCount(); j++) {
                    SoapObject result = (SoapObject) result_1.getProperty(j);
                    Log.e("count",Integer.toString(result.getPropertyCount()));

                    if(result.getProperty("task_id").equals("anyType{}"))
                    {
                        listData.task_id="N/A";
                    }
                    else {
                        listData.task_id=result.getProperty("task_id").toString();
                    }
                    if(result.getProperty("call_no").toString().equals("anyType{}")){
                        listData.call_no="N/A";
                    }
                    else {
                        listData.call_no=result.getProperty("call_no").toString();

                    }
                    if(result.getProperty("date").toString().equals("anyType{}")){
                        listData.date="N/A";
                    }
                    else {
                        listData.date=result.getProperty("date").toString();

                    }
                    if(result.getProperty("location").toString().equals("anyType{}")){

                        listData.location="N/A";
                    }
                    else{
                        listData.location=result.getProperty("location").toString();

                    }
                    if(result.getProperty("des").toString().equals("anyType{}")){
                        listData.des="N/A";

                    }
                    else{
                        listData.des=result.getProperty("des").toString();

                    }

                    if(result.getProperty("priority").toString().equals("anyType{}")){
                        listData.priority="N/A";
                    }
                    else {
                        listData.priority=result.getProperty("priority").toString();

                    }
                    data.add(new TaskListData(listData.location,listData.des,listData.priority,listData.date.substring(0,10),listData.date.substring(11,16)));

                    allTaskId.add(listData.task_id);
                    allCallNo.add(listData.call_no);
                    allDate.add(listData.date);
                    allDes.add(listData.des);
                    allLocations.add(listData.location);
                    allPriority.add(listData.priority);



                }*/


            } catch (HttpResponseException e) {
                e.printStackTrace();
            } catch (SoapFault soapFault) {
                soapFault.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return xml;
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
                Intent i = new Intent(AssignedTaskActivity.this, TaskStartActivity.class);

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
                //i.putExtra(getString(R.string.task_number),data.get(position).task_id);
                // i.putExtra(getString(R.string.call_number),data.get(position).call_no);

                startActivity(i);
            } else {
                Intent i = new Intent(AssignedTaskActivity.this, TaskDetailsActivity.class);

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
                //i.putExtra(getString(R.string.task_number),data.get(position).task_id);
                // i.putExtra(getString(R.string.call_number),data.get(position).call_no);

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

            //Log.e("Task_id", data.get(position).task_id);

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

            // Log.e("Task_id", data.get(position).call_no);


            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            transportSE = new HttpTransportSE(URL);
            transportSE.debug = true;

            try {

                transportSE.call(Soap_Action + "getWorkStatus", envelope);
                response = (SoapPrimitive) envelope.getResponse();
                start = Integer.parseInt(response.toString());
                //Log.e("error", response.toString());
//

            } catch (Exception e) {
                //  Log.e("error", e.getMessage());
                //Toast.makeText(TaskStatusUpdateActivity.this,e.getMessage()+"Connection Error, Please Check your Internet Connection",Toast.LENGTH_LONG).show();

            }
            return start;
        }
    }

}
