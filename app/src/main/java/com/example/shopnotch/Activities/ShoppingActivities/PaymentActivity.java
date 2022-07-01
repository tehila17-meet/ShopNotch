package com.example.shopnotch.Activities.ShoppingActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.shopnotch.Utilities.GeneralUtils.JSONParser;
import com.example.shopnotch.ObjectBlueprints.PlacedOrderModel;
import com.example.shopnotch.ObjectBlueprints.SingleProductModel;
import com.example.shopnotch.R;
import com.example.shopnotch.UserSession.UserSession;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class PaymentActivity extends AppCompatActivity {

    private String custId = "", orderId = "", merchantId = "", amountToPay = "";

    private static String TAG = "PaymentActivity";

    private FirebaseFirestore firebaseFirestore;
    private PlacedOrderModel placedOrderModel;
    private String currdatetime;
    private ArrayList<SingleProductModel> cartcollect;
    private UserSession session;
    private HashMap<String, String> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        session = new UserSession(PaymentActivity.this);
        user = session.getUserDetails();

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        cartcollect = (ArrayList<SingleProductModel>) args.getSerializable("cartcollect");
        Log.d("xlr8_PYTM_cc", String.valueOf(cartcollect));

        orderId = intent.getExtras().getString("orderid");
        Log.d("xlr8_PYTM_oid", String.valueOf(orderId));
        custId = intent.getExtras().getString("custid");
        Log.d("xlr8_PYTM_cid", String.valueOf(custId));
        amountToPay = intent.getExtras().getString("amount_to_pay");
        Log.d("xlr8_PYTM_atp", String.valueOf(amountToPay));
        placedOrderModel = (PlacedOrderModel) intent.getSerializableExtra("PlacedOrderModel");
        Log.d("xlr8_PYTM_pom", String.valueOf(placedOrderModel));
        currdatetime = intent.getExtras().getString("currdatetime");
        Log.d("xlr8_PYTM_cdt", String.valueOf(currdatetime));


        merchantId = "PUT YOUR ID";
        FirebaseApp.initializeApp(this);
        firebaseFirestore = FirebaseFirestore.getInstance();

    }


    private void saveDataToFirestoreServer() {

        final KProgressHUD progressDialog = KProgressHUD.create(PaymentActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Placing Order. Please Wait...")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();


        //adding user details to the database under orders table
        firebaseFirestore.collection("Orders").document(placedOrderModel.getPlaced_user_email())
                .collection(user.get(UserSession.KEY_NAME)+" Orders")
                .document(currdatetime)
                .set(placedOrderModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                for (final SingleProductModel model : cartcollect) {

                    firebaseFirestore.collection("Orders").document(placedOrderModel.getPlaced_user_email())
                            .collection(user.get(UserSession.KEY_NAME)+" Orders")
                            .document(currdatetime).collection("Items").add(model).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "Model Added: " + model.getPrname());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toasty.warning(PaymentActivity.this, "Server down! Please contact Administrator.", 2000).show();
                            Log.d(TAG, "Error: " + e.getMessage());
                        }
                    });
                }

                for (final SingleProductModel model : cartcollect) {
                    firebaseFirestore.collection("Cart").document(placedOrderModel.getPlaced_user_email())
                            .collection(user.get(UserSession.KEY_NAME) + " Cart").document(String.valueOf(model.getPrid()))
                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Model: " + model.getPrname() + " Deleted");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Deleting Error: " + e.getMessage());
                        }
                    });

                }
                session.setCartValue(0);
                progressDialog.dismiss();


                Toasty.success(PaymentActivity.this, "Order Placed Successfully", 2000).show();
                Intent intent = new Intent(PaymentActivity.this, OrderPlacedActivity.class);
                intent.putExtra("orderid", orderId);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toasty.warning(PaymentActivity.this, "Server down! Please contact Administrator.", 2000).show();
                Log.d(TAG, "Placed Order Model Error: " + e.getMessage());
            }
        });




    }





}
