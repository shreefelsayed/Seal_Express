package com.armjld.rayashipping;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import timber.log.Timber;


public class QRScannerTools extends BaseScannerActivity implements ZXingScannerView.ResultHandler {
    TextView txtCounter;
    ImageView btnBack;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());
    private ZXingScannerView mScannerView;
    public static String type = "";
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("raya");


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_q_r_scanner);
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        txtCounter = findViewById(R.id.txtCounter);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v-> finish());

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
        checkTracking(rawResult.getText());
    }

    private void checkTracking(String trackId) {
        mDatabase.orderByChild("trackid").equalTo(trackId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        Order order = ds.getValue(Order.class);
                        makeAction(order);
                        break;
                    }
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void makeAction(Order order) {
        switch (type) {
            case "whatsapp" :
                whats(order);
                break;
            case "phone" :
                phone(order);
                break;
            case "update" :
                update(order);
                break;
            case "denied" :
                denied(order);
                break;
        }
    }

    private void denied(Order order) {
        DeniedReasons.orderData = order;
        startActivity(new Intent(this, DeniedReasons.class));
    }

    private void update(Order order) {
        UpdateDialog updateDialog = new UpdateDialog(this);
        updateDialog.showDialog(order);

        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    private void phone(Order order) {
        CallingPhone callingPhone = new CallingPhone(this);
        callingPhone.callConsigne(order);
    }

    private void whats(Order order) {
        Whatsapp whatsapp = new Whatsapp();
        CopyingData copyingData = new CopyingData(this);
        whatsapp.openWhats(order.getDPhone(), copyingData.getOrderDataToReciver(order), this);
    }


}