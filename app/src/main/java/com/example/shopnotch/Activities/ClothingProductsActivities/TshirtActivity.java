package com.example.shopnotch.Activities.ClothingProductsActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
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

public class TshirtActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private LottieAnimationView no_products_icon;
    private UserSession session;

    private final String TAG = this.getClass().getSimpleName()+"_xlr8";

    //Getting reference to Firebase Database
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tshirts);

        FirebaseApp.initializeApp(this);
        session = new UserSession(getApplicationContext());

        firebaseFirestore = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        new CheckInternetConnection(this).checkConnection();

        mRecyclerView = findViewById(R.id.tshirts_recycler);
        no_products_icon = findViewById(R.id.no_products_icon);
        no_products_icon.setVisibility(View.GONE);

        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Query query = firebaseFirestore.collection("/Tshirts");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    if(task.getResult().size()==0){
                        no_products_icon.setVisibility(View.VISIBLE);
                    }
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                }

            }
        });

        final FirestoreRecyclerOptions<GenericProductModel> response = new FirestoreRecyclerOptions.Builder<GenericProductModel>()
                .setQuery(query, GenericProductModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<GenericProductModel, ProductView>(response) {
            @Override
            protected void onBindViewHolder(@NonNull ProductView viewHolder, final int position, @NonNull GenericProductModel model) {

                viewHolder.modelname.setText(model.getCardname());
                viewHolder.modelprice.setText("$ "+Float.toString(model.getCardprice()));
                Picasso.get().load(model.getCardimage()).into(viewHolder.modelimage1);
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TshirtActivity.this, IndividualProductActivity.class);
                        intent.putExtra("product",getItem(position));
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public ProductView onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.product_view, viewGroup, false);
                return new ProductView(view);
            }


        };

        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();



    }


    public void viewCart(View view) {
        startActivity(new Intent(TshirtActivity.this, CartActivity.class));
        finish();
    }

    public static class ProductView extends RecyclerView.ViewHolder{

        TextView modelname;
        ImageView modelimage1;
        TextView modelprice;
        View mView;
        public ProductView(View v) {
            super(v);
            mView =v;
            modelname = v.findViewById(R.id.modelname);
            modelimage1 = v.findViewById(R.id.modelimage1);
            modelprice = v.findViewById(R.id.modelprice);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"Adapter Listening");
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"Adapter Not  Listening");
        adapter.stopListening();
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
