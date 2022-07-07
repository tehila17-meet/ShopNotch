package com.example.shopnotch.Activities.UserActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.shopnotch.Utilities.GeneralUtils.CheckInternetConnection;
import com.example.shopnotch.R;
import com.example.shopnotch.UserSession.UserSession;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView namebutton;
    private CircleImageView primage;
    private ImageSlider imageSlider;

    //to get user session data
    private UserSession session;
    private TextView tvemail,tvphone;
    private HashMap<String,String> user;
    private String name,email,mobile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initialize();

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        //retrieve session values and display on listviews
        getValues();

        //ImageSLider
        generateImageSlider();

    }

    private void generateImageSlider() {

        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel("https://media.gq.com/photos/620ff1a2f59deb0e8e8345bd/master/pass/shirts.jpg"));
        slideModels.add(new SlideModel("https://img.freepik.com/free-photo/female-legs-heel-sneaker-yellow-background_185193-83306.jpg?w=2000"));
        slideModels.add(new SlideModel("https://media.istockphoto.com/photos/photo-of-nice-lady-hiding-eyes-with-hood-sending-air-kiss-wear-yellow-picture-id1166158026?k=20&m=1166158026&s=612x612&w=0&h=sRdjZ2pjryfdNE_QpELpXlaO5a9FbEW-Oc73SGzAFHc="));
        slideModels.add(new SlideModel("https://img.freepik.com/free-photo/magnificent-woman-long-bright-skirt-dancing-studio-carefree-inspired-female-model-posing-with-pleasure-yellow_197531-11084.jpg?w=2000"));
        slideModels.add(new SlideModel("https://img.freepik.com/free-photo/pretty-hispanic-woman-with-bronze-skin-waving-hands-while-sitting-longboard-inspired-latin-girl-sunglasses-posing-skateboard_197531-4153.jpg"));
        slideModels.add(new SlideModel("https://wi.wallpapertip.com/wsimgs/25-255529_spring-summer-2020-mens-sport-fashion-trends.png"));

        imageSlider.setImageList(slideModels,true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initialize() {

        imageSlider = findViewById(R.id.slider);
        tvemail=findViewById(R.id.emailview);
        tvphone=findViewById(R.id.mobileview);
        namebutton=findViewById(R.id.name_button);



    }

    public void viewCart(View view) {
        //startActivity(new Intent(ProfileActivity.this,Cart.class));
        //finish();
    }



    private void getValues() {

        //create new session object by passing application context
        session = new UserSession(getApplicationContext());

        //validating session
        session.isLoggedIn();

        //get User details if logged in
        user = session.getUserDetails();

        name=user.get(UserSession.KEY_NAME);
        email=user.get(UserSession.KEY_EMAIL);
        mobile=user.get(UserSession.KEY_MOBiLE);

        //setting values
        tvemail.setText(email);
        tvphone.setText(mobile);
        namebutton.setText(name);


    }
}
