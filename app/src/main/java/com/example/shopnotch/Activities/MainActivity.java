package com.example.shopnotch.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.shopnotch.Activities.ShoppingActivities.CartActivity;
import com.example.shopnotch.Activities.ShoppingActivities.OrderHistoryActivity;
import com.example.shopnotch.Activities.UserActivities.ProfileActivity;
import com.example.shopnotch.Utilities.GeneralUtils.CheckInternetConnection;
import com.example.shopnotch.Activities.ClothingProductsActivities.ShoesActivity;
import com.example.shopnotch.R;
import com.example.shopnotch.UserSession.UserSession;
import com.google.firebase.FirebaseApp;
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.interfaces.ICrossfader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.mikepenz.materialize.util.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private UserSession session;
    private HashMap<String, String> user;
    private String name, email, mobile;
    private ImageSlider imageSlider;
    private Drawer result;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(MainActivity.this);
        new CheckInternetConnection(this).checkConnection();

        session = new UserSession(getApplicationContext());

        Typeface typeface = ResourcesCompat.getFont(this, R.font.blacklist);
        TextView appname = findViewById(R.id.appname);
        appname.setTypeface(typeface);

        getValues();
        inflateNavDrawer();

        imageSlider = findViewById(R.id.slider);
        inflateImageSlider();
    }

    private void inflateNavDrawer() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        IProfile profile = new ProfileDrawerItem()
                .withName(name)
                .withEmail(email);

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.gradient_background)
                .addProfiles(profile)
                .withCompactStyle(true)
                .build();

        //Adding nav drawer items ------------------------------------------------------------------
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.home).withIcon(R.drawable.home);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.cart).withIcon(R.drawable.cart);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.order_history).withIcon(R.drawable.order_hist_icon);

        SecondaryDrawerItem item4 = new SecondaryDrawerItem().withIdentifier(4).withName(R.string.myprofile).withIcon(R.drawable.profile);
        SecondaryDrawerItem item5 = new SecondaryDrawerItem().withIdentifier(5).withName(R.string.logout).withIcon(R.drawable.logout);

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withDrawerLayout(R.layout.crossfade_drawer)
                .withAccountHeader(headerResult)
                .withDrawerWidthDp(72)
                .withGenerateMiniDrawer(true)
                .withTranslucentStatusBar(true)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(item1,item2,item3, new DividerDrawerItem(), item4, item5,new DividerDrawerItem()).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position){
                            case 0:
                                if (result != null && result.isDrawerOpen()) {
                                    result.closeDrawer();
                                }
                                break;

                            case 1:
                                startActivity(new Intent(MainActivity.this, CartActivity.class));
                                result.closeDrawer();
                                break;


                            case 2:
                                startActivity(new Intent(MainActivity.this, OrderHistoryActivity.class));
                                result.closeDrawer();
                                break;

                            case 3:
                                result.closeDrawer();
                                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                                break;

                            case 4:
                            session.logoutUser();
                            finish();
                            break;

                            default:
                                Toast.makeText(MainActivity.this, "Default", Toast.LENGTH_LONG).show();
                        }

                        return true;
                    }
                }).build();


        crossfadeDrawerLayout = (CrossfadeDrawerLayout) result.getDrawerLayout();
        crossfadeDrawerLayout.setMaxWidthPx(DrawerUIUtils.getOptimalDrawerWidth(this));

        final MiniDrawer miniResult = result.getMiniDrawer();

        View view = miniResult.build(this);
        view.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(this, com.mikepenz.materialdrawer.R.attr.material_drawer_background, com.mikepenz.materialdrawer.R.color.material_drawer_background));
        crossfadeDrawerLayout.getSmallView().addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        miniResult.withCrossFader(new ICrossfader() {
            @Override
            public void crossfade() {
                boolean isFaded = isCrossfaded();
                crossfadeDrawerLayout.crossfade(400);

                if (isFaded) {
                    result.getDrawerLayout().closeDrawer(GravityCompat.START);
                }
            }

            @Override
            public boolean isCrossfaded() {
                return crossfadeDrawerLayout.isCrossfaded();
            }
        });
    }


    private void inflateImageSlider() {

        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel("https://m.media-amazon.com/images/G/31/img19/Wireless/Apple/iPhone11/RiverImages/11Pro/IN_iPhone11Pro_DESKTOP_01._CB437064827_.jpg"));
        slideModels.add(new SlideModel("https://piunikaweb.com/wp-content/uploads/2019/08/oneplus_7_pro_5g_experience_the_power_of_5g_banner-750x354.jpg"));
        slideModels.add(new SlideModel("https://lh3.googleusercontent.com/RSyeouwiFX4XVq6iw3H94al0VcXD693tBy2MxhBKCxAHCIfIpdt7wDV47_j2HanPSnTli7JgZ0fYHxESjz0uvVgeCBT3=w1000"));
        slideModels.add(new SlideModel("https://cdn.metrobrands.com/mochi/media/images/content/Homepage/HOTTMARZZ-BANNER-MOCHI.webp"));
        slideModels.add(new SlideModel("https://i.pinimg.com/originals/b2/78/7c/b2787cea792bff7d2c33e26ada6436bb.jpg"));
        slideModels.add(new SlideModel("https://cdnb.artstation.com/p/assets/images/images/016/802/459/large/shuja-shuaib-banner.jpg?1553535424"));

        imageSlider.setImageList(slideModels,true);

    }

    private void getValues() {
        session.isLoggedIn();
        user = session.getUserDetails();

        name = user.get(UserSession.KEY_NAME);
        email = user.get(UserSession.KEY_EMAIL);
        mobile = user.get(UserSession.KEY_MOBiLE);
    }

    public void viewProfile(View view) {
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
    }


    public void viewCart(View view) {
        startActivity(new Intent(MainActivity.this, CartActivity.class));
    }

    public void shoesActivity(View view) {
        startActivity(new Intent(MainActivity.this, ShoesActivity.class));
    }

    public void tshirtActivity(View view) {
        //startActivity(new Intent(MainActivity.this, TshirtActivity.class));
    }

    public void pantsActivity(View view) {

        //startActivity(new Intent(MainActivity.this, PantsActivity.class));
    }

    public void hoodiesActivity(View view) {

        //startActivity(new Intent(MainActivity.this, HoodiesActivity.class));
    }

    public void sportAttireActivity(View view) {

        //startActivity(new Intent(MainActivity.this, SportAttireActivity.class));
    }

    public void shirtsActivity(View view) {

        //startActivity(new Intent(MainActivity.this, ShirtsActivity.class));
    }
}
