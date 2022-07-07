package com.example.shopnotch.Activities.ShoppingActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.shopnotch.ObjectBlueprints.PlacedOrderModel;
import com.example.shopnotch.ObjectBlueprints.SingleProductModel;
import com.example.shopnotch.Utilities.GeneralUtils.CheckInternetConnection;
import com.example.shopnotch.R;
import com.example.shopnotch.UserSession.UserSession;
import com.example.shopnotch.Utilities.ShoppingUtils.CheckoutUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class CheckoutActivity extends AppCompatActivity {

    @BindView(R.id.delivery_date)
    TextView deliveryDate;
    @BindView(R.id.no_of_items)
    TextView noOfItems;
    @BindView(R.id.total_amount)
    TextView totalAmount;
    MaterialEditText ordernumber;
    @BindView(R.id.orderaddress)
    MaterialEditText orderaddress;
    @BindView(R.id.orderpincode)
    MaterialEditText orderpincode;

    private ArrayList<SingleProductModel> cartcollect;
    private ArrayList<String> placed_order_images;
    private String orderDateTime;
    private UserSession session;
    private FirebaseFirestore firebaseFirestore;
    private String payment_mode="PAYPAL",order_reference_id;
    private HashMap<String,String> user;
    private String placed_user_name,getPlaced_user_email,getPlaced_user_mobile_no;
    private String currdatetime;
    private FirebaseAuth mAuth;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ButterKnife.bind(this);
        new CheckInternetConnection(this).checkConnection();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Checkout");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        session = new UserSession(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        placed_order_images = new ArrayList<>();

        session.isLoggedIn();
        FirebaseApp.initializeApp(this);
        firebaseFirestore = FirebaseFirestore.getInstance();

        generatepProductdetails();

    }

    private void generatepProductdetails() {

        bundle = getIntent().getExtras();
        totalAmount.setText(bundle.get("totalprice").toString());
        noOfItems.setText(bundle.get("totalproducts").toString());
        cartcollect = (ArrayList<SingleProductModel>) bundle.get("cartproducts");
        deliveryDate.setText(CheckoutUtils.getOrderArrivalDate());

        user = session.getUserDetails();
        placed_user_name=user.get(UserSession.KEY_NAME);
        getPlaced_user_email=user.get(UserSession.KEY_EMAIL);
        getPlaced_user_mobile_no=user.get(UserSession.KEY_MOBiLE);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH-mm");
        currdatetime = sdf.format(new Date());
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public void PlaceOrder(View view) {

        order_reference_id = "ORDER-" + CheckoutUtils.getRandomString();
        orderDateTime = new SimpleDateFormat("dd.MMM.yyyy-HH.mm.ss").format(new Date());

        for (SingleProductModel singleProductModel : cartcollect) {
            placed_order_images.add(singleProductModel.getPrimage());
        }

        final KProgressHUD progressDialog = KProgressHUD.create(CheckoutActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Placing Order. Please Wait...")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        for (final SingleProductModel model : cartcollect) {
            firebaseFirestore.collection("Cart").document(getPlaced_user_email)
                    .collection(user.get(UserSession.KEY_NAME) + " Cart").document(String.valueOf(model.getPrid()))
                    .delete(); }
        session.setCartValue(0);
        progressDialog.dismiss();

        Toasty.success(CheckoutActivity.this, "Order Placed Successfully", 2000).show();
        Intent intent = new Intent(CheckoutActivity.this, OrderPlacedActivity.class);
        intent.putExtra("orderid", order_reference_id);
        intent.putExtra("custid", mAuth.getCurrentUser().getUid());
        intent.putExtra("amount_to_pay", bundle.get("totalprice").toString());
        startActivity(intent);
        finish();


    }


    public PlacedOrderModel getProductObject() {
        return new PlacedOrderModel(placed_order_images,"ORDER_PLACED",order_reference_id,orderDateTime,noOfItems.getText().toString(),totalAmount.getText().toString(),deliveryDate.getText().toString(),payment_mode,orderaddress.getText().toString(),orderpincode.getText().toString(),placed_user_name,getPlaced_user_email,getPlaced_user_mobile_no);
    }
    public void connectToPaypal(View v){
        Toasty.success(CheckoutActivity.this, "Connected To Your Paypal! - " + getPlaced_user_email, 2000).show();

    }



}