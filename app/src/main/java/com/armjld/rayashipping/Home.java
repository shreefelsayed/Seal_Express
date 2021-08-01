package com.armjld.rayashipping;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.armjld.rayashipping.Captin.capPickUp;
import com.armjld.rayashipping.Captin.capReady;
import com.armjld.rayashipping.Login.LoginManager;
import com.armjld.rayashipping.Login.StartUp;
import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.Models.UserInFormation;
import com.armjld.rayashipping.Models.notiData;
import com.armjld.rayashipping.Notifications.NotificationFragment;
import com.armjld.rayashipping.Settings.SettingFragment;
import com.armjld.rayashipping.SuperCaptins.MyCaptins;
import com.armjld.rayashipping.SuperVisor.AllOrders;
import com.armjld.rayashipping.SuperVisor.SuperAvillable;
import com.armjld.rayashipping.SuperVisor.SuperRecived;
import com.armjld.rayashipping.Models.UserData;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

import timber.log.Timber;

public class Home extends AppCompatActivity {

    // ---------- Super Visor Data ----------- \\
    public static ArrayList<UserData> mCaptins = new ArrayList<>(); // My Captins Data

    // ---------- Common Data ----------- \\
    public static ArrayList<notiData> notiList = new ArrayList<>(); // Notifications Data

    // ---- Tracking
    boolean doubleBackToExitPressedOnce = false;


    public static int notiCount = 0;
    public static String whichFrag = "Home";
    public static BottomNavigationView bottomNavigationView;
    public static BadgeDrawable badge;

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("Home");
        if (fragment != null && fragment.isVisible()) {
            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                System.exit(0);
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "اضغط مرة اخري للخروج من التطبيق", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        } else {
            whichFrag = "Home";
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new AllOrders(), whichFrag).addToBackStack("Home").commit();
            bottomNavigationView.setSelectedItemId(R.id.home);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_home);

        if (UserInFormation.getUser().getId() == null) {
            finish();
            startActivity(new Intent(this, StartUp.class));
            return;
        }

        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem item = menu.findItem(R.id.noti);
        badge = bottomNavigationView.getOrCreateBadge(item.getItemId());


        if (notiCount == 0) {
            bottomNavigationView.removeBadge(R.id.noti);
        } else {
            badge.setNumber(notiCount);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, whichFrag(), whichFrag).addToBackStack("Home").commit();

        if (UserInFormation.getUser().getAccountType().equals("Delivery Worker")) {
            bottomNavigationView.getMenu().removeItem(R.id.profile);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!LoginManager.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = item -> {
        Fragment fragment = null;
        String fragTag = "";
        switch (item.getItemId()) {
            case R.id.home: {
                fragment = new AllOrders();
                fragTag = "Home";
                break;
            }

            case R.id.settings: {
                fragment = new SettingFragment();
                fragTag = "Settings";
                break;
            }

            case R.id.profile: {
                fragment = new MyCaptins();
                fragTag = "Profile";
                break;
            }

            case R.id.noti: {
                fragment = new NotificationFragment();
                fragTag = "Noti";
                break;
            }
        }
        assert fragment != null;
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, fragTag).addToBackStack("Home").commit();
        return true;
    };


    public static void getSuperVisorOrders() {
        UserInFormation.getUser().getListOrder().clear();
        getOrdersByLatest();
        getRayaDelv();
    }

    // -- Get Pickups
    public static void getOrdersByLatest() {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef("Raya");

        mDatabase.orderByChild("statue").equalTo("placed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // --------- Get Avillable Orders Data ------
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.getChildrenCount() < 5) return;
                        Order orderData = ds.getValue(Order.class);
                        assert orderData != null;

                        UserInFormation.getUser().getListOrder().add(orderData);
                    }
                }

                // -------- Add data to Fragment
                SuperAvillable.getOrders();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void getCaptins() {
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").orderByChild("mySuper").equalTo(UserInFormation.getUser().getSupervisor_code()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Home.mCaptins.clear();
                Home.mCaptins.trimToSize();
                if (snapshot.exists()) {
                    // ------ Get my Captins Data
                    for (DataSnapshot captin : snapshot.getChildren()) {
                        UserData user = captin.getValue(UserData.class);
                        assert user != null;
                        Home.mCaptins.add(user); // Add the Captin data
                    }
                }

                MyCaptins.getCaptins();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // ---------- Get To Deliver Raya Orders --------- \\
    public static void getRayaDelv() {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef("Raya");

        mDatabase.orderByChild("dSupervisor").equalTo(UserInFormation.getUser().getSupervisor_code()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot allOrders) {
                if (allOrders.exists()) {
                    for (DataSnapshot notDelv : allOrders.getChildren()) {
                        Order orderData = notDelv.getValue(Order.class);
                        assert orderData != null;
                        if (orderData.getStatue().equals("supD") || orderData.getStatue().equals("supDenied")) {
                            UserInFormation.getUser().getListOrder().add(orderData);
                        }
                    }
                }
                //set in Fragment -------
                SuperRecived.getOrders();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static void getNoti() {
        if (badge == null || bottomNavigationView == null) {
            return;
        }

        FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests").child(UserInFormation.getUser().getId()).limitToLast(30).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notiList.clear();
                notiList.trimToSize();
                notiCount = 0;
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.getChildrenCount() < 5) return;
                        String notiID = ds.getKey();
                        assert notiID != null;
                        notiData notiDB = ds.getValue(notiData.class);
                        assert notiDB != null;

                        notiList.add(notiDB);
                        notiDB.setNotiID(notiID);
                        if (notiDB.getIsRead().equals("false")) {
                            notiCount++;
                        }
                        Timber.i("Added This : %s", notiID);

                    }
                    if (notiCount > 0) {
                        badge.setNumber(notiCount);
                    } else {
                        bottomNavigationView.removeBadge(R.id.noti);
                    }
                    NotificationFragment.setNoti();

                } else {
                    bottomNavigationView.removeBadge(R.id.noti);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void getCaptinOrders() {
        UserInFormation.getUser().getListOrder().clear();
        getDeliveryOrders();
    }

    public static void getDeliveryOrders() {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef("Raya");

        mDatabase.orderByChild("uAccepted").equalTo(UserInFormation.getUser().getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Order orderData = ds.getValue(Order.class);
                        assert orderData != null;
                        if(orderData.getStatue().equals("deleted")) continue;
                        UserInFormation.getUser().getListOrder().add(orderData);
                    }
                }
                // -------- Set orders in Fragment
                capPickUp.getOrders();
                capReady.getOrders();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private Fragment whichFrag() {
        Fragment frag = null;
        switch (whichFrag) {
            case "Home": {
                frag = new AllOrders();
                bottomNavigationView.setSelectedItemId(R.id.home);
                break;
            }
            case "Profile": {
                frag = new MyCaptins();
                bottomNavigationView.setSelectedItemId(R.id.profile);
                break;
            }

            case "Noti": {
                frag = new NotificationFragment();
                bottomNavigationView.setSelectedItemId(R.id.noti);
                break;
            }

            case "Settings": {
                frag = new SettingFragment();
                bottomNavigationView.setSelectedItemId(R.id.settings);
                break;
            }

        }
        return frag;
    }
}