package com.example.shopnotch.Activities.UserActivities.UserAccessActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.shopnotch.Activities.ClothingProductsActivities.IndividualProductActivity;
import com.example.shopnotch.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import es.dmoral.toasty.Toasty;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText email = findViewById(R.id.email);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

    }

    public void sendResetLink(View view){
        Toasty.success(ForgotPasswordActivity.this, "Sent Password Reset Mail To: " + email, 2000).show();

    }
}
