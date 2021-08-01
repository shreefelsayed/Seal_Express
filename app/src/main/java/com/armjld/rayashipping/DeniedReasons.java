package com.armjld.rayashipping;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.rayashipping.Captin.capReady;
import com.armjld.rayashipping.Login.LoginManager;
import com.armjld.rayashipping.Login.StartUp;
import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.Models.UserInFormation;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class DeniedReasons extends AppCompatActivity {

    private RadioButton rd1,rd2,rd3,rd4;
    private String Msg = "";
    private EditText txtContact;
    Button btnSend;
    public static Order orderData;
    CheckBox chkRecive,chkTry;
    RadioGroup rdGroup;
    EditText txtRecivedMoney;
    TextInputLayout tlRecivedMoney;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("raya");
    DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denied_reasons);

        rd1 = findViewById(R.id.rd1);
        rd2 = findViewById(R.id.rd2);
        rd3 = findViewById(R.id.rd3);
        rd4 = findViewById(R.id.rd4);
        chkRecive = findViewById(R.id.chkRecive);
        chkTry = findViewById(R.id.chkTry);
        txtContact = findViewById(R.id.txtContact);
        rdGroup = findViewById(R.id.rdGroup);
        btnSend = findViewById(R.id.btnSend);
        txtRecivedMoney = findViewById(R.id.txtRecivedMoney);
        tlRecivedMoney = findViewById(R.id.tlRecivedMoney);

        tlRecivedMoney.setVisibility(View.GONE);
        chkRecive.setEnabled(false);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("سبب المرتجع");

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v-> finish());

        rdGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.rd3) {
                txtContact.setVisibility(View.VISIBLE);
            } else {
                txtContact.setVisibility(View.GONE);
            }
        });

        chkTry.setOnCheckedChangeListener((buttonView, isChecked) -> chkRecive.setEnabled(isChecked));

        chkRecive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                tlRecivedMoney.setVisibility(View.VISIBLE);
            } else {
                tlRecivedMoney.setVisibility(View.GONE);
            }
        });

        btnSend.setOnClickListener(v -> {
            if(rd1.isChecked()) {
                Msg = rd1.getText().toString();
            } else if (rd2.isChecked()) {
                Msg = rd2.getText().toString();
            } else if(rd4.isChecked()) {
                Msg = rd4.getText().toString();
            } else if(rd3.isChecked()) {
                if(txtContact.getText().toString().isEmpty()) {
                    Toast.makeText(this, "الرجاء توضيح سبب عدم الاستلام", Toast.LENGTH_SHORT).show();
                    return;
                }
                Msg = txtContact.getText().toString();
            } else {
                Toast.makeText(this, "الرجاء توضيح سبب عدم الاستلام", Toast.LENGTH_SHORT).show();
                return;
            }

            if(chkRecive.isChecked() && txtRecivedMoney.getText().toString().trim().isEmpty()) {
                return;
            }

            String strMoney = txtRecivedMoney.getText().toString().trim();

            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(this).setMessage("هل تريد تسجيل الشحنه كمرتجع ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_delete_white, (dialogInterface, which) -> {

                OrdersClass ordersClass = new OrdersClass(this);
                ordersClass.orderDenied(orderData, Msg, chkTry.isChecked());


                if(chkRecive.isChecked()) {
                    mDatabase.child(orderData.getId()).child("recivedMoney").setValue(strMoney);
                    mDatabase.child(orderData.getId()).child("moneyStatue").setValue("courier");

                    orderData.setMoneyStatue("courier");
                    orderData.setRecivedMoney(strMoney);

                    // --- Add Money to Captins PackMoney
                    int CurrentPackMoney = Integer.parseInt(UserInFormation.getUser().getPackMoney());
                    int finalMoney = CurrentPackMoney + Integer.parseInt(orderData.getGMoney());
                    Wallet wallet = new Wallet();
                    wallet.addToUser(UserInFormation.getUser().getId(), Integer.parseInt(strMoney),orderData, "ourmoney");

                    uDatabase.child(UserInFormation.getUser().getId()).child("packMoney").setValue(finalMoney + "");
                    UserInFormation.getUser().setPackMoney(finalMoney + "");
                }

                // ----- Update Adapter
                orderData.setStatue("denied");
                capReady.getOrders();

                finish();
                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        });
    }

}