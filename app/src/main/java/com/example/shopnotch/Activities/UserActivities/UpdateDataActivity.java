package com.example.shopnotch.Activities.UserActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopnotch.Utilities.GeneralUtils.CheckInternetConnection;
import com.example.shopnotch.R;
import com.example.shopnotch.UserSession.UserSession;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class UpdateDataActivity extends AppCompatActivity {

    private Button button;
    private EditText edtname,edtemail,edtmobile;
    private TextView namebutton;
    private String name,email,photo,mobile,newemail;
    private String check;

    //to get user session data
    private UserSession session;
    private HashMap<String,String> user;
    private Button updateProfileBtn;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        initialize();

        //retrieve session values and display on listviews
        getValues();



        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* */

                if (validateName() && validateEmail() && validateNumber()) {

                    final KProgressHUD progressDialog=  KProgressHUD.create(UpdateDataActivity.this)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setLabel("Please wait")
                            .setCancellable(false)
                            .setAnimationSpeed(2)
                            .setDimAmount(0.5f)
                            .show();

                    name = edtname.getText().toString();
                    email = edtemail.getText().toString();
                    mobile = edtmobile.getText().toString();


                    firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).update(
                            "name", name, "number", mobile
                    ).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toasty.success(UpdateDataActivity.this, "Profile Updated Successfully! Please Login again.", 2000).show();
                            session.createLoginSession(name, email, mobile);
                            session.logoutUser();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.success(UpdateDataActivity.this, "Profile Updated Successfully", 2000).show();
                        }
                    });

                } else {

                    Toasty.warning(UpdateDataActivity.this,"Incorrect Details Entered",Toast.LENGTH_LONG).show();

                }
            }
        });
    }
    public void viewCart(View view) {
        //startActivity(new Intent(UpdateData.this,Cart.class));
        //finish();
    }

    public void viewProfile(View view) {
        startActivity(new Intent(UpdateDataActivity.this,ProfileActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private boolean validateNumber() {

        check = edtmobile.getText().toString();
        Log.e("inside number",check.length()+" ");
        if (check.length()>10) {
            return false;
        }else if(check.length()<10){
            return false;
        }
        return true;
    }

    private boolean validateEmail() {

        check = edtemail.getText().toString();

        if (check.length() < 4 || check.length() > 40) {
            return false;
        } else if (!check.matches("^[A-za-z0-9.@]+")) {
            return false;
        } else if (!check.contains("@") || !check.contains(".")) {
            return false;
        }

        return true;
    }

    private boolean validateName() {

        check = edtname.getText().toString();

        return !(check.length() < 4 || check.length() > 20);

    }

    //TextWatcher for Name -----------------------------------------------------

    TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() < 4 || check.length() > 20) {
                edtname.setError("Name Must consist of 4 to 20 characters");
            }
        }

    };

    //TextWatcher for Email -----------------------------------------------------

    TextWatcher emailWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() < 4 || check.length() > 40) {
                edtemail.setError("Email Must consist of 4 to 20 characters");
            } else if (!check.matches("^[A-za-z0-9.@]+")) {
                edtemail.setError("Only . and @ characters allowed");
            } else if (!check.contains("@") || !check.contains(".")) {
                edtemail.setError("Enter Valid Email");
            }

        }

    };

    //TextWatcher for Mobile -----------------------------------------------------

    TextWatcher numberWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length()>10) {
                edtmobile.setError("Number cannot be grated than 10 digits");
            }else if(check.length()<10){
                edtmobile.setError("Number should be 10 digits");
            }
        }

    };

    private void initialize() {

        namebutton =findViewById(R.id.name_button);
        edtname =findViewById(R.id.name);
        edtemail =findViewById(R.id.email);
        edtmobile =findViewById(R.id.number);
        updateProfileBtn = findViewById(R.id.update);
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        edtname.addTextChangedListener(nameWatcher);
        edtemail.addTextChangedListener(emailWatcher);
        edtmobile.addTextChangedListener(numberWatcher);



    }




    private void getValues() {

        //create new session object by passing application context
        session = new UserSession(getApplicationContext());

        //validating session
        session.isLoggedIn();

        //get User details if logged in
        user = session.getUserDetails();

        name = user.get(UserSession.KEY_NAME);
        email = user.get(UserSession.KEY_EMAIL);
        mobile = user.get(UserSession.KEY_MOBiLE);

        //setting values
        edtemail.setText(email);
        edtmobile.setText(mobile);
        edtname.setText(name);
        namebutton.setText(name);

    }
}