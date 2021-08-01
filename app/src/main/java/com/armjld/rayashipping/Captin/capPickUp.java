package com.armjld.rayashipping.Captin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.armjld.rayashipping.Adapters.DeliveryAdapter;
import com.armjld.rayashipping.Home;
import com.armjld.rayashipping.Models.UserInFormation;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.SuperVisor.AllOrders;

import java.util.ArrayList;
import java.util.Collections;

import timber.log.Timber;

public class capPickUp extends Fragment {

    public static ArrayList<Order> filterList = new ArrayList<>();
    public static RecyclerView recyclerView;
    public static DeliveryAdapter orderAdapter;
    public static Context mContext;
    static LinearLayout EmptyPanel;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public capPickUp() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_captin_avillable, container, false);

        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
        EmptyPanel = view.findViewById(R.id.EmptyPanel);
        recyclerView = view.findViewById(R.id.recycler);

        EmptyPanel.setVisibility(View.GONE);

        //Recycler
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        // ------------------------ Refresh the recycler view ------------------------------- //
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            Home.getCaptinOrders();
            mSwipeRefreshLayout.setRefreshing(false);
        });

        getOrders();

        return view;
    }

    public static void getOrders() {
        Timber.i("Setting orders in Home Fragment");
        filterList = UserInFormation.getUser().getCapPending();
        Collections.sort(filterList, (lhs, rhs) -> rhs.getDate().compareTo(lhs.getDate()));

        if (mContext != null) {
            AllOrders.tabs.getTabAt(0).getOrCreateBadge().setNumber(UserInFormation.getUser().getCapPending().size());
            orderAdapter = new DeliveryAdapter(mContext, filterList, "Home");
            recyclerView.setAdapter(orderAdapter);
            updateNone(filterList.size());
        }
    }

    @SuppressLint("SetTextI18n")
    private static void updateNone(int listSize) {
        if (listSize > 0) {
            EmptyPanel.setVisibility(View.GONE);
        } else {
            EmptyPanel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }
}