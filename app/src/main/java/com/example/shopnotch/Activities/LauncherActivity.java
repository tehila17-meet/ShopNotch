package com.example.shopnotch.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;


import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.shopnotch.Activities.UserActivities.UserAccessActivities.LoginActivity;
import com.example.shopnotch.R;
import com.example.shopnotch.UserSession.UserSession;

public class LauncherActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    private TextView appname;
    private UserSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        session =new UserSession(LauncherActivity.this);

        appname= findViewById(R.id.appname);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.blacklist);
        appname.setTypeface(typeface);

        YoYo.with(Techniques.Bounce)
                .duration(7000)
                .playOn(findViewById(R.id.logo));

        YoYo.with(Techniques.FadeInUp)
                .duration(5000)
                .playOn(findViewById(R.id.appname));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    if(session.isLoggedIn()) {
                        Intent i = new Intent(LauncherActivity.this, MainActivity.class);
                        startActivity(i);
                    }else{
                        Intent i = new Intent(LauncherActivity.this, LoginActivity.class);
                        startActivity(i);
                    }
                    finish();

            }
        }, SPLASH_TIME_OUT);

    }
}
