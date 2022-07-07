package com.example.shopnotch.Activities.UserActivities.UserAccessActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopnotch.Utilities.UserUtils.Validator;

import com.example.shopnotch.Utilities.GeneralUtils.CheckInternetConnection;
import com.example.shopnotch.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kaopiz.kprogresshud.KProgressHUD;

import es.dmoral.toasty.Toasty;


public class RegisterActivity extends AppCompatActivity {

    private EditText edtname, edtemail, edtpass, edtcnfpass, edtmobile;
    private String check, name, email, password, mobile;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private final String TAG = this.getClass().getSimpleName();
    private String userId;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    private KProgressHUD progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        new CheckInternetConnection(this).checkConnection();
        Typeface typeface = ResourcesCompat.getFont(this, R.font.rooster);

        TextView appname = findViewById(R.id.appname);
        appname.setTypeface(typeface);

        FirebaseApp.initializeApp(RegisterActivity.this);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        edtname = findViewById(R.id.name);
        edtemail = findViewById(R.id.email);
        edtpass = findViewById(R.id.password);
        edtcnfpass = findViewById(R.id.confirmpassword);
        edtmobile = findViewById(R.id.number);

        edtname.addTextChangedListener(nameWatcher);
        edtemail.addTextChangedListener(emailWatcher);
        edtpass.addTextChangedListener(passWatcher);
        edtcnfpass.addTextChangedListener(cnfpassWatcher);
        edtmobile.addTextChangedListener(numberWatcher);


        Button register_button = findViewById(R.id.register);

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Validator.validateName(edtname) && Validator.validateEmail(edtemail) && Validator.validatePass(edtpass) && Validator.validateCnfPass(edtcnfpass, edtpass) && Validator.validateNumber(edtmobile)) {

                    name = edtname.getText().toString();
                    email = edtemail.getText().toString();
                    password = edtcnfpass.getText().toString();
                    mobile = edtmobile.getText().toString();

                    try {

                        progressDialog = KProgressHUD.create(RegisterActivity.this)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setLabel("Signing You Up!")
                                .setCancellable(false)
                                .setAnimationSpeed(2)
                                .setDimAmount(0.5f)
                                .show();

                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            user = mAuth.getCurrentUser();
                                            userId = mAuth.getCurrentUser().getUid();
                                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    if (task.isSuccessful()) {
                                                        Toasty.success(RegisterActivity.this, "We've sent a verification link to your email.", Toast.LENGTH_SHORT, true).show();
                                                    }
                                                }
                                            });


                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            finish();

                                        } else {
                                            progressDialog.dismiss();
                                            Toasty.error(RegisterActivity.this, "Failed to Register", Toast.LENGTH_LONG, true).show();
                                        }
                                    }
                                });

                    } catch (Exception e) {
                        Toasty.error(RegisterActivity.this, "Failed to Register", Toast.LENGTH_LONG, true).show();

                    }


                }
            }
        });

        //GOTO LOGIN BUTTON
        final TextView loginuser = findViewById(R.id.login_now);
        loginuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        //GOTO FORGOT PASSWORD BUTTON
        final TextView forgotpass = findViewById(R.id.forgot_pass);
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, ForgotPasswordActivity.class));
                finish();
            }
        });


    }

    private boolean validateNumber() {

        check = edtmobile.getText().toString();
        Log.e("inside number", check.length() + " ");
        if (check.length() > 10) {
            return false;
        } else if (check.length() < 10) {
            return false;
        }
        return true;
    }

    private boolean validateCnfPass() {

        check = edtcnfpass.getText().toString();

        return check.equals(edtpass.getText().toString());
    }

    private boolean validatePass() {


        check = edtpass.getText().toString();

        if (check.length() < 4 || check.length() > 20) {
            return false;
        } else if (!check.matches("^[A-za-z0-9@]+")) {
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

    TextWatcher passWatcher = new TextWatcher() {
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
                edtpass.setError("Password Must consist of 4 to 20 characters");
            } else if (!check.matches("^[A-za-z0-9@]+")) {
                edtemail.setError("Only @ special character allowed");
            }
        }

    };

    TextWatcher cnfpassWatcher = new TextWatcher() {
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

            if (!check.equals(edtpass.getText().toString())) {
                edtcnfpass.setError("Both the passwords do not match");
            }
        }

    };

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

            if (check.length() > 10) {
                edtmobile.setError("Number cannot be grated than 10 digits");
            } else if (check.length() < 10) {
                edtmobile.setError("Number should be 10 digits");
            }
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        new CheckInternetConnection(this).checkConnection();
    }

}
