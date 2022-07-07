package com.example.shopnotch.Activities.ClothingProductsActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.util.Log;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopnotch.Activities.MainActivity;
import com.example.shopnotch.Activities.UserActivities.ProfileActivity;
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
import java.util.UUID;



import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class IndividualProductActivity extends AppCompatActivity {

    @BindView(R.id.productimage1)
    ImageView productimage1;
    @BindView(R.id.productimage2)
    ImageView productimage2;
    @BindView(R.id.productname)
    TextView productname;
    @BindView(R.id.productprice)
    TextView productprice;
    @BindView(R.id.add_to_cart)
    TextView addToCart;
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
        new CheckInternetConnection(this).checkConnection();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initialize();
    }

    private void initialize() {

        firestore = FirebaseFirestore.getInstance();
        model = (GenericProductModel) getIntent().getSerializableExtra("product");

        productprice.setText("$ " + Float.toString(model.getCardprice()));
        productname.setText(model.getCardname());
        quantityProductPage.setText("1");
        Picasso.get().load(model.getCardimage().toString()).into(productimage1);
        productimage2.setVisibility(View.GONE);
        session = new UserSession(getApplicationContext());

        session.isLoggedIn();
        usermobile = session.getUserDetails().get(UserSession.KEY_MOBiLE);
        useremail = session.getUserDetails().get(UserSession.KEY_EMAIL);
        username = session.getUserDetails().get(UserSession.KEY_NAME);

    };


    public void decrementProductQuantity(View view) {
        if (quantity > 1) {
            quantity--;
            quantityProductPage.setText(String.valueOf(quantity));
        }
    }

    public void incrementProductQuantity(View view) {
        quantity++;
        quantityProductPage.setText(String.valueOf(quantity));
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
                .document(String.valueOf(model.getCardname())).set(getProductObject()).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public void showPic2(View view){
        productimage1.setVisibility(View.GONE);
        productimage2.setVisibility(View.VISIBLE);

        Picasso.get().load(model.getCardimage2().toString()).into(productimage2);

    }
    public void showPic1(View view){
        productimage2.setVisibility(View.GONE);
        productimage1.setVisibility(View.VISIBLE);
        Picasso.get().load(model.getCardimage().toString()).into(productimage1);

    }

    public void addToCart(View view) {
        addToCartProcess(true);
    }

    public void viewProfile(View view) {
        startActivity(new Intent(IndividualProductActivity.this, ProfileActivity.class));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        new CheckInternetConnection(this).checkConnection();
    }
}

