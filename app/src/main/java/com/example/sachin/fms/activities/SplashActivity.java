package com.example.sachin.fms.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.sachin.fms.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private ImageView rotate;
    private SharedPreferences sp;

    private Timer timer;
    private MyTimerTask myTimerTask;
    private String cd, newtask;

    private View root_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        root_view = findViewById(R.id.root_view);
        // launchTestService();


        sp = this.getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);
        cd = sp.getString(getString(R.string.user_cd), null);


        rotate = (ImageView) findViewById(R.id.rotate);
        rotate.post(
                new Runnable() {

                    @Override
                    public void run() {
                        rotate.startAnimation(AnimationUtils.loadAnimation(SplashActivity.this, R.anim.rotate));
                    }
                });

        //Calculate the total duration
        int duration = 0;
        ConnectivityManager cm = (ConnectivityManager) SplashActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            Snackbar.make(root_view, "Internet connection is not available", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Action", null).show();
            //Toast.makeText(SplashActivity.this, "Internet connection is not available", Toast.LENGTH_SHORT).show();
        } else {
            timer = new Timer();
            myTimerTask = new MyTimerTask();
            timer.schedule(myTimerTask, 1600);
        }


    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {

            timer.cancel();


            if (cd == null) {
                Intent intent = new Intent(
                        SplashActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(
                        SplashActivity.this, LandingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish();
            }


        }
    }
}
