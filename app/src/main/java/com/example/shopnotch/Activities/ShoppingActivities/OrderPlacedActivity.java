package com.example.shopnotch.Activities.ShoppingActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.shopnotch.Activities.MainActivity;
import com.example.shopnotch.Utilities.GeneralUtils.CheckInternetConnection;
import com.example.shopnotch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderPlacedActivity extends AppCompatActivity {

    @BindView(R.id.orderid)
    TextView orderidview;
    private String orderid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);

        ButterKnife.bind(this);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        initialize();
    }

    private void initialize() {
        orderid = getIntent().getStringExtra("orderid");
        orderidview.setText(orderid);
    }

    public void finishActivity(View view) {
        Intent i = new Intent(OrderPlacedActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}