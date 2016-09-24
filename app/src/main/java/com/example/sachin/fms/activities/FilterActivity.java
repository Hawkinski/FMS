package com.example.sachin.fms.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.sachin.fms.R;
import com.example.sachin.fms.adapterPackage.List_1_Adapter;
import com.example.sachin.fms.adapterPackage.List_2_Adapter;
import com.example.sachin.fms.dataSets.PriorityData;
import com.example.sachin.fms.dataSets.SavedFilterData;
import com.example.sachin.fms.dataSets.TaskListData;
import com.example.sachin.fms.database.DBHelper;
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

public class FilterActivity extends AppCompatActivity implements List_1_Adapter.OnTitleClick, List_2_Adapter.OnDescClick {
    private HashMap<String, String> filterList;
    private HashMap<String, String> priorityList;
    private List<String> p_code_list;
    private List<String> list_1;

    private RecyclerView rv1, rv2;
    private String NameSpace;
    private String Soap_Action;
    private String URL;
    private WebServiceConnection connection;

    private SoapSerializationEnvelope envelope;
    private SoapObject request;
    private SoapPrimitive response;
    private HttpTransportSE transportSE;
    private ProgressDialog pdialog;
    private SharedPreferences sp;
    private List<PriorityData> data;
    private String taskId, call_id;
    private DBHelper helper;
    private List<String> codeList;
    private List<SavedFilterData> savedFilterDatas = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connection = new WebServiceConnection();
        NameSpace = connection.NameSpace;
        Soap_Action = connection.Soap_Action;
        URL = connection.URL;


        rv2 = (RecyclerView) findViewById(R.id.list_2);
        rv1 = (RecyclerView) findViewById(R.id.list_1);


        pdialog = new ProgressDialog(this);
        pdialog.setMessage("Loading...");
        pdialog.setIndeterminate(true);
        pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdialog.setCancelable(false);

        sp = this.getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);
        taskId = sp.getString(getString(R.string.task_number), "");


        call_id = sp.getString(getString(R.string.call_number), "null");

        ActionBar actionBar;
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Filters");
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


        helper = new DBHelper(this);


        int count = helper.checkForData("P");
        //Log.e("COUNT", Integer.toString(count));

        if (count == 0) {
            GetPriority get = new GetPriority();
            get.execute();
            // Log.e("COUNT", "IINSDIDEEEE");

        }


        list_1 = new ArrayList<>();
        list_1.add("Show All");
        list_1.add("Status");
        list_1.add("Task Type");
        list_1.add("Priority");

        List_1_Adapter adapter = new List_1_Adapter(this, list_1);

        rv1.setAdapter(adapter);
        adapter.SetOnTitleClick(this);
        rv1.setLayoutManager(new LinearLayoutManager(FilterActivity.this));


    }


    @Override
    public void OnTitleClick(View v, int position) {
        switch (position) {

            case 0:

                // Log.e("Position", Integer.toString(position));

                HashMap<String, String> list = new HashMap<>();
                List<SavedFilterData> temp = new ArrayList<>();
                helper.getReadableDatabase();

                temp = helper.getSavedData("A");
                codeList = new ArrayList<>();

                for (int i = 0; i < temp.size(); i++) {
                    list.put(temp.get(i).code, temp.get(i).code);
                    //Log.e("Positionsdfasd asd", temp.get(i).code + "   " + temp.get(i).code);
                    codeList.add(temp.get(i).code);

                }

                List_2_Adapter adapter = new List_2_Adapter(this, list, codeList, helper);

                rv2.setAdapter(adapter);
                adapter.SetOnDescClick(FilterActivity.this);
                rv2.setLayoutManager(new LinearLayoutManager(FilterActivity.this));


                break;
            case 1:
                //Log.e("Position", Integer.toString(position));


                list = new HashMap<>();
                helper.getReadableDatabase();

                temp = helper.getSavedData("S");
                codeList = new ArrayList<>();

                for (int i = 0; i < temp.size(); i++) {
                    list.put(temp.get(i).code, temp.get(i).code);
                    // Log.e("Positionsdfasd asd", temp.get(i).code + "   " + temp.get(i).code);
                    codeList.add(temp.get(i).code);

                }

                adapter = new List_2_Adapter(this, list, codeList, helper);

                rv2.setAdapter(adapter);
                adapter.SetOnDescClick(FilterActivity.this);
                rv2.setLayoutManager(new LinearLayoutManager(FilterActivity.this));

                break;
            case 2:

                // Log.e("Position", Integer.toString(position));


                list = new HashMap<>();
                helper.getReadableDatabase();

                temp = helper.getSavedData("T");
                codeList = new ArrayList<>();

                for (int i = 0; i < temp.size(); i++) {
                    list.put(temp.get(i).code, temp.get(i).code);
                    Log.e("Positionsdfasd asd", temp.get(i).code + "   " + temp.get(i).code);
                    codeList.add(temp.get(i).code);

                }

                adapter = new List_2_Adapter(this, list, codeList, helper);

                rv2.setAdapter(adapter);
                adapter.SetOnDescClick(FilterActivity.this);
                rv2.setLayoutManager(new LinearLayoutManager(FilterActivity.this));


                break;
            case 3:

                //  Log.e("Position", Integer.toString(position));
                /*int count = helper.checkForData();
                if(count == 0){
                    GetPriority get= new GetPriority();
                    get.execute();


                    adapter = new List_2_Adapter(FilterActivity.this,priorityList,p_code_list);
                    rv2.setAdapter(adapter);
                    adapter.SetOnDescClick(FilterActivity.this);
                    rv2.setLayoutManager(new LinearLayoutManager(FilterActivity.this));



                }
                else{
                    savedFilterDatas = new ArrayList<>();
                    savedFilterDatas = helper.getAll();

                }*/

                list = new HashMap<>();
                helper.getReadableDatabase();

                temp = helper.getSavedData("P");
                codeList = new ArrayList<>();

                for (int i = 0; i < temp.size(); i++) {
                    list.put(temp.get(i).code, temp.get(i).code);
                    //Log.e("Positionsdfasd asd", temp.get(i).code + "   " + temp.get(i).code);
                    codeList.add(temp.get(i).code);

                }

                adapter = new List_2_Adapter(this, list, codeList, helper);

                rv2.setAdapter(adapter);
                adapter.SetOnDescClick(FilterActivity.this);
                rv2.setLayoutManager(new LinearLayoutManager(FilterActivity.this));


                break;


        }
    }

    @Override
    public void OnDescClick(View v, int position, boolean checked) {

        String code = codeList.get(position);

        int i = 0;
        if (checked) {
            helper.getWritableDatabase();

            i = helper.updateData(code, "1");

        } else {
            helper.getWritableDatabase();

            i = helper.updateData(code, "0");

        }


    }

    public boolean parse(String xml) throws XmlPullParserException, IOException {

        ArrayList<PriorityData> products = null;
        PriorityData current = null;
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
                        current = new PriorityData();
                    } else if (current != null) {
                        switch (name) {
                            case "PRDESC":
                                current.p_desc = parser.nextText();

                                break;
                            case "PRCODE":
                                current.p_code = parser.nextText();

                                break;
                            case "PRHRS":
                                current.p_hrs = parser.nextText();
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

    public boolean printProducts(ArrayList<PriorityData> xmlList) {

        data = new ArrayList<>();
        boolean isSuccess = false;
        for (PriorityData current : xmlList) {


            data.add(new PriorityData(current.p_desc, current.p_code, current.p_hrs));


            isSuccess = true;


        }


        return isSuccess;
    }

    public class GetPriority extends AsyncTask<String, String, String> {

        boolean isSuccess = false;
        List<TaskListData> d = new ArrayList<>();

        public void onPreExecute() {
            pdialog.show();

        }

        public void onPostExecute(String s) {
            pdialog.dismiss();
            if (isSuccess) {
                priorityList = new HashMap<>();
                p_code_list = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {


                    priorityList.put(data.get(i).p_code, data.get(i).p_desc);
                    p_code_list.add(data.get(i).p_code);
                    helper.insert(new SavedFilterData(data.get(i).p_code, data.get(i).p_desc, "P", "0"));


                    //Log.e("Position", data.get(i).p_code);

                }

                helper.insert(new SavedFilterData(sp.getString(getString(R.string.work_completed_code), ""), "Assigned work is completed", "S", "0"));
                helper.insert(new SavedFilterData(sp.getString(getString(R.string.work_progress_code), ""), "Work in progress", "S", "0"));
                helper.insert(new SavedFilterData("999", "All", "A", "0"));
                helper.insert(new SavedFilterData("PPM", "PPM Task", "T", "0"));
                helper.insert(new SavedFilterData("111", "Other Task", "T", "0"));


            } else {
                Toast.makeText(FilterActivity.this, "There is no data", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            PropertyInfo compcode;

            request = new SoapObject(NameSpace, "getPriority");


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

                transportSE.call(Soap_Action + "getPriority", envelope);
                // SoapObject result_1 = (SoapObject) envelope.getResponse();
                //xml = result_1.toString();

                /////////////////////////////////////////////////////////////////////////////////////////////  Log.e("HHH", transportSE.responseDump);

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


}
