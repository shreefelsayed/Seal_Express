package com.armjld.rayashipping.SuperVisor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.armjld.rayashipping.CopyingData;
import com.armjld.rayashipping.MapsActivity;
import com.armjld.rayashipping.OrderStatue;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Whatsapp;
import com.armjld.rayashipping.caculateTime;
import com.armjld.rayashipping.getRefrence;
import com.armjld.rayashipping.Models.Order;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class OrderInfo extends AppCompatActivity {

    private static final int PHONE_CALL_CODE = 100;
    public static Order orderData;
    String orderID;
    DatabaseReference uDatabase, nDatabase;
    String owner;
    TextView date3, date, orderto, OrderFrom, txtPack, txtWeight, ordercash2, fees2, txtPostDate2,txtOrder,orderid;
    TextView dsUsername;
    TextView ddCount;
    TextView dsPAddress, dsDAddress, txtCustomerName;
    ImageView supPP, btnOrderMap;
    ImageView btnClose;
    ImageView icTrans;
    TextView txtNotes;
    LinearLayout linSupplier, advice,linSender;
    ImageView btnWhatsSender, btnWhatsReciver,btnUpdates;

    String orderState = "placed";
    String acceptedTime = "";
    String lastEdit = "";
    String dName = "";
    String ownerName = "";
    String dPhone = "";
    String ownerPhone;
    int position = 0;
    private boolean hasMore = false;
    private boolean toClick = true;
    FloatingActionButton btnCallSupplier, btnCall;
    OrderStatue orderStatue = new OrderStatue();
    Whatsapp whatsapp = new Whatsapp();

    @Override
    public void onBackPressed() {
        finish();
        //getBack();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);

        if (orderData == null) {
            finish();
            return;
        }

        orderID = orderData.getId();
        owner = orderData.getuId();

        position = getIntent().getIntExtra("position", 0);
        String _from = getIntent().getStringExtra("from");

        toClick = !_from.equals("SameUser");

        uDatabase = getInstance().getReference().child("Pickly").child("users");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");

        dsUsername = findViewById(R.id.ddUsername);
        dsPAddress = findViewById(R.id.txtDAddress);
        dsDAddress = findViewById(R.id.dsDAddress);
        linSender = findViewById(R.id.linSender);
        supPP = findViewById(R.id.supPP);
        ddCount = findViewById(R.id.ddCount);
        btnClose = findViewById(R.id.btnClose);
        txtPostDate2 = findViewById(R.id.txtPostDate2);
        txtOrder = findViewById(R.id.txtOrder);
        txtCustomerName = findViewById(R.id.txtCustomerName);
        btnOrderMap = findViewById(R.id.btnOrderMap);
        date3 = findViewById(R.id.date3);
        date = findViewById(R.id.date);
        orderto = findViewById(R.id.orderto);
        OrderFrom = findViewById(R.id.OrderFrom);
        txtPack = findViewById(R.id.txtPack);
        txtWeight = findViewById(R.id.txtWeight);
        ordercash2 = findViewById(R.id.ordercash2);
        fees2 = findViewById(R.id.fees2);
        icTrans = findViewById(R.id.icTrans);
        txtNotes = findViewById(R.id.txtNotes);
        linSupplier = findViewById(R.id.linSupplier);
        advice = findViewById(R.id.advice);
        btnCall = findViewById(R.id.btnCall);
        btnCallSupplier = findViewById(R.id.btnCallSupplier);
        orderid = findViewById(R.id.orderid);
        btnWhatsSender = findViewById(R.id.btnWhatsSender);
        btnWhatsReciver = findViewById(R.id.btnWhatsReciver);
        btnUpdates = findViewById(R.id.btnUpdates);


        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("بيانات الشحنة");

        btnClose.setOnClickListener(v -> finish());
        linSupplier.setVisibility(View.GONE);

        orderState = orderData.getStatue();
        acceptedTime = orderData.getAcceptedTime();
        lastEdit = orderData.getLastedit();
        dName = orderData.getDName();
        dPhone = orderData.getDPhone();


        setPostDate(orderData.getDate());

        String from = orderData.reStateP();
        String to = orderData.reStateD();

        String PAddress = "العنوان : " + from + " - " + orderData.getmPAddress();
        String DAddress = "العنوان : " + to;
        String fees = orderData.getReturnMoney();
        String money = orderData.getGMoney() + " ج";
        String pDate = "تاريخ الاستلام : " + orderData.getpDate();
        String dDate = "تاريخ التسليم : " + orderData.getDDate();
        String pack = "المحتوي : " + orderData.getPackType();
        String weight = "الوزن : " + orderData.getPackWeight() + " كيلو";
        DAddress = DAddress + " - " + orderData.getDAddress();


        if (orderData.getProvider().equals("Esh7nly")) {
            advice.setVisibility(View.VISIBLE);
        } else {
            advice.setVisibility(View.GONE);
        }

        if (orderData.getNotes().equals("")) txtNotes.setVisibility(View.GONE);

        boolean toPick = orderData.getStatue().equals("placed") || orderData.getStatue().equals("accepted") || orderData.getStatue().equals("recived");
        boolean toDelv = orderData.getStatue().equals("readyD") || orderData.getStatue().equals("supD") || orderData.getStatue().equals("capDenied") || orderData.getStatue().equals("supDenied");


        if (toPick || toDelv) btnOrderMap.setVisibility(View.VISIBLE);

        // ------- Set the Data
        fees2.setText(fees + " ج");
        ordercash2.setText(money);
        date3.setText(pDate);
        date.setText(dDate);
        txtPack.setText(pack);
        txtWeight.setText(weight);
        orderto.setText(to);
        OrderFrom.setText(from);
        txtNotes.setText("الملاحظات : " + orderData.getNotes());
        txtCustomerName.setText("اسم المستلم : " + orderData.getDName());
        dsPAddress.setText(PAddress);
        dsDAddress.setText(DAddress);
        txtOrder.setText(orderStatue.shortState(orderData));
        orderid.setText("رقم التتبع : " + orderData.getTrackid());

        // --- Open Map of Order
        btnOrderMap.setOnClickListener(v -> {
            if(orderData.getLat().equals("") || orderData.get_long().equals("")) return;
            ArrayList<Order> order = new ArrayList<>();
            order.add(orderData);

            MapsActivity.filterList = order;
            Intent map = new Intent(this, MapsActivity.class);
            startActivity(map);
        });

        btnWhatsReciver.setOnClickListener(v-> {
            CopyingData copyingData = new CopyingData(this);
            whatsapp.openWhats(dPhone, copyingData.getOrderDataToReciver(orderData), this);

        });

        btnWhatsSender.setOnClickListener(v-> {
            CopyingData copyingData = new CopyingData(this);
            whatsapp.openWhats(ownerPhone, copyingData.getOrderData(orderData), this);
        });

        orderid.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("TrackID", orderData.getTrackid());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "تم نسخ رقم التتبع الخاص بالشحنه", Toast.LENGTH_LONG).show();
        });

        linSender.setOnClickListener(v-> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("SenderPhone", ownerPhone);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "تم نسخ رقم هاتف الراسل", Toast.LENGTH_LONG).show();
        });

        txtCustomerName.setOnClickListener(v-> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("ReciverPhone", dPhone);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "تم نسخ رقم هاتف المرسل له", Toast.LENGTH_LONG).show();
        });

        // --- Call Customer
        btnCall.setOnClickListener(v -> {
            if (orderData.getDPhone().equals(""))  return;
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(OrderInfo.this).setMessage("هل تريد الاتصال بالعميل ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_add_phone, (dialogInterface, which) -> {

                checkPermission(Manifest.permission.CALL_PHONE, PHONE_CALL_CODE);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + dPhone));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        });

        btnUpdates.setOnClickListener(v-> {
            OrdersHistory.orderData = orderData;
            startActivity(new Intent(this, OrdersHistory.class));
        });

        // --- Call Supplier
        btnCallSupplier.setOnClickListener(v-> {
            if (orderData.getpPhone().equals(""))  return;
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(OrderInfo.this).setMessage("هل تريد الاتصال بالراسل ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_add_phone, (dialogInterface, which) -> {
                checkPermission(Manifest.permission.CALL_PHONE, PHONE_CALL_CODE);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + orderData.getpPhone()));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        });

        // ------- Set More Supplier Data ------- //
        getSupData(owner);
    }

    private void getSupData(String owner) {
        dsUsername.setText(orderData.getOwner());
        if(owner.equals("")) return;
        uDatabase.child(owner).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Supplier Main Info ---
                ownerName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                String dsPP = Objects.requireNonNull(snapshot.child("ppURL").getValue()).toString();
                Picasso.get().load(Uri.parse(dsPP)).into(supPP);
                dsUsername.setText(ownerName);
                ownerPhone = Objects.requireNonNull(snapshot.child("phone").getValue()).toString();

                linSupplier.setVisibility(View.VISIBLE); // Show the Info
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void setPostDate(String startDate) {
        caculateTime _cacu = new caculateTime();
        txtPostDate2.setText(_cacu.setPostDate(startDate));
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PHONE_CALL_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Phone Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Phone Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
