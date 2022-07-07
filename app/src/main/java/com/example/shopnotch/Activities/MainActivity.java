package com.example.shopnotch.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.shopnotch.Activities.ShoppingActivities.CartActivity;
import com.example.shopnotch.Activities.UserActivities.ProfileActivity;
import com.example.shopnotch.Utilities.GeneralUtils.CheckInternetConnection;
import com.example.shopnotch.Activities.ClothingProductsActivities.ShoesActivity;
import com.example.shopnotch.Activities.ClothingProductsActivities.ShirtsActivity;
import com.example.shopnotch.Activities.ClothingProductsActivities.SweatersActivity;
import com.example.shopnotch.Activities.ClothingProductsActivities.TshirtActivity;
import com.example.shopnotch.Activities.ClothingProductsActivities.PantsActivity;
import com.example.shopnotch.Activities.ClothingProductsActivities.SportAttireActivity;
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
    private TextView scrollingSlogan;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(MainActivity.this);
        new CheckInternetConnection(this).checkConnection();
        session = new UserSession(getApplicationContext());

        Typeface typeface = ResourcesCompat.getFont(this, R.font.rooster);
        TextView appname = findViewById(R.id.appname);
        appname.setTypeface(typeface);

        getUserValues();
        generateNavDrawer();
        scrollingSlogan = (TextView) this.findViewById(R.id.slogans);
        scrollingSlogan.setSelected(true);

        imageSlider = findViewById(R.id.slider);
        generateImageSlider();
    }


    private void generateNavDrawer() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Adding nav drawer items ------------------------------------------------------------------
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.home).withIcon(R.drawable.home);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.cart).withIcon(R.drawable.cart);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.myprofile).withIcon(R.drawable.profile);
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.logout).withIcon(R.drawable.logout);

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withDrawerLayout(R.layout.crossfade_drawer)
                .withDrawerWidthDp(72)
                .withGenerateMiniDrawer(true)
                .withTranslucentStatusBar(true)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(item1,item2,item3,item4, new DividerDrawerItem()).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position){
                            case 0:
                                if (result != null && result.isDrawerOpen()) {
                                    result.closeDrawer();
                                }
                                break;

                            case 1:
                                result.closeDrawer();
                                startActivity(new Intent(MainActivity.this, CartActivity.class));
                                break;

                            case 2:
                                result.closeDrawer();
                                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                                break;

                            case 3:
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


    private void generateImageSlider() {

        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel("https://img.freepik.com/free-photo/female-legs-heel-sneaker-yellow-background_185193-83306.jpg?w=2000"));
        slideModels.add(new SlideModel("https://media.gq.com/photos/620ff1a2f59deb0e8e8345bd/master/pass/shirts.jpg"));
        slideModels.add(new SlideModel("https://media.istockphoto.com/photos/photo-of-nice-lady-hiding-eyes-with-hood-sending-air-kiss-wear-yellow-picture-id1166158026?k=20&m=1166158026&s=612x612&w=0&h=sRdjZ2pjryfdNE_QpELpXlaO5a9FbEW-Oc73SGzAFHc="));
        slideModels.add(new SlideModel("https://img.freepik.com/free-photo/magnificent-woman-long-bright-skirt-dancing-studio-carefree-inspired-female-model-posing-with-pleasure-yellow_197531-11084.jpg?w=2000"));
        slideModels.add(new SlideModel("https://img.freepik.com/free-photo/pretty-hispanic-woman-with-bronze-skin-waving-hands-while-sitting-longboard-inspired-latin-girl-sunglasses-posing-skateboard_197531-4153.jpg"));
        slideModels.add(new SlideModel("https://wi.wallpapertip.com/wsimgs/25-255529_spring-summer-2020-mens-sport-fashion-trends.png"));

        imageSlider.setImageList(slideModels,true);

    }

    private void getUserValues() {
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
        startActivity(new Intent(MainActivity.this, TshirtActivity.class));
    }

    public void pantsActivity(View view) {

        startActivity(new Intent(MainActivity.this, PantsActivity.class));
    }

    public void sweatersActivity(View view) {

        startActivity(new Intent(MainActivity.this, SweatersActivity.class));
    }

    public void sportAttireActivity(View view) {

        startActivity(new Intent(MainActivity.this, SportAttireActivity.class));
    }

    public void shirtsActivity(View view) {

        startActivity(new Intent(MainActivity.this, ShirtsActivity.class));
    }
}
