package com.example.shopnotch.Activities.ShoppingActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.shopnotch.Activities.UserActivities.ProfileActivity;
import com.example.shopnotch.ObjectBlueprints.SingleProductModel;
import com.example.shopnotch.Utilities.GeneralUtils.CheckInternetConnection;
import com.example.shopnotch.R;
import com.example.shopnotch.UserSession.UserSession;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class CartActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName()+"_xlr8";

    private UserSession session;
    private HashMap<String,String> user;
    private String name,email,photo,mobile;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;

    private LottieAnimationView tv_no_item;
    private LinearLayout activitycartlist;
    private LottieAnimationView emptycart;

    private ArrayList<SingleProductModel> cartcollect;
    private float totalcost=0;
    private int totalproducts=0;

    //Getting reference to Firebase Database
    private FirebaseFirestore firebaseFirestore;
    private TextView no_of_items_tv;
    private TextView total_amount_tv;
    private FirestoreRecyclerAdapter adapter;
    private TextView checkout;
    private LinearLayout details_layout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Cart");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FirebaseApp.initializeApp(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        no_of_items_tv = findViewById(R.id.total_items_tv);
        total_amount_tv = findViewById(R.id.total_amount_tv);
        details_layout = findViewById(R.id.details_layout);
        checkout = findViewById(R.id.text_action_bottom2);
        new CheckInternetConnection(this).checkConnection();

        getUserValues();

        session = new UserSession(getApplicationContext());
        session.isLoggedIn();

        mRecyclerView = findViewById(R.id.recyclerview);
        tv_no_item = findViewById(R.id.tv_no_cards);
        activitycartlist = findViewById(R.id.activity_cart_list);
        emptycart = findViewById(R.id.empty_cart);
        cartcollect = new ArrayList<>();

        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true);
        }

        mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        populateRecyclerView();


    }

    private void populateRecyclerView() {


        Query query = firebaseFirestore.collection("Cart").document(user.get(UserSession.KEY_EMAIL))
                .collection(user.get(UserSession.KEY_NAME)+" Cart");

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    if(task.getResult().size()==0){

                        tv_no_item.setVisibility(View.GONE);
                        activitycartlist.setVisibility(View.GONE);
                        emptycart.setVisibility(View.VISIBLE);
                        return;
                    }

                    for (QueryDocumentSnapshot document : task.getResult()) {


                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                }

            }
        });

        final FirestoreRecyclerOptions<SingleProductModel> response = new FirestoreRecyclerOptions.Builder<SingleProductModel>()
                .setQuery(query, SingleProductModel.class)
                .build();
        adapter =new FirestoreRecyclerAdapter<SingleProductModel, CartActivity.ProductView>(response) {
            @Override
            protected void onBindViewHolder(@NonNull CartActivity.ProductView viewHolder, final int position, @NonNull SingleProductModel model) {
                if(tv_no_item.getVisibility()== View.VISIBLE){
                    tv_no_item.setVisibility(View.GONE);
                }
                viewHolder.cardname.setText(model.getPrname());
                viewHolder.cardprice.setText("$ "+model.getPrprice());
                viewHolder.cardcount.setText("Quantity : "+model.getNo_of_items());
                viewHolder.totalCardAmt.setText("$ "+model.getPrprice()+" x "+model.getNo_of_items()+" = $ "+(Float.valueOf(model.getPrprice())*model.getNo_of_items()));
                Picasso.get().load(model.getPrimage()).into(viewHolder.cardimage);

                totalcost += model.getNo_of_items()*Float.parseFloat(model.getPrprice());
                totalproducts += model.getNo_of_items();
                cartcollect.add(model);


                    checkout.setVisibility(View.VISIBLE);
                    details_layout.setVisibility(View.VISIBLE);


                viewHolder.carddelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSnapshots().getSnapshot(position).getReference().delete();
                        session.decreaseCartValue();
                        startActivity(new Intent(CartActivity.this,CartActivity.class));
                        finish();
                    }
                });


            }

            @NonNull
            @Override
            public CartActivity.ProductView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cart_item_layout, parent, false);

                return new ProductView(view);

            }


        };

        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();



    }

    private void getUserValues() {

        session = new UserSession(getApplicationContext());
        session.isLoggedIn();
        user = session.getUserDetails();

        name = user.get(UserSession.KEY_NAME);
        email = user.get(UserSession.KEY_EMAIL);
        mobile = user.get(UserSession.KEY_MOBiLE);
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


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public static class ProductView extends RecyclerView.ViewHolder{

        TextView cardname;
        ImageView cardimage;
        TextView cardprice;
        TextView cardcount;
        ImageView carddelete;
        TextView totalCardAmt;

        View mView;
        public ProductView(View v) {
            super(v);
            mView = v;
            cardname = v.findViewById(R.id.cart_prtitle);
            cardimage = v.findViewById(R.id.image_cartlist);
            cardprice = v.findViewById(R.id.cart_prprice);
            cardcount = v.findViewById(R.id.cart_prcount);
            carddelete = v.findViewById(R.id.deletecard);
            totalCardAmt = v.findViewById(R.id.total_card_amount);
        }
    }


    public void checkout(View view) {
        Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
        intent.putExtra("totalprice",Float.toString(totalcost));
        intent.putExtra("totalproducts",Integer.toString(totalproducts));
        intent.putExtra("cartproducts",cartcollect);
        startActivity(intent);
        finish();
    }

    public void viewProfile(View view) {
        startActivity(new Intent(CartActivity.this, ProfileActivity.class));
        finish();
    }

}