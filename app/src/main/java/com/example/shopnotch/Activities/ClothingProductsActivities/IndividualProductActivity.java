package com.example.shopnotch.Activities.ClothingProductsActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopnotch.ObjectBlueprints.GenericProductModel;
import com.example.shopnotch.ObjectBlueprints.SingleProductModel;
import com.example.shopnotch.Utilities.GeneralUtils.CheckInternetConnection;
import com.example.shopnotch.R;
import com.example.shopnotch.UserSession.UserSession;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class IndividualProductActivity extends AppCompatActivity {

    @BindView(R.id.productimage)
    ImageView productimage;
    @BindView(R.id.productname)
    TextView productname;
    @BindView(R.id.productprice)
    TextView productprice;
    @BindView(R.id.add_to_cart)
    TextView addToCart;
    @BindView(R.id.productdesc)
    TextView productdesc;
    @BindView(R.id.quantityProductPage)
    EditText quantityProductPage;




    private GenericProductModel model;
    private UserSession session;

    private String usermobile, useremail;
    private int quantity = 1;
    private String username;

    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_product);
        ButterKnife.bind(this);



        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        initialize();


    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }

    private void initialize() {

        firestore = FirebaseFirestore.getInstance();
        model = (GenericProductModel) getIntent().getSerializableExtra("product");

        productprice.setText("$ " + Float.toString(model.getCardprice()));
        productname.setText(model.getCardname());
        quantityProductPage.setText("1");
        Picasso.get().load(model.getCardimage()).into(productimage);

        //SharedPreference for Cart Value
        session = new UserSession(getApplicationContext());

        //validating session
        session.isLoggedIn();
        usermobile = session.getUserDetails().get(UserSession.KEY_MOBiLE);
        useremail = session.getUserDetails().get(UserSession.KEY_EMAIL);
        username = session.getUserDetails().get(UserSession.KEY_NAME);

        //setting textwatcher for no of items field
        quantityProductPage.addTextChangedListener(productcount);




    }

    //check that product count must not exceed 500
    TextWatcher productcount = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (quantityProductPage.getText().toString().equals("")) {
                quantityProductPage.setText("0");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            //none
            if (Integer.parseInt(quantityProductPage.getText().toString()) >= 500) {
                Toasty.error(IndividualProductActivity.this, "Product Count Must be less than 500", Toast.LENGTH_LONG).show();
            }
        }

    };

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public void decrement(View view) {
        if (quantity > 1) {
            quantity--;
            quantityProductPage.setText(String.valueOf(quantity));
        }
    }

    public void increment(View view) {
        if (quantity < 500) {
            quantity++;
            quantityProductPage.setText(String.valueOf(quantity));
        } else {
            Toasty.error(IndividualProductActivity.this, "Product Count Must be less than 500", Toast.LENGTH_LONG).show();
        }
    }


    private SingleProductModel getProductObject() {

        return new SingleProductModel(model.getCardid(), Integer.parseInt(quantityProductPage.getText().toString()), useremail, usermobile, model.getCardname(), Float.toString(model.getCardprice()), model.getCardimage());

    }


    private void addToCartProcess(final boolean addToCart){
        final KProgressHUD progressDialog=  KProgressHUD.create(IndividualProductActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        firestore.collection("Cart").document(useremail).collection(username+" Cart")
                .document(String.valueOf(model.getCardid())).set(getProductObject()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                session.increaseCartValue();
                if (addToCart==true) {
                    Toasty.success(IndividualProductActivity.this, "Added to Cart", 2000).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toasty.error(IndividualProductActivity.this,"Failed to add.",2000).show();
            }
        });

    }
    public void connectToPayPal(){
        Toasty.success(IndividualProductActivity.this, "Connected To Paypal: " + useremail, 3000).show();

    }
    public void addToCart(View view) {
        addToCartProcess(true);
    }
}