package com.example.shopnotch.Activities.ClothingProductsActivities;

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
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.shopnotch.Activities.ShoppingActivities.CartActivity;
import com.example.shopnotch.ObjectBlueprints.GenericProductModel;
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

public class ShoesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private LottieAnimationView tv_no_item;
    private String usermobile;
    private UserSession session;

    private final String TAG = this.getClass().getSimpleName()+"_xlr8";

    //Getting reference to Firebase Database
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoes);

        FirebaseApp.initializeApp(this);
        session = new UserSession(getApplicationContext());

        firebaseFirestore = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        //Initializing our Recyclerview
        mRecyclerView = findViewById(R.id.my_recycler_view);
        tv_no_item = findViewById(R.id.tv_no_cards);
        tv_no_item.setVisibility(View.VISIBLE);

      //  if (mRecyclerView != null) {
            //to enable optimization of recyclerview
        //    mRecyclerView.setHasFixedSize(true);
       // }

        //using staggered grid pattern in recyclerview
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Query query = firebaseFirestore.collection("/Shoes");

//        Query query = firebaseFirestore.collection("/Shoes").document("101").collection("Shoes");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                }

            }
        });


        Log.d(TAG,"Creating Response");

        final FirestoreRecyclerOptions<GenericProductModel> response = new FirestoreRecyclerOptions.Builder<GenericProductModel>()
                .setQuery(query, GenericProductModel.class)
                .build();

        Log.d(TAG,"Response Created");
        Log.d(TAG,"Getting Data");

         adapter = new FirestoreRecyclerAdapter<GenericProductModel, ProductView>(response) {
            @Override
            protected void onBindViewHolder(@NonNull ProductView viewHolder, final int position, @NonNull GenericProductModel model) {
                Log.d(TAG,"Populating Data");

                viewHolder.modelname.setText(model.getCardname());
                viewHolder.modelprice.setText("$ "+Float.toString(model.getCardprice()));
                Picasso.get().load(model.getCardimage()).into(viewHolder.modelimage1);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ShoesActivity.this, IndividualProductActivity.class);
                        intent.putExtra("product",getItem(position));
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public ProductView onCreateViewHolder(@NonNull ViewGroup group, int viewType) {
                Log.d(TAG,"Inflating Layout");
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.cards_cardview_layout, group, false);

                return new ProductView(view);
            }


        };

        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, String.valueOf(response.getSnapshots()));

            }
        },10000);


    }



    public void viewCart(View view) {
        startActivity(new Intent(ShoesActivity.this, CartActivity.class));
        finish();
    }

    //viewHolder for our Firebase UI
    public static class ProductView extends RecyclerView.ViewHolder{

        TextView modelname;
        ImageView modelimage1;
        TextView modelprice;
        View mView;
        public ProductView(View v) {
            super(v);
            mView =v;
            modelname = v.findViewById(R.id.cardcategory);
            modelimage1 = v.findViewById(R.id.cardimage);
            modelprice = v.findViewById(R.id.cardprice);
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.d(TAG,"Adapter Listening");
//        adapter.startListening();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.d(TAG,"Adapter Not  Listening");
//        adapter.stopListening();
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//        return true;
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        //check Internet Connection
//        new CheckInternetConnection(this).checkConnection();
//    }

}
