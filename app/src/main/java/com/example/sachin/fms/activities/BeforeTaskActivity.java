package com.example.sachin.fms.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sachin.fms.R;
import com.example.sachin.fms.classes.DatePicker;
import com.example.sachin.fms.classes.Logout;
import com.example.sachin.fms.classes.TimePicker;
import com.example.sachin.fms.others.WebServiceConnection;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BeforeTaskActivity extends AppCompatActivity {

    private static final int GRANT_PERMISSION = 990;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private EditText text;
    private ProgressDialog pbar;
    private TextView image_count;
    private SharedPreferences sp, sp2;
    private boolean imageClick = false;
    private List<Bitmap> bitmap = new ArrayList<>();
    private boolean flag;
    private ViewPager viewPager;
    private List<String> encodedImage = new ArrayList<>();
    private int activity;
    private String taskId, call_id;
    private List<Bitmap> originalImage = new ArrayList<>();
    private List<Uri> uriList = new ArrayList<>();
    private Uri tempUri;
    private int temp = 0;
    private boolean FAB_status = false;

    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private Animation show_fab1, hide_fab1, show_fab2, hide_fab2;
    private String[] permissionGroup;


    private String NameSpace;
    private String Soap_Action;
    private String URL;
    private WebServiceConnection connection;

    private SoapSerializationEnvelope envelope, envelope2;
    private SoapObject request, request2;
    private HttpTransportSE transportSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connection = new WebServiceConnection();
        NameSpace = connection.NameSpace;
        Soap_Action = connection.Soap_Action;
        URL = connection.URL;
        Bundle b = getIntent().getExtras();


        sp = this.getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);
        taskId = sp.getString(getString(R.string.task_number), "");


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


        EditText date = (EditText) findViewById(R.id.current_date);
        EditText time = (EditText) findViewById(R.id.current_time);


        //Log.e("CAll_no", call_id);
        //Log.e("CAll_no", taskId);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        Date d = new Date();
        String current = dateFormat.format(d);
        String[] str = current.split(" ");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_2);
        // fab3 = (FloatingActionButton) findViewById(R.id.fab_3);

        show_fab1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_fab_1);
        hide_fab1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_fab_1);
        show_fab2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_fab_2);
        hide_fab2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_fab_2);
        //show_fab3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_fab_3);
        //hide_fab3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_fab_3);

        activity = b.getInt("activity");


        //LoadAlreadySavedData load= new LoadAlreadySavedData(activity);
        // load.execute();

        sp2 = BeforeTaskActivity.this.getSharedPreferences("permission_preference", MODE_PRIVATE);


        if (fab != null) {
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
        }


        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout logout = new Logout(BeforeTaskActivity.this, sp);
                logout.execute();


                finish();

            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(BeforeTaskActivity.this, LandingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        if (date != null) {
            date.setText(str[0]);
        }
        if (time != null) {
            time.setText(str[1]);
        }
        //test=(ImageView)findViewById(R.id.test);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        if (date != null) {
            date.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.setFocusable(true);
                    v.setFocusableInTouchMode(true);

                    return false;
                }
            });
        }


        if (date != null) {
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
        }

        if (time != null) {
            time.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.setFocusable(true);
                    v.setFocusableInTouchMode(true);

                    return false;
                }
            });
        }

        if (time != null) {
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
        }


        image_count = (TextView) findViewById(R.id.image_count);
        text = (EditText) findViewById(R.id.before_work_description);
        ImageView btn_click = (ImageView) findViewById(R.id.camera_btn);
        Button btn = (Button) findViewById(R.id.before_work_save_btn);

        //pbar=(ProgressBar)findViewById(R.id.pbar);
        //pbar.setVisibility(View.GONE);
        pbar = new ProgressDialog(this);
        pbar.setMessage("Saving Data.... ");
        pbar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pbar.setIndeterminate(true);
        pbar.setCancelable(false);

        if (btn_click != null) {
            btn_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    imageClick = true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        permissionCheck();
                    } else {
                        selectImage();
                    }

                }
            });
        }


        if (btn != null) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    temp = 1;
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putInt("temp", temp);
                    edit.apply();

                        /*String img="";
                        String r= text.getText().toString();
                        String d= date.getText().toString();
                        String t= time.getText().toString();

                        if(activity ==1){
                            img="B";

                        }
                        else if(activity == 2){
                            img="A";
                        }
                        AsyncCallWS asy= new AsyncCallWS(img,r,t,d);
                        asy.execute();*/


                    check save = new check(activity);
                    save.execute();


                }
            });
        }


        if (activity == 2) {
            TextView text = (TextView) findViewById(R.id.top);
            if (text != null) {
                text.setText(getString(R.string.After_picture));
            }

        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            permissionGroup = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            };
        }

    }


    @TargetApi(Build.VERSION_CODES.M)
    public void permissionCheck() {

        // String permission = Manifest.permission.CAMERA;
        if (sp2.getBoolean("permission_camera", false)) {
            selectImage();
        } else {
            requestPermissions(permissionGroup, GRANT_PERMISSION);

        }


        //need to request permission
        //if (shouldShowRequestPermissionRationale(permission))
        // {
        //Explain to the user why we need to read the contacts
        // Snackbar.make(this, "Location access is required to show coffee shops nearby.", Snackbar.LENGTH_INDEFINITE)
        // .SetAction("OK", v => RequestPermissions(PermissionsLocation, RequestLocationId))
        ///  .Show();
        //return;
        //}
        //Finally request permissions with the list of permissions and Id



       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (p5 != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
                        /*if(p6!= PackageManager.PERMISSION_GRANTED) {
                            permissions.add(Manifest.permission.CAPTURE_VIDEO_OUTPUT);
                        }
                        if(p7!= PackageManager.PERMISSION_GRANTED) {
                            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
                        }
            if (!permissions.isEmpty()) {


                requestPermissions(permissions.toArray(new String[permissions.size()]), 20);

            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //p1 = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

            if (p2 != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CAMERA);
            }
                       /* if(p3!= PackageManager.PERMISSION_GRANTED) {
                            permissions.add(Manifest.permission.INTERNET);
                        }

                        if(p4!= PackageManager.PERMISSION_GRANTED) {
                            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        }
                        if(p5!= PackageManager.PERMISSION_GRANTED) {
                            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                        if(p6!= PackageManager.PERMISSION_GRANTED) {
                            permissions.add(Manifest.permission.CAPTURE_VIDEO_OUTPUT);
                        }
                        if(p7!= PackageManager.PERMISSION_GRANTED) {
                            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
                        }

            if (!permissions.isEmpty()) {

                requestPermissions(permissions.toArray(new String[permissions.size()]), 10);
            }

        }*/
    }


    public boolean selectImage() {

        flag = false;
        final CharSequence[] option = {"Select from Gallery", "Take Photo", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(BeforeTaskActivity.this);
        builder.setTitle("Add");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (option[which].equals("Select from Gallery")) {

                    if (Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)
                            && !Environment.getExternalStorageState().equals(
                            Environment.MEDIA_CHECKING)) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        flag = true;
                        startActivityForResult(intent, 1);

                    } else {
                        Toast.makeText(BeforeTaskActivity.this,
                                "No activity found to perform this task",
                                Toast.LENGTH_SHORT).show();
                        flag = false;
                    }


                } else if (option[which].equals("Take Photo")) {


                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

                    flag = true;
                    startActivityForResult(intent, 2);

                   /* Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    flag=true;

                    startActivityForResult(intent, 2);*/


                } else if (option[which].equals("Cancel")) {
                    dialog.dismiss();
                    flag = false;
                }
            }
        });
        builder.show();

        return flag;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResult) {

        switch (requestCode) {
            case GRANT_PERMISSION: {


                {
                    if (grantResult[0] == PackageManager.PERMISSION_GRANTED) {

                        //Snackbar.make(getCurrentFocus(),"Permission Granted: "+ permission[0],Snackbar.LENGTH_SHORT).show();

                        SharedPreferences.Editor edit = sp2.edit();
                        edit.putBoolean("permission_camera", true);
                        edit.apply();

                        selectImage();
                        //Log.d("Permission", "Permission Granted :" + permission[0]);


                    } else if (grantResult[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, "Permission Denied: " + permission[0], Toast.LENGTH_LONG).show();
                    }
                }


            }


        }


    }

    public File setFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        String currentImage = "file:" + image.getAbsolutePath();
        return image;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1) {

            switch (resultCode) {

                case RESULT_OK:


                    Uri selectedImg = data.getData();
                    uriList.add(selectedImg);

                    try {
                        InputStream stream = getContentResolver().openInputStream(selectedImg);
                        originalImage.add(BitmapFactory.decodeStream(stream));


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    ImageLoader il = new ImageLoader(selectedImg);
                    il.execute();


                    Bitmap originBitmap = null;
                    Uri selectedImage = data.getData();

                    InputStream imageStream;
                    try {
                        imageStream = getContentResolver().openInputStream(
                                selectedImage);
                        originBitmap = BitmapFactory.decodeStream(imageStream);
                    } catch (FileNotFoundException ignored) {
                    }
                    if (originBitmap != null) {

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        originBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                        byte[] imagebyte = stream.toByteArray();

                        encodedImage.add(Base64.encodeToString(imagebyte, Base64.NO_WRAP));
                        Log.e("Base64", Base64.encodeToString(imagebyte, Base64.NO_WRAP));

                    } else {
                        Toast.makeText(BeforeTaskActivity.this, "No image selected", Toast.LENGTH_LONG).show();
                    }


                    break;

                case RESULT_CANCELED:

                    //Toast.makeText(BeforeTaskActivity.this, "Image Selection is canceled by the user", Toast.LENGTH_SHORT).show();

                    break;

                default:
                    break;
            }


        } else if (requestCode == 2) {

            switch (resultCode) {

                case RESULT_OK:

                    File f = new File(Environment.getExternalStorageDirectory().toString());
                    for (File temp : f.listFiles()) {
                        if (temp.getName().equals("temp.jpg")) {
                            f = temp;

                            tempUri = Uri.fromFile(f);
                            break;
                        }
                    }
                    //Uri captureuri= data.getData();
                    Bitmap capturedimg = null;
                    try {
                        capturedimg = decodeUri(tempUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();


                    File destination = new File(Environment.getExternalStorageDirectory(),
                            System.currentTimeMillis() + ".jpg");
                    FileOutputStream fo;
                    try {
                        destination.createNewFile();
                        fo = new FileOutputStream(destination);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    if (capturedimg != null) {
                        capturedimg.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    }

                    originalImage.add(capturedimg);

                    if (capturedimg != null) {
                        capturedimg.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                    }

                    byte[] obyte = bytes.toByteArray();

                    encodedImage.add(Base64.encodeToString(obyte, Base64.NO_WRAP));


                    bitmap.add(capturedimg);


                    if (bitmap.isEmpty()) {
                        image_count.setText("0 Picture Selected");

                    } else if (bitmap.size() > 1) {
                        image_count.setText(bitmap.size() + " Pictures Selected");

                    } else {
                        image_count.setText(bitmap.size() + " Picture Selected");

                    }
                    if (imageClick && flag) {
                        fill_image_adapter(bitmap, originalImage);
                    } else {
                        Toast.makeText(BeforeTaskActivity.this, "ERROR", Toast.LENGTH_LONG).show();
                    }


                    //  CameraImage ci=new CameraImage();
                    // ci.execute();

                    break;

                case RESULT_CANCELED:
                    // Toast.makeText(BeforeTaskActivity.this, "Image Selection is canceled by the user", Toast.LENGTH_SHORT).show();

                    break;

                default:
                    break;

            }


        }


    }


    //Image loader background method

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {


        Bitmap rotatedImage = null;

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);


        // The new size we want to scale to
        final int width = 900;
        final int height = 600;
        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < width
                    || height_tmp / 2 < height) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap map = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

        try {
            ExifInterface exif = new ExifInterface(selectedImage.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    angle = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    angle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    angle = 270;
                    break;
                default:
                    angle = 0;
                    break;


            }
            Matrix matrix = new Matrix();
            if (angle == 0 && viewPager.getHeight() < map.getHeight()) {
                matrix.postRotate(90);

            } else {
                matrix.postRotate(angle);

            }
            rotatedImage = Bitmap.createBitmap(map, 0, 0, map.getWidth(), map.getHeight());


        } catch (IOException e) {
            e.printStackTrace();
        }

        return rotatedImage;


    }


    //downsize;

    @Override
    public void onResume() {
        super.onResume();

    }

    public void fill_image_adapter(List<Bitmap> bitmap, List<Bitmap> originalImage) {
        List<Bitmap> clone = new ArrayList<>(bitmap);
        List<Bitmap> imageClone = new ArrayList<>(originalImage);
        ImageAdapter imageAdapter = new ImageAdapter(BeforeTaskActivity.this, clone, imageClone);
        viewPager.setAdapter(imageAdapter);


    }

    // Fill image method to fill the pager adapter with images

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


//  Save task into database

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

      /*  FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin -= (int) (fab3.getWidth() * 0.25);
        layoutParams3.bottomMargin -= (int) (fab3.getHeight() * 1.7);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(hide_fab3);
        fab3.setClickable(false);*/

    }

    public class ImageLoader extends AsyncTask<String, String, Bitmap> {

        Uri image_uri;
        boolean isSuccess = false;
        Matrix matrix;
        private Bitmap bmap;
        private Bitmap photo;

        public ImageLoader(Uri uri) {
            this.image_uri = uri;
        }

        public void onPreExecute() {
            pbar.show();

        }

        public void onPostExecute(Bitmap r) {
            pbar.dismiss();


            if (isSuccess) {
                //Toast.makeText(BeforeTaskActivity.this,uriList.get(0).toString(),Toast.LENGTH_LONG).show();

                matrix = new Matrix();
                matrix.postRotate(90);


                Bitmap bit = Bitmap.createScaledBitmap(photo, 900, 600, false);

                //Bitmap rotate=Bitmap.createBitmap(bit,0,0,bit.getWidth(),bit.getHeight(),matrix,true);
                bitmap.add(bit);

                if (bitmap.isEmpty()) {
                    image_count.setText("0 Picture Selected");

                } else if (bitmap.size() > 1) {
                    image_count.setText(bitmap.size() + " Pictures Selected");

                } else {
                    image_count.setText(bitmap.size() + " Picture Selected");

                }
                if (imageClick && flag) {
                    fill_image_adapter(bitmap, bitmap);
                }

            }


        }

        @Override
        protected Bitmap doInBackground(String... params) {


            try {
                bmap = decodeUri(image_uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            Bitmap originBitmap = bmap;
            // Uri selectedImage = data.getData();
            if (originBitmap != null) {

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                originBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imagebyte = stream.toByteArray();
                photo = BitmapFactory.decodeByteArray(imagebyte, 0, imagebyte.length);

                //encodedImage.add(Base64.encodeToString(imagebyte, Base64.NO_WRAP));

                isSuccess = true;
            } else {
                isSuccess = false;

            }


            return photo;
        }
    }

    public class check extends AsyncTask<String, String, String> {
        int x;

        String img_type = "";

        String z = "";
        boolean isSuccess = false;


        public check(int x) {
            this.x = x;


        }

        @Override
        public void onPreExecute() {
            pbar.show();

        }

        @Override
        public void onPostExecute(String r) {
            pbar.dismiss();


            if (!z.equalsIgnoreCase("Please try again")) {
                //Log.e("without ", r);
                SaveWithoutInsert insert = new SaveWithoutInsert(activity);
                insert.execute();

            } else if (z.equalsIgnoreCase("Please try again")) {
                //Log.e("insert ", r);

                SaveBeforeTask save = new SaveBeforeTask(activity);
                save.execute();
            }


        }

        @Override
        protected String doInBackground(String... params) {
            if (x == 1) {
                img_type = "B";
            } else if (x == 2) {
                img_type = "A";
            }


            PropertyInfo call_no, taskid, compcode, imagetype;

            {
                request2 = new SoapObject(NameSpace, "check");

                call_no = new PropertyInfo();
                call_no.setName("call_no");
                call_no.setType(String.class);
                call_no.setValue(call_id);
                request2.addProperty(call_no);


                taskid = new PropertyInfo();
                taskid.setName("task_id");
                taskid.setType(String.class);
                taskid.setValue(taskId);
                request2.addProperty(taskid);


                compcode = new PropertyInfo();
                compcode.setName("compcode");
                compcode.setType(String.class);
                compcode.setValue(sp.getString(getString(R.string.company_code), ""));
                request2.addProperty(compcode);


                imagetype = new PropertyInfo();
                imagetype.setName("imagetype");
                imagetype.setType(String.class);
                imagetype.setValue(img_type);
                request2.addProperty(imagetype);


                envelope2 = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope2.setOutputSoapObject(request2);
                envelope2.dotNet = true;
                transportSE = new HttpTransportSE(URL);
                transportSE.debug = true;

                try {

                    transportSE.call(Soap_Action + "check", envelope2);
                    SoapObject result2 = (SoapObject) envelope2.getResponse();
                    if (result2.getPropertyCount() != 0) {
                        z = result2.getProperty(0).toString();

                        isSuccess = true;
                    } else {
                        z = "Please try again";
                        isSuccess = false;

                    }


                } catch (Exception e) {
                    // Log.e("error", e.getMessage());
                    //Toast.makeText(TaskStatusUpdateActivity.this,e.getMessage()+"Connection Error, Please Check your Internet Connection",Toast.LENGTH_LONG).show();

                }
            }

            return z;
        }
    }


    //Image Adapter

    public class SaveBeforeTask extends AsyncTask<String, String, String> {
        int x;
        String remarks = text.getText().toString();

        String img_type = "";

        String z = "";
        boolean isSuccess = false;
        boolean isSuccess_1 = false;

        public SaveBeforeTask(int x) {
            this.x = x;


        }

        @Override
        public void onPreExecute() {
            pbar.show();

        }

        @Override
        public void onPostExecute(String r) {
            pbar.dismiss();

            if (isSuccess_1 && isSuccess && z.equalsIgnoreCase("Saved")) {

                if (x == 1) {
                    Toast.makeText(BeforeTaskActivity.this, "Before Task Data Saved!! ", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(BeforeTaskActivity.this, TaskStartActivity.class);
                    i.putExtra(getString(R.string.task_number), taskId);
                    i.putExtra("activity", 1);
                    i.putExtra(getString(R.string.call_number), call_id);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                } else if (x == 2) {
                    Toast.makeText(BeforeTaskActivity.this, "After Task Data Saved!! ", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(BeforeTaskActivity.this, TaskStartActivity.class);
                    i.putExtra(getString(R.string.task_number), taskId);
                    i.putExtra("activity", 2);
                    i.putExtra(getString(R.string.call_number), call_id);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }


            } else {
                Toast.makeText(BeforeTaskActivity.this, "Database Error,please check your connection", Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        protected String doInBackground(String... params) {
            if (x == 1) {
                img_type = "B";
            } else if (x == 2) {
                img_type = "A";
            }


            PropertyInfo workcompleted, doc_date, call_no, taskid, compcode, imagetype, remark, Value, reported_date, building, location;

            {
                request2 = new SoapObject(NameSpace, "insert");

                call_no = new PropertyInfo();
                call_no.setName("call_no");
                call_no.setType(String.class);
                call_no.setValue(call_id);
                request2.addProperty(call_no);


                taskid = new PropertyInfo();
                taskid.setName("task_id");
                taskid.setType(String.class);
                taskid.setValue(taskId);
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
                request2.addProperty(compcode);

                imagetype = new PropertyInfo();
                imagetype.setName("imagetype");
                imagetype.setType(String.class);
                imagetype.setValue(img_type);
                request2.addProperty(imagetype);


                String currentTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US).format(Calendar.getInstance().getTime());
                // Log.e("currentTime", currentTime);

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

                //Log.e("reported date", sp.getString(getString(R.string.reported_date), "N/A"));
                // Log.e("reported date", sp.getString(getString(R.string.reported_time), "N/A"));
                // Log.e("reported date", sp.getString(getString(R.string.reported_datetime), "N/A"));


                DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
                DateFormat format2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
                String date = "";
                try {
                    String temp = sp.getString(getString(R.string.reported_date), "null") + " " + sp.getString(getString(R.string.reported_time), "null");

                    Date d = format.parse(temp);
                    date = format2.format(d);

                    //Log.e("reported date formatted", date);

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
                        isSuccess = false;

                        z = "Please try again";
                    }
                    if (isSuccess && z.equalsIgnoreCase("inserted")) {

                        for (int i = 0; i < encodedImage.size(); i++) {
                            request = new SoapObject(NameSpace, "saveImage");

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

                            imagetype = new PropertyInfo();
                            imagetype.setName("imagetype");
                            imagetype.setType(String.class);
                            imagetype.setValue(img_type);
                            request.addProperty(imagetype);


                            remark = new PropertyInfo();
                            remark.setName("remarks");
                            remark.setType(String.class);
                            remark.setValue(remarks);
                            request.addProperty(remark);

                            Value = new PropertyInfo();
                            Value.setName("Value");
                            Value.setType(String.class);
                            Value.setValue(encodedImage.get(i));
                            request.addProperty(Value);


                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.setOutputSoapObject(request);
                            envelope.dotNet = true;
                            transportSE = new HttpTransportSE(URL);
                            transportSE.debug = true;
                            try {
                                transportSE.call(Soap_Action + "saveImage", envelope);
                                SoapObject result = (SoapObject) envelope.getResponse();
                                if (result.getPropertyCount() != 0 && result.getProperty(0).toString().equalsIgnoreCase("Saved")) {
                                    z = result.getProperty(0).toString();
                                    isSuccess_1 = true;

                                } else {
                                    isSuccess_1 = false;
                                }
                            } catch (Exception e) {
                                isSuccess_1 = false;

                                //Log.e("error", e.getMessage());

                            }

                        }


                    }


                } catch (Exception e) {
                    // Log.e("error", e.getMessage());
                    ///Toast.makeText(TaskStatusUpdateActivity.this,e.getMessage()+"Connection Error, Please Check your Internet Connection",Toast.LENGTH_LONG).show();

                }
            }

            return z;
        }
    }

    public class SaveWithoutInsert extends AsyncTask<String, String, String> {
        int x;
        String remarks = text.getText().toString();

        String img_type = "";

        String z = "";
        boolean isSuccess = false;
        boolean isSuccess_1 = false;

        public SaveWithoutInsert(int x) {
            this.x = x;


        }

        @Override
        public void onPreExecute() {
            pbar.show();

        }

        @Override
        public void onPostExecute(String r) {
            pbar.dismiss();

            if (isSuccess_1) {

                if (x == 1) {
                    Toast.makeText(BeforeTaskActivity.this, "Before Task Data Saved!! ", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(BeforeTaskActivity.this, TaskStartActivity.class);
                    i.putExtra(getString(R.string.task_number), taskId);
                    i.putExtra("activity", 1);
                    i.putExtra(getString(R.string.call_number), call_id);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                } else if (x == 2) {
                    Toast.makeText(BeforeTaskActivity.this, "After Task Data Saved!! ", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(BeforeTaskActivity.this, TaskStartActivity.class);
                    i.putExtra(getString(R.string.task_number), taskId);
                    i.putExtra("activity", 2);
                    i.putExtra(getString(R.string.call_number), call_id);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }


            } else {
                Toast.makeText(BeforeTaskActivity.this, "Database Error,please check your connection", Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        protected String doInBackground(String... params) {
            if (x == 1) {
                img_type = "B";
            } else if (x == 2) {
                img_type = "A";
            }


            PropertyInfo call_no, taskid, compcode, imagetype, remark, Value;

            {
                for (int i = 0; i < encodedImage.size(); i++) {
                    request = new SoapObject(NameSpace, "saveImage");

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

                    imagetype = new PropertyInfo();
                    imagetype.setName("imagetype");
                    imagetype.setType(String.class);
                    imagetype.setValue(img_type);
                    request.addProperty(imagetype);


                    remark = new PropertyInfo();
                    remark.setName("remarks");
                    remark.setType(String.class);
                    remark.setValue(remarks);
                    request.addProperty(remark);

                    Value = new PropertyInfo();
                    Value.setName("Value");
                    Value.setType(String.class);
                    Value.setValue(encodedImage.get(i));
                    request.addProperty(Value);


                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.setOutputSoapObject(request);
                    envelope.dotNet = true;

                    try {
                        transportSE.call(Soap_Action + "saveImage", envelope);
                        SoapObject result = (SoapObject) envelope.getResponse();
                        if (result.getPropertyCount() != 0 && result.getProperty(0).toString().equalsIgnoreCase("Saved")) {
                            z = result.getProperty(0).toString();
                            isSuccess_1 = true;

                        } else {
                            isSuccess_1 = false;
                        }
                    } catch (Exception e) {
                        isSuccess_1 = false;

                        //Log.e("error", e.getMessage());

                    }

                }


            }

            return z;
        }
    }

    public class ImageAdapter extends PagerAdapter {


        List<Bitmap> bitmapList = Collections.emptyList();
        Context context;
        List<Bitmap> imageClone = Collections.emptyList();


        public ImageAdapter(Context context, List<Bitmap> bitmapList, List<Bitmap> imageClone) {


            this.context = context;
            this.bitmapList = bitmapList;
            this.imageClone = imageClone;
        }

        @Override
        public int getCount() {
            return bitmapList.size();
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {


            final ImageView imageView = new ImageView(context);
            //int padding = context.getResources().getDimensionPixelSize(
            //  R.dimen.activity_horizontal_margin);
            // imageView.setPadding(padding, padding, padding, padding);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);


            //Bitmap bit = BitmapFactory.decodeFile(list.get(position));

            imageView.setImageBitmap(bitmapList.get(position));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog nagDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                    nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    nagDialog.setCancelable(false);
                    nagDialog.setContentView(R.layout.content_edit);
                    ImageButton btnClose = (ImageButton) nagDialog.findViewById(R.id.imageButton);

                    ImageView ivPreview = (ImageView) nagDialog.findViewById(R.id.iv_preview_image);
                    ivPreview.setImageBitmap(imageClone.get(position));
                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {

                            nagDialog.dismiss();
                        }
                    });
                    nagDialog.show();


                    //Intent intent= new Intent(Intent.ACTION_VIEW,uriList.get(position));
                    //context.startActivity(intent);
                    //Toast.makeText(context,uriList.get(position).toString(),Toast.LENGTH_LONG).show();
                }
            });
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete").setMessage("Are you sure you want to Delete this Picture")
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (imageClone.isEmpty()) {
                                        Toast.makeText(context, "Sorry There are no images to delete", Toast.LENGTH_LONG).show();
                                    } else {
                                        imageClone.remove(position);
                                        bitmapList.remove(position);
                                        bitmap.remove(position);
                                        encodedImage.remove(position);
                                        originalImage.remove(position);
                                        notifyDataSetChanged();
                                        dialog.dismiss();
                                    }

                                }
                            }).create().show();


                    return true;
                }
            });
            imageView.setImageBitmap(bitmapList.get(position));

            if (bitmap.isEmpty()) {
                image_count.setText("0 Picture Selected");

            } else if (bitmap.size() > 1) {
                image_count.setText(bitmap.size() + " Pictures Selected");

            } else {
                image_count.setText(bitmap.size() + " Picture Selected");

            }
            container.addView(imageView, 0);

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }


    }

    public class LoadImageAdapter extends PagerAdapter {

        List<Bitmap> bitmap = Collections.emptyList();
        Context context;

        public LoadImageAdapter(Context context, List<Bitmap> bitmap) {
            this.context = context;
            this.bitmap = bitmap;

        }

        @Override
        public int getCount() {
            return bitmap.size();
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {


            final ImageView imageView = new ImageView(context);
            //int padding = context.getResources().getDimensionPixelSize(
            //  R.dimen.activity_horizontal_margin);
            // imageView.setPadding(padding, padding, padding, padding);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);


            //Bitmap bit = BitmapFactory.decodeFile(list.get(position));

            imageView.setImageBitmap(bitmap.get(position));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog nagDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                    nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    nagDialog.setCancelable(false);
                    nagDialog.setContentView(R.layout.content_edit);
                    ImageButton btnClose = (ImageButton) nagDialog.findViewById(R.id.imageButton);

                    ImageView ivPreview = (ImageView) nagDialog.findViewById(R.id.iv_preview_image);
                    ivPreview.setImageBitmap(bitmap.get(position));
                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {

                            nagDialog.dismiss();
                        }
                    });
                    nagDialog.show();


                    //Intent intent= new Intent(Intent.ACTION_VIEW,uriList.get(position));
                    //context.startActivity(intent);
                    //Toast.makeText(context,uriList.get(position).toString(),Toast.LENGTH_LONG).show();
                }
            });


            container.addView(imageView, 0);

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }


    }






   /* private class AsyncCallWS extends AsyncTask<String, Void, String> {

        String imageType="";
        String r="";
        String t,d;
        public AsyncCallWS(String type,String r,String t,String d){
            this.imageType= type;
            this.r=r;
            this.t=t;
            this.d=d;

        }

        @Override
        protected String doInBackground(String... params) {
            PropertyInfo taskid,empcode,imagetype,remarks,time,date,ImgValue;

            request = new SoapObject(NameSpace, "HelloWorld");
            // creating the SoapObject and "Hello" is the method name of the webservice which will called.
            taskid = new PropertyInfo();
            taskid.setName("taskid"); // setting the variable name
            taskid.setValue(taskId);// setting the value
            taskid.setType(String.class);// setting the type
            request.addProperty(taskid);// adding the property


            empcode = new PropertyInfo();
            empcode.setName("empcode"); // setting the variable name
            empcode.setValue(sp.getString(getString(R.string.employee_code),""));// setting the value
            empcode.setType(String.class);// setting the type
            request.addProperty(empcode);// adding the property

            imagetype = new PropertyInfo();
            imagetype.setName("imagetype"); // setting the variable name
            imagetype.setValue(imageType);// setting the value
            imagetype.setType(String.class);// setting the type
            request.addProperty(imagetype);// adding the property

            remarks = new PropertyInfo();
            remarks.setName("remarks"); // setting the variable name
            remarks.setValue(r);// setting the value
            remarks.setType(String.class);// setting the type
            request.addProperty(remarks);// adding the property

            time = new PropertyInfo();
            time.setName("time"); // setting the variable name
            time.setValue(t);// setting the value
            time.setType(String.class);// setting the type
            request.addProperty(time);// adding the property

            date = new PropertyInfo();
            date.setName("taskid"); // setting the variable name
            date.setValue(d);// setting the value
            date.setType(String.class);// setting the type
            request.addProperty(date);// adding the property


            String[] temp=new String[encodedImage.size()];
            encodedImage.toArray(temp);

            for(int i = 0;i<temp.length;i++){

            }
            ImgValue = new PropertyInfo();
            ImgValue.setName("ImgValue"); // setting the variable name
            ImgValue.setValue(temp);// setting the value
            request.addProperty(ImgValue);// adding the property







            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            // creating a serialized envelope which will be used to carry the
            // parameters for SOAP body and call the method
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            transportSE = new HttpTransportSE(uri);
            try {
                transportSE.call(Soap_Action + "HelloWorld", envelope); // Hello
                response = (SoapPrimitive) envelope.getResponse();
                serverResponse = response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                serverResponse = e.toString();
            }
            return serverResponse;
        }
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(BeforeTaskActivity.this,result,Toast.LENGTH_SHORT).show();
            pbar.dismiss();
            if(imageType.equals("B")){
                clicks= true;
                Toast.makeText(BeforeTaskActivity.this,"Before Task Data Saved!! ",Toast.LENGTH_SHORT).show();
                Intent i= new Intent(BeforeTaskActivity.this,TaskStartActivity.class);
                i.putExtra("edit_before",true);
                i.putExtra("this",1);
                i.putExtra(getString(R.string.task_number),taskId);

                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
            else if(imageType.equals("A")){
                clicks= true;
                Toast.makeText(BeforeTaskActivity.this,"After Task Data Saved!! ",Toast.LENGTH_SHORT).show();
                Intent i= new Intent(BeforeTaskActivity.this,TaskStartActivity.class);
                i.putExtra("edit_after",true);
                i.putExtra("this",2);
                i.putExtra(getString(R.string.task_number),taskId);

                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }

        }
        @Override
        protected void onPreExecute() {
            pbar.show();
        }
        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }*/


}



