package com.example.sachin.fms.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sachin.fms.R;
import com.example.sachin.fms.adapterPackage.Adapter;
import com.example.sachin.fms.dataSets.AddedData;
import com.example.sachin.fms.dataSets.LocationData;
import com.example.sachin.fms.dataSets.MaterialData;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AddMaterialActivity extends AppCompatActivity implements Adapter.SetOnItemClick {


    private AutoCompleteTextView textView;

    private SharedPreferences sp;
    private List<AddedData> data = new ArrayList<>();
    private RecyclerView material_recycler;
    private TextView p_code;
    private TextView p_des;
    private TextView unit;
    private EditText qt;
    private String taskId, docId;
    private HashMap<String, MaterialData> mDatabyCode;
    private HashMap<String, MaterialData> mDatabyDes;
    private ProgressDialog pdialog;

    private Spinner locationSpinner;
    private List<String> location = new ArrayList<>();
    private HashMap<String, String> location_code = new HashMap<>();
    private List<AddedData> list = new ArrayList<>();
    private List<LocationData> locationDatas = new ArrayList<>();


    private String NameSpace;
    private String Soap_Action;
    private String URL;
    private WebServiceConnection connection;
    private SoapSerializationEnvelope envelope;
    private SoapObject request;
    private HttpTransportSE transportSE;
    private Adapter material_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_material);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar;
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Material Request");
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


        pdialog = new ProgressDialog(this);


        sp = this.getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);

        Bundle b = getIntent().getExtras();
        taskId = b.getString(getString(R.string.task_number));
        textView = (AutoCompleteTextView) findViewById(R.id.search_view);
        docId = b.getString(getString(R.string.doc_number));
        //Toast.makeText(this, taskId+"  "+docId,Toast.LENGTH_SHORT).show();

        TextView doc_no = (TextView) findViewById(R.id.doc_no);
        TextView task_no = (TextView) findViewById(R.id.task_no);

        if (task_no != null) {
            task_no.setText(taskId);
        }
        if (doc_no != null) {
            doc_no.setText(docId);
        }

        Button btn = (Button) findViewById(R.id.add);
        Button btn2 = (Button) findViewById(R.id.save);

        p_des = (TextView) findViewById(R.id.product_des);
        p_code = (TextView) findViewById(R.id.product_code);
        qt = (EditText) findViewById(R.id.quantity);
        unit = (TextView) findViewById(R.id.uom);
        locationSpinner = (Spinner) findViewById(R.id.location);

        fill_list fill = new fill_list();
        fill.execute();

        location.add("Select Location");


        material_recycler = (RecyclerView) findViewById(R.id.material_recycler);


        //Log.e("seting length start", Integer.toString(list.size()));


        ArrayAdapter<String> adapter_2 = new ArrayAdapter<String>(AddMaterialActivity.this, android.R.layout.simple_spinner_item, location) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);

                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);

                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        adapter_2.setDropDownViewResource(R.layout.spinner_item);
        locationSpinner.setAdapter(adapter_2);


        /*locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

        {
            private boolean isSuccess = false;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                location = new ArrayList<String>();
                location_code = new HashMap<String, String>();
                location.add("Select Location");
                if (position > 0) {
                    try {
                        Connection con = connectionLanding.landingCONN();
                        if (con == null) {
                            Toast.makeText(getApplicationContext(), "Connection Error! Please check your connection", Toast.LENGTH_LONG).show();

                        } else {


                            String query = "SELECT LC_CD,LC_DESC FROM dbo.INLOCN WHERE LC_CompCd='001' AND LC_ALLOW_MOBILTEAM = 1 ";

                            Statement stm = con.createStatement();
                            ResultSet rs = stm.executeQuery(query);
                            while (rs.next()) {
                                location.add(rs.getString(2));
                                location_code.put(rs.getString(2), rs.getString(1));
                                isSuccess = true;

                            }


                        }

                    } catch (SQLException e) {
                        isSuccess = false;
                        e.printStackTrace();
                    }


                    if (isSuccess) {

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplication(), android.R.layout.simple_spinner_item, location) {
                            @Override
                            public boolean isEnabled(int position) {
                                if (position == 0) {
                                    return false;
                                } else {
                                    return true;
                                }
                            }

                            @Override
                            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);

                                TextView tv = (TextView) view;
                                if (position == 0) {
                                    tv.setTextColor(Color.GRAY);

                                } else {
                                    tv.setTextColor(Color.BLACK);
                                }
                                return view;
                            }
                        };

                        adapter.setDropDownViewResource(R.layout.spinner_item);
                        locationSpinner.setAdapter(adapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
                    }


                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {




            }
        });*/


        if (btn != null) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    AddCheck();


                }
            });
        }

        if (btn2 != null) {
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Check();

                }
            });
        }


        //rview =(RecyclerView)findViewById(R.id.xml);

        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/XML");
            File cert = new File(dir, "gfm_product.xml");

            //File file  = new File(myDir, "config.xml");

            // File myDir = new File(getFilesDir().getAbsolutePath());

            //File myFile = new File("xmldata.xml"); // path contains full path to the file including file name
            InputStream is = new FileInputStream(cert);


            //inputStream = this.getAssets().open("xmldata.xml");
            XmlPullParserFactory obj = XmlPullParserFactory.newInstance();
            XmlPullParser parser = obj.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            parser.setInput(is, null);
            //parse(parser);
            xmlparse xml = new xmlparse(parser);
            xml.execute();


        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }


    }


    public void AddCheck() {
        String edit = textView.getText().toString();
        String str4 = qt.getText().toString();

        boolean cancel_1 = false;

        if (TextUtils.isEmpty(edit)) {
            textView.setError("Please select the material");
            cancel_1 = true;
        }
        if (TextUtils.isEmpty(str4)) {
            qt.setError(getString(R.string.error_field_required));
            cancel_1 = true;
        }
        if (!cancel_1) {
            data = new ArrayList<AddedData>();
            data = fill();

            List<AddedData> addedDatas = new ArrayList<>();
            addedDatas.addAll(data);

            textView.setText("");
            p_code.setText("");
            p_des.setText("");
            unit.setText("");
            qt.setText("");
            material_adapter = new Adapter(AddMaterialActivity.this, addedDatas);
            material_adapter.SetOnDeleteClick(this);
            material_recycler.setAdapter(material_adapter);
            material_recycler.setLayoutManager(new LinearLayoutManager(AddMaterialActivity.this));


        }


    }

    public void Check() {

        boolean cancel = false;
        String str = p_code.getText().toString();
        String str2 = p_des.getText().toString();
        String str3 = unit.getText().toString();
        String str4 = qt.getText().toString();


        if (data.isEmpty()) {
            if (TextUtils.isEmpty(str)) {
                p_code.setError(getString(R.string.error_field_required));
                cancel = true;
            }
            if (TextUtils.isEmpty(str2)) {
                p_des.setError(getString(R.string.error_field_required));
                cancel = true;
            }
            if (TextUtils.isEmpty(str3)) {
                unit.setError(getString(R.string.error_field_required));
                cancel = true;
            }
            if (TextUtils.isEmpty(str4)) {
                qt.setError(getString(R.string.error_field_required));
                cancel = true;
            }


            if (locationSpinner.getSelectedItem() == "Select Location") {
                TextView errorText = (TextView) locationSpinner.getSelectedView();
                errorText.setError("Please seletc this");
                errorText.setTextColor(Color.RED);//just to highlight that this is an error
                errorText.setText("Please select Location");//changes the selected item text to this
                cancel = true;

            }

        } else if (!data.isEmpty() && locationSpinner.getSelectedItem() == "Select Location") {
            TextView errorText = (TextView) locationSpinner.getSelectedView();
            errorText.setError("Please seletc this");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Please select Location");//changes the selected item text to this
            cancel = true;
        } else if (!data.isEmpty() && locationSpinner.getSelectedItem() != "Select Location") {
            cancel = false;
        }


        if (!cancel) {
            if (list.isEmpty()) {
                Toast.makeText(AddMaterialActivity.this, "Please click on Add button to add some material.", Toast.LENGTH_SHORT).show();

            } else {
                Save save = new Save();
                save.execute();
            }

        }

    }

    @Override
    public void onItemClick(View v, int position) {

        material_adapter.removeAt(position);
        list.remove(position);


    }

    public boolean parseLocation(String xml) throws XmlPullParserException, IOException {

        ArrayList<LocationData> products = null;
        LocationData current = null;
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
                        current = new LocationData();
                    } else if (current != null) {
                        if (name.equals("LC_CD")) {
                            current.loc_code = parser.nextText();

                        } else if (name.equals("LC_DESC")) {
                            current.loc_des = parser.nextText();
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
            isSuccess = printLocation(products);

        }


        return isSuccess;
    }

    public boolean printLocation(ArrayList<LocationData> xmlList) {

        locationDatas = new ArrayList<>();
        boolean isSuccess = false;
        for (LocationData current : xmlList) {
            locationDatas.add(new LocationData(current.loc_code, current.loc_des));

            isSuccess = true;

        }


        return isSuccess;
    }

    public List<AddedData> fill() {


        String code = p_code.getText().toString();
        String des = p_des.getText().toString();
        String uom = unit.getText().toString();
        String count = (qt.getText().toString());


        list.add(new AddedData(code, count, R.drawable.ic_clear_black_24dp, des, uom));

        // Log.e("seting length", Integer.toString(list.size()));
        return list;
    }

    public void printProducts(ArrayList<Table> products) {
        data = new ArrayList<>();
        mDatabyCode = new HashMap<>();
        mDatabyDes = new HashMap<>();

        ArrayList<String> code_list = new ArrayList<>();
        for (Table current : products) {
            code_list.add(current.code);
            code_list.add(current.name);


            mDatabyCode.put(current.code, new MaterialData(current.code, current.name, current.uom, "10", 5));
            mDatabyDes.put(current.name, new MaterialData(current.code, current.name, current.uom, "10", 5));


        }
        textView = (AutoCompleteTextView) findViewById(R.id.search_view);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, code_list);
        textView.setAdapter(adapter1);


        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if (mDatabyDes.containsKey(textView.getText().toString())) {
                    p_code.setText(mDatabyDes.get(textView.getText().toString()).product_code);
                    p_des.setText(mDatabyDes.get(textView.getText().toString()).product_des);
                    unit.setText(mDatabyDes.get(textView.getText().toString()).uom);

                } else if (mDatabyCode.containsKey(textView.getText().toString())) {
                    p_code.setText(mDatabyCode.get(textView.getText().toString()).product_code);
                    p_des.setText(mDatabyCode.get(textView.getText().toString()).product_des);
                    unit.setText(mDatabyCode.get(textView.getText().toString()).uom);


                } else if (textView.getText().toString().isEmpty()) {
                    p_code.setText("");
                    p_des.setText("");
                    unit.setText("");

                }

            }
        });


    }

    public class fill_list extends AsyncTask<String, String, String> {

        private boolean isSuccess = false;
        private String z = "";


        @Override
        protected String doInBackground(String... params) {

            PropertyInfo compcode;

            request = new SoapObject(NameSpace, "getLocation");

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

                transportSE.call(Soap_Action + "getLocation", envelope);

                isSuccess = transportSE.responseDump != null && parseLocation(transportSE.responseDump);


            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }


            return z;

        }


        public void onPreExecute() {
            pdialog.setMessage("Loading....");
            pdialog.setIndeterminate(true);
            pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pdialog.show();
            pdialog.setCancelable(false);

        }

        public void onPostExecute(String s) {

            pdialog.dismiss();

            if (isSuccess) {
                for (int i = 0; i < locationDatas.size(); i++) {
                    location.add(locationDatas.get(i).loc_des);
                    location_code.put(locationDatas.get(i).loc_des, locationDatas.get(i).loc_des);

                }
                //Log.e("lengeth", Integer.toString(locationDatas.size()));
            } else {
                // Log.e("location", "not successful");
            }

            //Toast.makeText(AddMaterialActivity.this, z, Toast.LENGTH_LONG).show();

            /*if (isSuccess) {

                /*SharedPreferences.Editor edit = sp.edit();
                Set<String> temp = new HashSet<String>(location);
                edit.putStringSet(getString(R.string.customer_contract_number), temp);
                edit.apply();


            } else {


                Toast.makeText(BookActivity.this, "Connection error", Toast.LENGTH_LONG).show();
            }*/
        }
    }

    public class Save extends AsyncTask<String, String, String> {

        List<AddedData> list = new ArrayList<>(data);
        List<String> code = new ArrayList<>();
        List<String> p_name = new ArrayList<>();
        List<String> qtlist = new ArrayList<>();
        List<String> uom = new ArrayList<>();

        boolean isSuccess = false;
        String z = "";
        String loc_des = locationSpinner.getSelectedItem().toString();


        public void onPreExecute() {
            pdialog.setMessage("Saving....");
            pdialog.setIndeterminate(true);
            pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pdialog.show();
            pdialog.setCancelable(false);

            if (!list.isEmpty()) {

                for (int i = 0; i < list.size(); i++) {
                    code.add(list.get(i).code);
                    qtlist.add(list.get(i).quantity);
                    uom.add(list.get(i).uom);
                    p_name.add(list.get(i).des);

                }
            } else {
                Toast.makeText(AddMaterialActivity.this, "Please Add some material before requesting.", Toast.LENGTH_SHORT).show();

            }


        }

        public void onPostExecute(String r) {

            pdialog.dismiss();
            if (isSuccess && z.equalsIgnoreCase("Added")) {

                Toast.makeText(AddMaterialActivity.this, "Material has been issued", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(AddMaterialActivity.this, TaskStartActivity.class);
                i.putExtra(getString(R.string.task_number), taskId);
                i.putExtra(getString(R.string.doc_number), docId);

                startActivity(i);
                finish();


            } else {
                Toast.makeText(AddMaterialActivity.this, r, Toast.LENGTH_SHORT).show();
            }


        }

        @Override
        protected String doInBackground(String... params) {

            PropertyInfo compcode, task_id, loc_, doc_no, current_time, codeinfo, p_nameinfo, qtlistinfo, uominfo, empcode;


            for (int i = 0; i < code.size(); i++) {
                request = new SoapObject(NameSpace, "saveMaterial");
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

                loc_ = new PropertyInfo();
                loc_.setName("loc_des");
                loc_.setType(String.class);
                loc_.setValue(loc_des);
                request.addProperty(loc_);

                doc_no = new PropertyInfo();
                doc_no.setName("doc_no");
                doc_no.setType(String.class);
                doc_no.setValue(docId);
                request.addProperty(doc_no);

                String currentTime = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());

                //Log.e("date", currentTime);

                current_time = new PropertyInfo();
                current_time.setName("current_time");
                current_time.setType(String.class);
                current_time.setValue(currentTime);
                request.addProperty(current_time);

                codeinfo = new PropertyInfo();
                codeinfo.setName("c");
                codeinfo.setType(String.class);
                codeinfo.setValue(code.get(i));
                request.addProperty(codeinfo);


                p_nameinfo = new PropertyInfo();
                p_nameinfo.setName("name");
                p_nameinfo.setType(String.class);
                p_nameinfo.setValue(p_name.get(i));
                request.addProperty(p_nameinfo);


                qtlistinfo = new PropertyInfo();
                qtlistinfo.setName("qt");
                qtlistinfo.setType(String.class);
                qtlistinfo.setValue(qtlist.get(i));
                request.addProperty(qtlistinfo);


                uominfo = new PropertyInfo();
                uominfo.setName("u");
                uominfo.setType(String.class);
                uominfo.setValue(uom.get(i));
                request.addProperty(uominfo);


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
                //b = getIntent().getExtras();


                try {

                    transportSE.call(Soap_Action + "saveMaterial", envelope);
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

            }


            return null;
        }
    }

    class Table {
        public String code;
        public String name;
        public String uom;

    }

    public class xmlparse extends AsyncTask<String, String, String> {
        ArrayList<Table> products = null;
        int eventType;
        Table current = null;
        XmlPullParser parser;

        public xmlparse(XmlPullParser parser) {
            this.parser = parser;
        }

        public void onPostExecute(String s) {

            pdialog.dismiss();
            printProducts(products);
        }

        public void onPreExecute() {
            pdialog.setMessage("Loading....");
            pdialog.setIndeterminate(true);
            pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pdialog.show();
            pdialog.setCancelable(false);

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String name = null;
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            products = new ArrayList<>();
                            break;
                        case XmlPullParser.START_TAG:
                            name = parser.getName();
                            if (name.equals("Table")) {
                                current = new Table();
                            } else if (current != null) {
                                switch (name) {
                                    case "CODE":
                                        current.code = parser.nextText();

                                        break;
                                    case "NAME":
                                        current.name = parser.nextText();

                                        break;
                                    case "UOM":
                                        current.uom = parser.nextText();
                                        break;
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            name = parser.getName();
                            if (name.equalsIgnoreCase("Table") && current != null) {
                                products.add(current);
                            }
                    }
                    eventType = parser.next();

                }


            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
