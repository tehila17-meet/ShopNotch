package com.example.shopnotch.UserSession;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.example.shopnotch.Activities.UserActivities.UserAccessActivities.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class UserSession {

    private final String TAG = this.getClass().getSimpleName();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "UserSessionPref";

    public static final String FIRST_TIME = "firsttime";

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_MOBiLE = "mobile";

    public static final String KEY_CART = "cartvalue";


    public static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private FirebaseFirestore firebaseFirestore;

    // Constructor
    public UserSession(Context context){
        this.context = context;
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void createLoginSession(String name, String email, String mobile){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_MOBiLE, mobile);
        editor.commit();
    }


    public void checkLogin(){
        if(!this.isLoggedIn()){
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }

    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_MOBiLE, pref.getString(KEY_MOBiLE, null));
        return user;
    }

    public void logoutUser(){
        editor.putBoolean(IS_LOGIN,false);
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }


    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public int getCartValue(){
        return pref.getInt(KEY_CART,0);
    }


    public void increaseCartValue(){
        int val = getCartValue()+1;
        editor.putInt(KEY_CART,val);
        editor.commit();
        Log.e("Cart Value PE", "Var value : "+val+"Cart Value :"+getCartValue()+" ");
    }



    public void decreaseCartValue(){
        int val = getCartValue()-1;
        editor.putInt(KEY_CART,val);
        editor.commit();
        Log.e("Cart Value PE", "Var value : "+val+"Cart Value :"+getCartValue()+" ");
    }


    public void setCartValue(int count){
        editor.putInt(KEY_CART,count);
        editor.commit();
    }




}


