package com.armjld.rayashipping.SuperCaptins;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.armjld.rayashipping.Adapters.WalletAdapter;
import com.armjld.rayashipping.EnvioMoney;
import com.armjld.rayashipping.Home;
import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.PDF.PrintCaptinMoney;
import com.armjld.rayashipping.PDF.PrintCaptinReport;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Models.CaptinMoney;
import com.armjld.rayashipping.Models.UserInFormation;
import com.armjld.rayashipping.Models.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.util.ArrayList;
import java.util.Collections;

public class CaptinWalletInfo extends AppCompatActivity {

    public static UserData user;
    ArrayList<CaptinMoney> capMoneyList = new ArrayList<>();
    ImageView btnBack, btnPrint;
    TextView txtBouns, txtMoney;
    RecyclerView recyclerWallet;
    WalletAdapter walletAdapter;
    LinearLayout EmptyPanel;
    String uId, walletMoney, packMoney;
    LinearLayout linPack, linBouns;
    SwipeRefreshLayout swipeRefresh;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("raya");
    ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captin_wallet_info);
        progressDialog = new ProgressDialog(this);

        TextView title = findViewById(R.id.toolbar_title);

        title.setText("المحفظه");

        if (UserInFormation.getUser().getAccountType().equals("Supervisor")) {
            uId = user.getId();
            walletMoney = user.getWalletmoney() + "";
            packMoney = user.getPackMoney();
        } else {
            uId = UserInFormation.getUser().getId();
            walletMoney = UserInFormation.getUser().getWalletmoney() + "";
            packMoney = UserInFormation.getUser().getPackMoney();
        }

        btnBack = findViewById(R.id.btnBack);
        txtBouns = findViewById(R.id.txtBouns);
        txtMoney = findViewById(R.id.txtMoney);
        recyclerWallet = findViewById(R.id.recyclerWallet);
        EmptyPanel = findViewById(R.id.EmptyPanel);
        linPack = findViewById(R.id.linPack);
        linBouns = findViewById(R.id.linBouns);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        btnPrint = findViewById(R.id.btnPrint);

        btnBack.setOnClickListener(v -> finish());

        txtBouns.setText(walletMoney + " ج");
        txtMoney.setText(packMoney + " ج");

        recyclerWallet.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerWallet.setLayoutManager(layoutManager);

        swipeRefresh.setRefreshing(true);

        btnPrint.setOnClickListener(v-> {
            printWallet();
        });

        swipeRefresh.setOnRefreshListener(() -> {
            if (UserInFormation.getUser().getAccountType().equals("Supervisor")) {
                refreshData();
            } else if (UserInFormation.getUser().getAccountType().equals("Delivery Worker")) {
                capRefresh();
            }
        });

        // --- Click to Pay Pack Money
        linPack.setOnClickListener(v -> {
            if (UserInFormation.getUser().getAccountType().equals("Supervisor") && !packMoney.equals("0")) {
                BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(this).setMessage("هل قمت باستلام من " + user.getName() + " مبلغ " + packMoney + " ج مستحقات التسليم ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                    EnvioMoney envioMoney = new EnvioMoney(this);
                    envioMoney.payPackMoney(user, packMoney);
                    refreshData();
                    dialogInterface.dismiss();
                }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
                mBottomSheetDialog.show();
            }
        });

        // --- Click to Pay Bouns Money
        linBouns.setOnClickListener(v -> {
            if (UserInFormation.getUser().getAccountType().equals("Supervisor") && !walletMoney.equals("0")) {
                BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(this).setMessage("هل قمت بتسليم " + user.getName() + " مبلغ " + walletMoney + " ج ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {

                    EnvioMoney envioMoney = new EnvioMoney(this);
                    envioMoney.payBouns(user, walletMoney);
                    refreshData();

                    dialogInterface.dismiss();
                }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
                mBottomSheetDialog.show();
            }
        });

        getMoney(uId);
    }

    private void printWallet() {
        progressDialog.setMessage("Loading ..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ArrayList<Order> listWallet = new ArrayList<>();
        ArrayList<String> listId = new ArrayList<>();

        for(int i =0; i < capMoneyList.size(); i++) {
            addToList(capMoneyList.get(i).getOrderid(), i, listWallet,listId);
            listId.add(capMoneyList.get(i).getTrackId());
        }
    }

    private void addToList(String orderid, int i, ArrayList<Order> listWallet, ArrayList<String> listId) {
        mDatabase.child(orderid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Order order = snapshot.getValue(Order.class);

                if(!listId.contains(orderid)) {
                    listWallet.add(order);
                    listId.add(orderid);
                }

                if(i == (capMoneyList.size() - 1)) {
                    finalOrder(listWallet);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void finalOrder(ArrayList<Order> listWallet) {
        progressDialog.dismiss();
        PrintCaptinMoney.listOrders = listWallet;
        PrintCaptinMoney.user = UserInFormation.getUser();
        startActivity(new Intent(this, PrintCaptinMoney.class));
    }

    private void refreshData() {
        swipeRefresh.setRefreshing(true);
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(UserData.class);
                assert user != null;
                getMoney(user.getId());
                walletMoney = String.valueOf(user.getWalletmoney());
                packMoney = user.getPackMoney();

                txtBouns.setText(walletMoney + " ج");
                txtMoney.setText(packMoney + " ج");

                Home.getCaptins();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void capRefresh() {
        swipeRefresh.setRefreshing(true);
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getUser().getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData _user = snapshot.getValue(UserData.class);
                assert _user != null;

                walletMoney = String.valueOf(_user.getWalletmoney());
                packMoney = _user.getPackMoney();

                UserInFormation.getUser().setWalletmoney(Integer.parseInt(walletMoney));
                UserInFormation.getUser().setPackMoney(packMoney);


                txtBouns.setText(walletMoney + " ج");
                txtMoney.setText(packMoney + " ج");

                getMoney(_user.getId());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getMoney(String uId) {
        capMoneyList.clear();
        capMoneyList.trimToSize();

        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("payments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        CaptinMoney captinMoney = ds.getValue(CaptinMoney.class);
                        assert captinMoney != null;
                        if (captinMoney.getIsPaid().equals("false")) {
                            capMoneyList.add(captinMoney);
                        }
                    }
                }

                Collections.sort(capMoneyList, (lhs, rhs) -> lhs.getDate().compareTo(rhs.getDate()));

                walletAdapter = new WalletAdapter(capMoneyList, CaptinWalletInfo.this);
                recyclerWallet.setAdapter(walletAdapter);
                swipeRefresh.setRefreshing(false);
                updateNone(capMoneyList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }


    private void updateNone(int listSize) {
        if (listSize > 0) {
            EmptyPanel.setVisibility(View.GONE);
        } else {
            EmptyPanel.setVisibility(View.VISIBLE);
        }
    }
}