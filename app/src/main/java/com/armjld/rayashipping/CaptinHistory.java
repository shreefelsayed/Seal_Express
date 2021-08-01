package com.armjld.rayashipping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.armjld.rayashipping.Adapters.DeliveryAdapter;
import com.armjld.rayashipping.Adapters.SmallAdapter;
import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.Models.UserInFormation;
import com.armjld.rayashipping.SuperCaptins.MyCaptinInfo;

import java.util.ArrayList;

public class CaptinHistory extends AppCompatActivity {

    RecyclerView recycler;
    ImageView btnBack;
    ArrayList<Order> filterList = new ArrayList<>();
    SmallAdapter smallAdapter;
    LinearLayout EmptyPanel;
    EditText txtSearch;
    TextView txtCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captin_history);

        btnBack = findViewById(R.id.btnBack);
        recycler = findViewById(R.id.recycler);
        EmptyPanel = findViewById(R.id.EmptyPanel);
        txtSearch = findViewById(R.id.txtSearch);
        txtCount = findViewById(R.id.txtCount);

        TextView fitlerTitle = findViewById(R.id.toolbar_title);
        fitlerTitle.setText("الشحنات التي تم تسليمها");

        btnBack.setOnClickListener(v-> finish());

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(smallAdapter != null) {
                    smallAdapter.getFilter().filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        //Recycler
        recycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recycler.setLayoutManager(layoutManager);

        getOrders();
    }

    private void getOrders() {
        filterList = UserInFormation.getUser().getMine();
        smallAdapter = new SmallAdapter(this, filterList);
        recycler.setAdapter(smallAdapter);
        updateNone(filterList.size());
    }

    @SuppressLint("SetTextI18n")
    private void updateNone(int listSize) {
        if (listSize > 0) {
            EmptyPanel.setVisibility(View.GONE);
        } else {
            EmptyPanel.setVisibility(View.VISIBLE);
        }

        txtCount.setText(listSize + " شحنه");
    }
}