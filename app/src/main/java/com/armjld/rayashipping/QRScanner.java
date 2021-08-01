package com.armjld.rayashipping;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.Models.UserInFormation;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import timber.log.Timber;


public class QRScanner extends BaseScannerActivity implements ZXingScannerView.ResultHandler {
    TextView txtCounter;
    CountDownTimer resend;
    ImageView btnBack;
    SpinKitView loading;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());
    private ZXingScannerView mScannerView;


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_q_r_scanner);
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        txtCounter = findViewById(R.id.txtCounter);
        btnBack = findViewById(R.id.btnBack);
        loading = findViewById(R.id.loading);

        mScannerView = new ZXingScannerView(this);
        mScannerView.setFormats(Collections.singletonList(BarcodeFormat.QR_CODE));
        mScannerView.setAutoFocus(true);
        mScannerView.setLaserEnabled(true);
        mScannerView.setBorderColor(Color.WHITE);
        mScannerView.stopCameraPreview();
        mScannerView.setIsBorderCornerRounded(true);
        mScannerView.setBorderCornerRadius(300);
        contentFrame.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        mScannerView.stopCameraPreview();
        if (UserInFormation.getUser().getAccountType().equals("Supervisor")) {
            checkForOrder(rawResult.getText());
        } else {
            checkForCaptin(rawResult.getText());
        }
    }


    // ---------- Scan For Captin ------------ \\
    private void checkForCaptin(String trackID) {
        loading.setVisibility(View.VISIBLE);
        DatabaseReference mDatabase;

        // ------------ Check for order Refrence
        getRefrence ref = new getRefrence();
        mDatabase = ref.getRef("Raya");

        mDatabase.orderByChild("trackid").equalTo(trackID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // ------------ Make Action on Order -----------
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Order orderData = ds.getValue(Order.class);
                        assert orderData != null;
                        OrdersClass ordersClass = new OrdersClass(QRScanner.this);

                        if (!orderData.getStatue().equals("deleted")) {
                            ordersClass.asignDelv(orderData, UserInFormation.getUser().getId());
                        } else {
                            Toast.makeText(QRScanner.this, "لا يمكنك استلام تلك الشحنه من المشرف", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                } else {
                    Toast.makeText(QRScanner.this, "رقم التتبع غير صحيح، حاول مره اخري ..", Toast.LENGTH_SHORT).show();
                }

                // -------------- Start Scanning Again in 5 Secs ----------
                loading.setVisibility(View.GONE);
                Handler handler = new Handler();
                int secDelay = 3000;
                handler.postDelayed(() -> mScannerView.resumeCameraPreview(QRScanner.this), secDelay);
                startCounter(secDelay);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // ---------- Scan For SuperVisor ------------ \\
    private void checkForOrder(String trackID) {
        loading.setVisibility(View.VISIBLE);
        DatabaseReference mDatabase;

        long tsLong = System.currentTimeMillis() / 1000;
        String logC = Long.toString(tsLong);

        // ------------ Check for order Refrence
        getRefrence ref = new getRefrence();
        mDatabase = ref.getRef("Raya");

        mDatabase.orderByChild("trackid").equalTo(trackID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // ------------ Make Action on Order -----------
                if (snapshot.exists()) {
                    Timber.i("Track Id Exists");
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Order orderData = ds.getValue(Order.class);
                        assert orderData != null;
                        if (orderData.getStatue().equals("hubD") || orderData.getStatue().equals("deniedD") || orderData.getStatue().equals("hubP")
                                || orderData.getStatue().equals("accepted")  || orderData.getStatue().equals("placed") || orderData.getStatue().equals("recived")
                                || orderData.getStatue().equals("recived2") || orderData.getStatue().equals("denied")) {
                            // --------- Update Values
                            mDatabase.child(orderData.getId()).child("statue").setValue("supD");
                            mDatabase.child(orderData.getId()).child("supDScanTime").setValue(datee);
                            mDatabase.child(orderData.getId()).child("dSupervisor").setValue(UserInFormation.getUser().getSupervisor_code());

                            // ---------------- Add Log
                            String Log = "تم تسليم الشحنه لمشرف التسليم " + UserInFormation.getUser().getSupervisor_code();
                            mDatabase.child(orderData.getId()).child("logs").child(logC).setValue(Log);

                            Toast.makeText(QRScanner.this, "تم استلام الشحنه بنجاح", Toast.LENGTH_SHORT).show();

                        } else if(orderData.getStatue().equals("hub2Denied") || orderData.getStatue().equals("hub1Denied")) {
                            // --------- Update Values
                            mDatabase.child(orderData.getId()).child("statue").setValue("supDenied");
                            mDatabase.child(orderData.getId()).child("supDeniedTime").setValue(datee);

                            // ---------------- Add Log
                            String Log = "تم استلام المرتجع من المخزن. ";
                            mDatabase.child(orderData.getId()).child("logs").child(logC).setValue(Log);

                            Toast.makeText(QRScanner.this, "تم استلام المرتجع من المخزن", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(QRScanner.this, "لا يمكنك استلام تلك الشحنه راجع الشحنه مع المخزن", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                } else {
                    Toast.makeText(QRScanner.this, "رقم التتبع غير صحيح، حاول مره اخري ..", Toast.LENGTH_SHORT).show();
                }

                // -------------- Start Scanning Again in 5 Secs ----------
                loading.setVisibility(View.GONE);
                startCounter(3000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    // ---------- Start a Delay Counter ------------ \\
    private void startCounter(int secDelay) {
        resend = new CountDownTimer(secDelay, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long l) {
                txtCounter.setVisibility(View.VISIBLE);
                txtCounter.setText((l / 1000) + "");
            }

            @Override
            public void onFinish() {
                txtCounter.setVisibility(View.GONE);
                mScannerView.resumeCameraPreview(QRScanner.this);
            }
        }.start();
    }
}