package com.armjld.rayashipping.Chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.armjld.rayashipping.Home;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.SuperVisor.AllOrders;
import com.armjld.rayashipping.Models.ChatsData;

import java.util.ArrayList;

@SuppressLint("StaticFieldLeak")
public class ChatFragmet extends Fragment {

    public static chatsAdapter _chatsAdapter;
    public static String cameFrom = "Profile";
    public static Context mContext;
    public static RecyclerView recyclerChat;
    public static ArrayList<ChatsData> mChat;
    public static LinearLayout EmptyPanel;
    ImageView btnBack;
    SwipeRefreshLayout refresh;

    public ChatFragmet() {
    }

    public static ChatFragmet newInstance() {
        ChatFragmet fragment = new ChatFragmet();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static void getMessages() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            _chatsAdapter = new chatsAdapter(mContext, mChat);
            if (recyclerChat != null) {
                recyclerChat.setAdapter(_chatsAdapter);
                updateNone(mChat.size());
            }
        }
    }

    private static void updateNone(int listSize) {
        if (listSize > 0) {
            EmptyPanel.setVisibility(View.GONE);
        } else {
            EmptyPanel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerChat = view.findViewById(R.id.recyclerChat);
        recyclerChat.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerChat.setLayoutManager(layoutManager);
        refresh = view.findViewById(R.id.refresh);


        TextView tbTitle = view.findViewById(R.id.toolbar_title);
        btnBack = view.findViewById(R.id.btnBack);
        EmptyPanel = view.findViewById(R.id.EmptyPanel);
        tbTitle.setText("المحادثات");

        btnBack.setOnClickListener(v -> {
            Home.whichFrag = "Home";
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.container, new AllOrders(), Home.whichFrag).addToBackStack("Home").commit();
            Home.bottomNavigationView.setSelectedItemId(R.id.home);
        });

        _chatsAdapter = new chatsAdapter(getActivity(), mChat);
        recyclerChat.setAdapter(_chatsAdapter);

        // ------------ Refresh View ---------- //
        refresh.setOnRefreshListener(() -> {
            refresh.setRefreshing(true);
            refresh.setRefreshing(false);
        });

        if (mChat.size() >= 1) {
            EmptyPanel.setVisibility(View.GONE);
        } else {
            EmptyPanel.setVisibility(View.VISIBLE);
        }

        return view;
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