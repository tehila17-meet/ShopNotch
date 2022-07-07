package com.example.shopnotch.Activities.UserActivities.UserAccessActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopnotch.Activities.MainActivity;
import com.example.shopnotch.Utilities.GeneralUtils.CheckInternetConnection;
import com.example.shopnotch.R;
import com.example.shopnotch.UserSession.UserSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaopiz.kprogresshud.KProgressHUD;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    private EditText edtemail,edtpass;
    private String email,pass;
    private TextView appname,forgotpass,registernow;
    private UserSession session;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser user;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        new CheckInternetConnection(this).checkConnection();

        FirebaseApp.initializeApp(LoginActivity.this);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Typeface typeface = ResourcesCompat.getFont(this, R.font.rooster);
        appname = findViewById(R.id.appname);
        appname.setTypeface(typeface);

        edtemail= findViewById(R.id.email);
        edtpass= findViewById(R.id.password);

        session= new UserSession(getApplicationContext());


        Button login_button=findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email=edtemail.getText().toString();
                pass=edtpass.getText().toString();

                final KProgressHUD progressDialog=  KProgressHUD.create(LoginActivity.this)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setLabel("Logging You In!")
                            .setCancellable(false)
                            .setAnimationSpeed(2)
                            .setDimAmount(0.5f)
                            .show();

                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    boolean isEmailVerified = checkIfEmailVerified();
                                if(!isEmailVerified){
                                    progressDialog.dismiss();
                                    Toasty.error(LoginActivity.this,"Email Not Verified.",2000).show();
                                } else {
                                    userID = mAuth.getCurrentUser().getUid();
                                    session.createLoginSession(userID,email,"0");

                                    progressDialog.dismiss();

                                    Intent loginSuccess = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(loginSuccess);
                                    finish();

                                }

                            } else {
                                progressDialog.dismiss();
                                Toasty.error(LoginActivity.this,"Couldn't Log In. Please check your Email/Password",2000).show();
                            }
                        }
                    });
            }
        });

        //GOTO REGISTRATION BUTTON
        registernow= findViewById(R.id.register_now);
        registernow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        //GOTO FORGOT PASSWORD
        forgotpass=findViewById(R.id.forgot_pass);
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    private boolean checkIfEmailVerified() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        return user.isEmailVerified();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Login CheckPoint","LoginActivity resumed");
        new CheckInternetConnection(this).checkConnection();
    }

    @Override
    protected void onStop () {
        super.onStop();
        Log.e("Login CheckPoint","LoginActivity stopped");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
