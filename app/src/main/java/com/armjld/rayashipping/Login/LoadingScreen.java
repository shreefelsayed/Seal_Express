package com.armjld.rayashipping.Login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.armjld.rayashipping.Captin.capPickUp;
import com.armjld.rayashipping.Captin.capReady;
import com.armjld.rayashipping.Home;
import com.armjld.rayashipping.Notifications.NotificationFragment;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.SuperCaptins.MyCaptins;
import com.armjld.rayashipping.SuperVisor.SuperAvillable;
import com.armjld.rayashipping.SuperVisor.SuperRecived;
import com.armjld.rayashipping.getRefrence;
import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.Models.UserInFormation;
import com.armjld.rayashipping.Models.notiData;
import com.armjld.rayashipping.Models.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoadingScreen extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private static TextView txtLoading;

    @Override
    public void onBackPressed() { }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        if (UserInFormation.getUser().getId() == null) {
            finish();
            startActivity(new Intent(this, StartUp.class));
            return;
        }

        txtLoading = findViewById(R.id.txtLoading);

        UserInFormation.getUser().getListOrder().clear();
        if (UserInFormation.getUser().getAccountType().equals("Supervisor")) {
            getSuperVisorOrders();
        } else if (UserInFormation.getUser().getAccountType().equals("Delivery Worker")) {
            getCaptinsOrders();
        }
    }

    // --- Captins
    private void getCaptinsOrders() {
        txtLoading.setText("جاري تحضير شحنات الشركه ..");

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
                getNoti();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // --------------- Get Captins -----
    private void getSuperVisorOrders() {
        txtLoading.setText("مراجعه المندوبين ..");

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

                // ------ Send data to captins fragment
                MyCaptins.getCaptins();

                // --- Get Orders
                getRayaOrders();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // -------------- Get Pick Ups ----
    private void getRayaOrders() {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef("Raya");

        txtLoading.setText("جاري تحضير الشحنات الشركه ..");

        mDatabase.orderByChild("statue").equalTo("placed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // --------- Get Avillable Orders Data ------
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Order orderData = ds.getValue(Order.class);
                        assert orderData != null;

                        UserInFormation.getUser().getListOrder().add(orderData);
                    }
                }

                // -------- Add data to Fragment
                SuperAvillable.getOrders();
                getRayaDelv();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // ---------- Get To Deliver Raya Orders --------- \\
    private void getRayaDelv() {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef("Raya");

        txtLoading.setText("جاري تحضير الشحنات الشركه ..");

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
                getNoti();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    // ---------- Get Notifications ------------- \\
    @SuppressLint("SetTextI18n")
    public void getNoti() {
        txtLoading.setText("جاري تجهيز الإشعارات ..");

        Home.notiList.clear();
        Home.notiList.trimToSize();

        FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests").child(UserInFormation.getUser().getId()).limitToLast(30).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int unRead = 0; // Unread Noti Count

                // -------- Check for Notifications
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.getChildrenCount() < 5) continue;
                        String notiID = ds.getKey();
                        assert notiID != null;
                        notiData notiDB = ds.getValue(notiData.class);
                        assert notiDB != null;

                        Home.notiList.add(notiDB);
                        notiDB.setNotiID(notiID);
                        if (notiDB.getIsRead().equals("false")) {
                            unRead++;
                        }
                    }
                }

                Home.notiCount = unRead; // Set unread Count

                // ------ Add to Fragment
                NotificationFragment.setNoti();

                whatToDo(); // Move to Next Activity
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void whatToDo() {
        Home.whichFrag = "Home";
        startActivity(new Intent(LoadingScreen.this, Home.class));
    }
}