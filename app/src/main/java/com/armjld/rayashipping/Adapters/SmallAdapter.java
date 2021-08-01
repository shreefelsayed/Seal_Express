package com.armjld.rayashipping.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.rayashipping.Captin.CaptinOrderInfo;
import com.armjld.rayashipping.CopyingData;
import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.Models.UserInFormation;
import com.armjld.rayashipping.OrdersClass;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.SuperVisor.AsignOrder;
import com.armjld.rayashipping.SuperVisor.OrderInfo;
import com.armjld.rayashipping.UpdateDialog;
import com.armjld.rayashipping.Whatsapp;
import com.armjld.rayashipping.caculateTime;
import com.armjld.rayashipping.getRefrence;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SmallAdapter extends RecyclerView.Adapter<SmallAdapter.MyViewHolder> implements Filterable {

    public static caculateTime _cacu = new caculateTime();
    Context mContext;
    ArrayList<Order> filtersData;
    ArrayList<Order> mDisplayedValues;

    public SmallAdapter(Context mContext, ArrayList<Order> filtersData) {
        this.mContext = mContext;
        this.filtersData = filtersData;
        this.mDisplayedValues = filtersData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.card_small_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Order orderData = mDisplayedValues.get(position);

        // Get Post Date
        holder.setOrderto(mDisplayedValues.get(position));
        holder.setPostDate(mDisplayedValues.get(position).getDilverTime());
        holder.setState(mDisplayedValues.get(position));
        holder.setData(orderData);

        OrdersClass ordersClass = new OrdersClass(mContext);
        Whatsapp whatsapp = new Whatsapp();
        CopyingData copyingData = new CopyingData(mContext);

        holder.linBody.setOnClickListener(v-> {
            CaptinOrderInfo.orderData = orderData;
            mContext.startActivity(new Intent(mContext, CaptinOrderInfo.class));
        });

        holder.txtTrackId.setOnClickListener(v-> {
            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("TrackId", holder.txtTrackId.getText().toString().trim());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(mContext, "تم نسخ رقم الشحنه", Toast.LENGTH_LONG).show();
        });

        holder.btnCopyData.setOnClickListener(v-> {
            PopupMenu popup = new PopupMenu(mContext, holder.btnCopyData);
            popup.inflate(R.menu.captin_adapter_menu);

            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.call_sender:
                        ordersClass.callSender(orderData);
                        return true;
                    case R.id.call_reciver:
                        ordersClass.callConsigne(orderData);
                        return true;
                    case R.id.whats_sender:
                        whatsapp.openWhats(orderData.getpPhone(), copyingData.getOrderData(orderData), mContext);
                        return true;
                    case R.id.whats_reciver:
                        whatsapp.openWhats(orderData.getDPhone(), copyingData.getOrderDataToReciver(orderData), mContext);
                        return true;
                    case R.id.copy_track:
                        copyingData.copyTrack(orderData);
                        return true;
                    case R.id.moreinfo:
                        copyingData.getOrderData(orderData);
                        return true;
                    case R.id.update:
                        UpdateDialog updateDialog = new UpdateDialog(mContext);
                        updateDialog.showDialog(orderData);
                        return true;
                    case R.id.key:
                        copyingData.copyKey(orderData);
                        return true;
                    default:
                        return false;
                }
            });
            popup.show();
        });

    }

    private void moreInfo(int position, Order order) {
        Intent intent;
        if (UserInFormation.getUser().getAccountType().equals("Delivery Worker")) {
            CaptinOrderInfo.orderData = order;
            intent = new Intent(mContext, CaptinOrderInfo.class);
            CaptinOrderInfo.orderData = mDisplayedValues.get(position);
        } else {
            OrderInfo.orderData = order;
            intent = new Intent(mContext, OrderInfo.class);
            OrderInfo.orderData = mDisplayedValues.get(position);
        }

        ((Activity) mContext).startActivityForResult(intent, 1);
    }

    @Override
    public int getItemCount() {
        return mDisplayedValues.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public View myview;
        public TextView txtgMoney, txtOrderTo, txtPostDate, txtUsername, txtTrackId,txtClient,txtReturns;
        public TextView txtStatue;
        public ImageView btnCopyData;
        public LinearLayout linBody;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;
            txtgMoney = myview.findViewById(R.id.ordercash);
            txtOrderTo = myview.findViewById(R.id.orderto);
            txtPostDate = myview.findViewById(R.id.txtPostDate);
            txtUsername = myview.findViewById(R.id.txtUsername);
            txtTrackId = myview.findViewById(R.id.txtTrackId);
            txtStatue = myview.findViewById(R.id.txtStatue);
            btnCopyData = myview.findViewById(R.id.btnCopyData);
            txtClient = myview.findViewById(R.id.txtClient);
            txtReturns = myview.findViewById(R.id.txtReturns);
            linBody = myview.findViewById(R.id.linBody);
        }

        @SuppressLint("SetTextI18n")
        public void setOrderto(Order oData) {
            txtOrderTo.setText(oData.getTxtDState() + " - " + oData.getmDRegion() + " - " + oData.getDAddress());
        }

        public void setPostDate(String startDate) {
            txtPostDate.setText(_cacu.setPostDate(startDate));
        }


        @SuppressLint("SetTextI18n")
        public void setState(Order orderData) {
            if(orderData.getStatue().equals("delivered")) {
                txtStatue.setText("تم التسليم");
                txtStatue.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                txtgMoney.setText(orderData.getGMoney() + " ج");

                if(!orderData.getIsPart().equals("false")) {
                    txtStatue.setText("تسليم جزئي");
                    txtStatue.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));

                    String text = "<font color=#FF0000>" + orderData.getOrignalMoney() + " ج / " +"</font> <font color=#008000>" + orderData.getGMoney() + " ج" +"</font>";
                    txtgMoney.setText(Html.fromHtml(text));
                    setReturns(orderData);
                }
            } else {
                txtStatue.setText("مرتجع");
                txtStatue.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }

        private void setReturns(Order orderData) {
            txtReturns.setVisibility(View.VISIBLE);
            String text = "<font color=#FF0000>" + orderData.getItemReturned() + " - " +"</font> <font color=#008000>" + orderData.getPackType() +"</font>";
            txtReturns.setText(Html.fromHtml(text));
        }

        public void setData(Order orderData) {
            txtTrackId.setText(orderData.getTrackid());
            txtUsername.setText(orderData.getOwner());
            txtClient.setText(orderData.getDName());
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {
                mDisplayedValues = (ArrayList<Order>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Order> FilteredArrList = new ArrayList<>();

                if (filtersData == null) {
                    filtersData = new ArrayList<>(mDisplayedValues); // saves the original data in userList
                }

                if (constraint == null || constraint.length() == 0) {
                    results.count = filtersData.size();
                    results.values = filtersData;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < filtersData.size(); i++) {
                        // -- Search
                        if (filtersData.get(i).getOwner().toLowerCase().contains(constraint.toString()) ||
                                filtersData.get(i).getDName().toLowerCase().contains(constraint.toString()) ||
                                filtersData.get(i).reStateD().toLowerCase().contains(constraint.toString()) ||
                                filtersData.get(i).getDPhone().toLowerCase().contains(constraint.toString()) ||
                                filtersData.get(i).getDAddress().toLowerCase().contains(constraint.toString()) ||
                                filtersData.get(i).getTrackid().toLowerCase().contains(constraint.toString()) ||
                                filtersData.get(i).getGMoney().toLowerCase().contains(constraint.toString())) {

                            FilteredArrList.add(filtersData.get(i));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
    }
}