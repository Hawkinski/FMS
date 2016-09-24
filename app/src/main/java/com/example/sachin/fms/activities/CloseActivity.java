package com.example.sachin.fms.activities;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.sachin.fms.R;
import com.example.sachin.fms.classes.DatePicker;
import com.example.sachin.fms.classes.DrawCanvas;
import com.example.sachin.fms.classes.Logout;
import com.example.sachin.fms.classes.TimePicker;
import com.example.sachin.fms.dataSets.TaskListData;
import com.example.sachin.fms.others.WebServiceConnection;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CloseActivity extends AppCompatActivity {
    DrawCanvas sign, draw;


    private Button btn, save;
    private EditText date, time, text;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private SharedPreferences sp;
    private ProgressDialog pdialog;
    private LinearLayout scroll;
    private Bitmap bitmap;
    private int activity;
    private String taskId, call_id;
    private String encodedString;


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
    private TaskListData listData = new TaskListData();


    private FloatingActionButton fab, fab1, fab2;
    private Animation show_fab1, hide_fab1, show_fab2, hide_fab2;
    private boolean FAB_status = false;
    private Bundle b;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connection = new WebServiceConnection();
        NameSpace = connection.NameSpace;
        Soap_Action = connection.Soap_Action;
        URL = connection.URL;


        b = getIntent().getExtras();
        sp = this.getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);

        taskId = sp.getString(getString(R.string.task_number), "null");
        call_id = sp.getString(getString(R.string.call_number), "null");

        ActionBar actionBar;
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Task No. " + taskId);
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


        pdialog = new ProgressDialog(this);
        pdialog.setMessage("Saving...");
        pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdialog.setIndeterminate(true);
        pdialog.setCancelable(false);

        //getSupportActionBar().setTitle("Task No. " + taskId);


        sign = (DrawCanvas) findViewById(R.id.drawing);
        btn = (Button) findViewById(R.id.button);
        // nestedScroll=(NestedScrollView)findViewById(R.id.nestedScroll);


        //Log.e("CALL _ NO ", call_id);
//

        date = (EditText) findViewById(R.id.current_date);
        text = (EditText) findViewById(R.id.before_work_description);
        time = (EditText) findViewById(R.id.current_time);

        save = (Button) findViewById(R.id.before_work_save_btn);
        scroll = (LinearLayout) findViewById(R.id.scroll);
        activity = b.getInt("activity");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        final Date d = new Date();
        String current = dateFormat.format(d);
        String[] str = current.split(" ");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_2);
        //fab3 = (FloatingActionButton) findViewById(R.id.fab_3);

        show_fab1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_fab_1);
        hide_fab1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_fab_1);
        show_fab2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_fab_2);
        hide_fab2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_fab_2);
        // show_fab3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_fab_3);
        // hide_fab3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_fab_3);

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout logout = new Logout(CloseActivity.this, sp);
                logout.execute();


                finish();

            }
        });


        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(CloseActivity.this, LandingActivity.class);
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
        date.setText(str[0]);
        time.setText(str[1]);
        //test=(ImageView)findViewById(R.id.test);

        date.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);

                return false;
            }
        });

        text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);

                return false;
            }
        });


        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    datePicker = new DatePicker(v);
                    FragmentTransaction F = getFragmentManager().beginTransaction();
                    datePicker.show(F, "DatePicker");

                }
            }
        });

        time.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);

                return false;
            }
        });

        time.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    timePicker = new TimePicker(v);
                    FragmentTransaction F = getFragmentManager().beginTransaction();
                    timePicker.show(F, "TimePicker");
                }

            }
        });

        //draw= new DrawCanvas(this,null);
        //scroll.addView(draw);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                sign.ClearCanvas();
                // LinearLayout.LayoutParams p=(LinearLayout.LayoutParams)sign.getLayoutParams();

                // scroll.updateViewLayout(getCurrentFocus(),p);


            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // View view = new DrawCanvas(CloseActivity.this);
                //bitmap = Bitmap.createBitmap(scroll.getWidth(), 200, Bitmap.Config.ARGB_8888);
                // Canvas canvas = new Canvas(bitmap);
                // view.draw(canvas);
                // ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                HashMap<String, Bitmap> _list;
                _list = sign.getBitmap();

                bitmap = _list.get("sign");

                if (bitmap.sameAs(_list.get("empty"))) {
                    Toast.makeText(CloseActivity.this, "Please take the signature before closing the task", Toast.LENGTH_LONG).show();
                    // Log.e("sign", _list.get("sign").toString());
                    /// Log.e("empty", _list.get("empty").toString());

                } else {

                    /////Log.e("empty", _list.get("empty").toString());
                    /// Log.e("sign", _list.get("sign").toString());

                    byte[] b = sign.getBytes();

                    encodedString = Base64.encodeToString(b, Base64.DEFAULT);
                    //Toast.makeText(CloseActivity.this, encodedString, Toast.LENGTH_LONG).show();


                    SaveBeforeTask save = new SaveBeforeTask(activity);
                    save.execute();
                }


            }
        });

    }

    /*@Override
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

                SharedPreferences sp = this.getSharedPreferences(getString(R.string.preferences), MODE_APPEND);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();

                Intent i = new Intent(this, LoginActivity.class);
                i.putExtra("finish", true); // if you are checking for this in your other Activities
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
                break;
            case R.id.HOME:
                finish();
                Intent intent = new Intent(this, LandingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

                break;
            default:

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
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

      /*  FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
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

    public class SaveBeforeTask extends AsyncTask<String, String, String> {
        int x;
        String remarks = text.getText().toString();

        String img_type = "";
        String tdi_date = date.getText().toString();

        String z = "";
        boolean isRated = false;
        boolean isSuccess = false;
        boolean isSuccess_1 = false;

        public SaveBeforeTask(int x) {
            this.x = x;


        }

        @Override
        public void onPreExecute() {
            pdialog.show();

        }

        @Override
        public void onPostExecute(String r) {
            pdialog.dismiss();
            if (isSuccess && isSuccess_1 && z.equalsIgnoreCase("Saved")) {


                //Create custom dialog object
                final Dialog dialog = new Dialog(CloseActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.feedback_dialog_layout);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                // Set the values


                RelativeLayout img1 = (RelativeLayout) dialog.findViewById(R.id.r1);
                RelativeLayout img2 = (RelativeLayout) dialog.findViewById(R.id.r2);
                RelativeLayout img3 = (RelativeLayout) dialog.findViewById(R.id.r3);
                RelativeLayout img4 = (RelativeLayout) dialog.findViewById(R.id.r4);
                RelativeLayout img5 = (RelativeLayout) dialog.findViewById(R.id.r5);

                dialog.show();

                img1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SaveFeedBack f = new SaveFeedBack("E");
                        f.execute();
                        dialog.dismiss();
                    }
                });

                img2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SaveFeedBack f = new SaveFeedBack("G");
                        f.execute();
                        dialog.dismiss();

                    }
                });

                img3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SaveFeedBack f = new SaveFeedBack("A");
                        f.execute();
                        dialog.dismiss();

                    }
                });

                img4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SaveFeedBack f = new SaveFeedBack("P");
                        f.execute();
                        dialog.dismiss();

                    }
                });

                img5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SaveFeedBack f = new SaveFeedBack("VP");
                        f.execute();
                        dialog.dismiss();

                    }
                });


            } else {
                Toast.makeText(CloseActivity.this, z, Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        protected String doInBackground(String... params) {
            img_type = "C";


            PropertyInfo workcompleted, doc_date, call_no, taskid, compcode, imagetype, remark, Value, reported_date, building, location;

            {
                request = new SoapObject(NameSpace, "saveImage");
                request2 = new SoapObject(NameSpace, "insert");

                call_no = new PropertyInfo();
                call_no.setName("call_no");
                call_no.setType(String.class);
                call_no.setValue(call_id);
                request.addProperty(call_no);
                request2.addProperty(call_no);


                taskid = new PropertyInfo();
                taskid.setName("task_id");
                taskid.setType(String.class);
                taskid.setValue(taskId);
                request.addProperty(taskid);
                request2.addProperty(taskid);


                workcompleted = new PropertyInfo();
                workcompleted.setName("workcompleted");
                workcompleted.setType(String.class);
                workcompleted.setValue(sp.getString(getString(R.string.work_completed_code), ""));
                request2.addProperty(workcompleted);

                compcode = new PropertyInfo();
                compcode.setName("compcode");
                compcode.setType(String.class);
                compcode.setValue(sp.getString(getString(R.string.company_code), ""));
                request.addProperty(compcode);
                request2.addProperty(compcode);

                imagetype = new PropertyInfo();
                imagetype.setName("imagetype");
                imagetype.setType(String.class);
                imagetype.setValue(img_type);
                request.addProperty(imagetype);
                request2.addProperty(imagetype);


                remark = new PropertyInfo();
                remark.setName("remarks");
                remark.setType(String.class);
                remark.setValue(remarks);
                request.addProperty(remark);

                Value = new PropertyInfo();
                Value.setName("Value");
                Value.setType(String.class);
                Value.setValue(encodedString);
                request.addProperty(Value);

                String currentTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US).format(Calendar.getInstance().getTime());
                //Log.e("currentTime", currentTime);

                doc_date = new PropertyInfo();
                doc_date.setName("doc_date");
                doc_date.setType(String.class);
                doc_date.setValue(currentTime);
                request2.addProperty(doc_date);


                building = new PropertyInfo();
                building.setName("building");
                building.setType(String.class);
                building.setValue(sp.getString(getString(R.string.building_code), "N/A"));
                request2.addProperty(building);


                DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
                DateFormat format2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
                String date = "";
                try {
                    String temp = sp.getString(getString(R.string.reported_date), "null") + " " + sp.getString(getString(R.string.reported_time), "null");

                    Date d = format.parse(temp);
                    date = format2.format(d);

                    // Log.e("reported date formatted", date);

                } catch (ParseException e) {
                    e.printStackTrace();
                }


                reported_date = new PropertyInfo();
                reported_date.setName("reported_date");
                reported_date.setType(String.class);
                reported_date.setValue(date);
                request2.addProperty(reported_date);

                location = new PropertyInfo();
                location.setName("location");
                location.setType(String.class);
                location.setValue(sp.getString(getString(R.string.location_code), "N/A"));
                request2.addProperty(location);


                envelope2 = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope2.setOutputSoapObject(request2);
                envelope2.dotNet = true;
                transportSE = new HttpTransportSE(URL);
                transportSE.debug = true;

                try {

                    transportSE.call(Soap_Action + "insert", envelope2);
                    SoapObject result2 = (SoapObject) envelope2.getResponse();
                    if (result2.getPropertyCount() != 0 && result2.getProperty(0).toString().equalsIgnoreCase("inserted")) {
                        z = result2.getProperty(0).toString();
                        isSuccess = true;

                    } else {
                        z = "Please try again";
                        isSuccess = false;
                    }
                    if (isSuccess && z.equalsIgnoreCase("inserted")) {
                        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope.setOutputSoapObject(request);
                        envelope.dotNet = true;
                        transportSE.call(Soap_Action + "saveImage", envelope);
                        SoapObject result = (SoapObject) envelope.getResponse();
                        if (result.getPropertyCount() != 0 && result.getProperty(0).toString().equalsIgnoreCase("Saved")) {
                            z = result.getProperty(0).toString();
                            isSuccess_1 = true;

                        } else {
                            isSuccess_1 = false;
                        }
                    }


                } catch (Exception e) {
                    // Log.e("error", e.getMessage());

                }
            }

            return z;
        }
    }

    public class SaveFeedBack extends AsyncTask<String, String, String> {

        String code = "";
        String z = "";
        boolean isSuccess = false;

        public SaveFeedBack(String code) {
            this.code = code;
        }

        public void onPreExecute() {

            pdialog.show();
        }

        public void onPostExecute(String s) {
            pdialog.dismiss();
            if (isSuccess) {
                Toast.makeText(CloseActivity.this, "Task has been Completed!", Toast.LENGTH_SHORT).show();


                //Create custom dialog object

                {
                    Intent i = new Intent(CloseActivity.this, TaskStartActivity.class);
                    i.putExtra("edit_after", true);
                    i.putExtra("this", 2);
                    i.putExtra(getString(R.string.task_number), taskId);

                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();

                }


            } else {
                Toast.makeText(CloseActivity.this, z, Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        protected String doInBackground(String... params) {
            PropertyInfo feedback_code, call_no, taskid, compcode;


            request = new SoapObject(NameSpace, "saveFeedBack");

            call_no = new PropertyInfo();
            call_no.setName("call_no");
            call_no.setType(String.class);
            call_no.setValue(call_id);
            request.addProperty(call_no);


            taskid = new PropertyInfo();
            taskid.setName("task_id");
            taskid.setType(String.class);
            taskid.setValue(taskId);
            request.addProperty(taskid);


            compcode = new PropertyInfo();
            compcode.setName("compcode");
            compcode.setType(String.class);
            compcode.setValue(sp.getString(getString(R.string.company_code), ""));
            request.addProperty(compcode);


            feedback_code = new PropertyInfo();
            feedback_code.setName("feedback_code");
            feedback_code.setType(String.class);
            feedback_code.setValue(code);
            request.addProperty(feedback_code);


            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            transportSE = new HttpTransportSE(URL);
            transportSE.debug = true;

            try {

                transportSE.call(Soap_Action + "saveFeedBack", envelope);
                SoapObject result2 = (SoapObject) envelope.getResponse();
                if (result2.getPropertyCount() != 0 && result2.getProperty(0).toString().equalsIgnoreCase("inserted")) {
                    z = result2.getProperty(0).toString();
                    isSuccess = true;

                } else {
                    z = "Please try again";
                    isSuccess = false;
                }


            } catch (Exception e) {
                //Log.e("error", e.getMessage());
                Toast.makeText(CloseActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

            }
            return z;

        }


    }


}