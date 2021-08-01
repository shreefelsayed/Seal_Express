package com.armjld.rayashipping.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.rayashipping.Captin.CaptinOrderInfo;
import com.armjld.rayashipping.Captin.PartDeliver;
import com.armjld.rayashipping.Captin.capReady;
import com.armjld.rayashipping.CopyingData;
import com.armjld.rayashipping.DeniedReasons;
import com.armjld.rayashipping.OrderStatue;
import com.armjld.rayashipping.OrdersClass;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.SuperVisor.AsignOrder;
import com.armjld.rayashipping.SuperVisor.OrderInfo;
import com.armjld.rayashipping.UpdateDialog;
import com.armjld.rayashipping.Whatsapp;
import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.Models.UserInFormation;
import com.armjld.rayashipping.caculateTime;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.util.ArrayList;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.MyViewHolder> implements Filterable {

    Context mContext;
    ArrayList<Order> filtersData;
    String from;
    ArrayList<Order> mDisplayedValues;


    public DeliveryAdapter(Context mContext, ArrayList<Order> filtersData, String from) {
        this.mContext = mContext;
        this.filtersData = filtersData;
        this.from = from;
        this.mDisplayedValues = filtersData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.card_new_captin, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Order orderData = mDisplayedValues.get(position);
        CopyingData copyingData = new CopyingData(mContext);
        Whatsapp whatsapp = new Whatsapp();
        OrdersClass ordersClass = new OrdersClass(mContext);

        // Get Post Date
        holder.setData(orderData);
        holder.setStatue(orderData);
        holder.setViewer(UserInFormation.getUser().getAccountType(), orderData);

        // ------------------------------------   Order info
        holder.linBody.setOnClickListener(v -> {
            moreInfo(position, orderData);
        });

        holder.btnMore.setOnClickListener(v-> {
            PopupMenu popup = new PopupMenu(mContext, holder.btnMore);
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

        // ---------------- Set order to Recived
        holder.btnRecived.setOnClickListener(v -> {
            int oCount = 0;

            ArrayList<Order> listPickUp = UserInFormation.getUser().getCapPending();

            for(int i = 0; i < listPickUp.size(); i ++) {
                Order oData = listPickUp.get(i);
                if(oData.getuId().equals(orderData.getuId())) {
                    if(oData.getStatue().equals("recived") || oData.getStatue().equals("accepted")) {
                        oCount ++;
                    }
                }
            }

            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) mContext).setMessage("تأكيد استلام الشحنه").setCancelable(true).setPositiveButton("استلام " + oCount + " شحنه", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                for(int i = 0; i < listPickUp.size(); i ++) {
                    Order oData = listPickUp.get(i);
                    if(oData.getuId().equals(orderData.getuId())) {
                        if(oData.getStatue().equals("recived") || oData.getStatue().equals("accepted")) {
                            // ------- Update Database ------
                            ordersClass.orderRecived(oData);

                            // -------- Update Adapter
                            this.notifyItemChanged(position);
                        }

                    }
                }
                dialogInterface.dismiss();
            }).setNegativeButton("استلام هذه الشحنه فقط", R.drawable.ic_close, (dialogInterface, which) -> {
                // ------- Update Database ------
                ordersClass.orderRecived(orderData);

                // -------- Update Adapter
                this.notifyItemChanged(position);
                dialogInterface.dismiss();
            }).build();
            mBottomSheetDialog.show();
        });

        // ----------------------- Return a Denied Order
        holder.btnOrderBack.setOnClickListener(v -> {
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) mContext).setMessage("هل قمت بتسليم الشحنة للتاجر ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                // ------- Update Database ------
                ordersClass.returnOrder(orderData);

                // -------- Update Adapter
                capReady.filterList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDisplayedValues.size());

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        });

        // ----------------------- Set Order As Denied
        holder.btnDenied.setOnClickListener(v -> {
            DeniedReasons.orderData = orderData;
            mContext.startActivity(new Intent(mContext, DeniedReasons.class));
        });

        // ----  Set ORDER as Delivered
        holder.btnDelivered.setOnClickListener(v -> {
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) mContext).setMessage("هل قمت بتسليم الشحنة تسليم كامل ام جزئي ؟").setCancelable(true).setPositiveButton("تسليم كامل", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                // --- Full Deliver (Update Order, Add Money)

                // ------- Update Database ------
                ordersClass.orderDelvered(orderData);
                // -------- Update Adapter
                capReady.filterList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDisplayedValues.size());

                dialogInterface.dismiss();
            }).setNegativeButton("تسليم جزئي", R.drawable.ic_close, (dialogInterface, which) -> {
                // --- Partial Deliver (Update Money and Send Notification)
                PartDeliver.orderData = orderData;
                mContext.startActivity(new Intent(mContext, PartDeliver.class));
                dialogInterface.dismiss();
            }).build();
            mBottomSheetDialog.show();
        });

        // ----- Asign To Another by SuperVisor
        holder.btnAsignOther.setOnClickListener(v-> {
            Intent intent = new Intent(mContext, AsignOrder.class);
            AsignOrder.assignToCaptin.clear();
            AsignOrder.assignToCaptin.add(mDisplayedValues.get(position));
            ((Activity) mContext).startActivityForResult(intent, 1);
        });

        // ----- Mark as Recived as a Supplier By Supper Visor
        holder.btnCapRecived.setOnClickListener(v-> {
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) mContext).setMessage("هل قام المندوب باستلام تلك الشحنه من الراسل ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                // ------- Update Database ------
                ordersClass.setRecived(orderData);

                // ---- Update Adatper
                mDisplayedValues.get(position).setStatue("recived");
                holder.setStatue(mDisplayedValues.get(position));
                notifyItemChanged(position);

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
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

        public Button btnDelivered, btnRecived, btnOrderBack, btnDenied;
        public Button btnCapRecived, btnAsignOther;

        public TextView txtTrackId, txtGetStat, txtgMoney, txtUsername, txtOrderTo, txtOwner,txtDate,txtPack;
        public LinearLayout linerAll, linSuperVisor,linBody;
        public ImageView btnMore;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            myview = itemView;

            txtDate = myview.findViewById(R.id.txtDate);
            btnDelivered = myview.findViewById(R.id.btnDelivered);
            txtGetStat = myview.findViewById(R.id.txtStatue);
            linerAll = myview.findViewById(R.id.linerAll);
            btnRecived = myview.findViewById(R.id.btnRecived);
            txtgMoney = myview.findViewById(R.id.ordercash);
            txtUsername = myview.findViewById(R.id.txtUsername);
            txtOrderTo = myview.findViewById(R.id.orderto);
            btnOrderBack = myview.findViewById(R.id.btnOrderBack);
            btnDenied = myview.findViewById(R.id.btnDenied);
            txtTrackId = myview.findViewById(R.id.txtTrackId);
            linSuperVisor = myview.findViewById(R.id.linSuperVisor);
            btnAsignOther = myview.findViewById(R.id.btnAsignOther);
            btnCapRecived = myview.findViewById(R.id.btnCapRecived);
            txtOwner = myview.findViewById(R.id.txtOwner);
            linBody = myview.findViewById(R.id.linBody);
            btnMore = myview.findViewById(R.id.btnMore);
            txtPack = myview.findViewById(R.id.txtPack);
        }

        @SuppressLint("SetTextI18n")
        public void setData(Order data) {
            txtTrackId.setText(data.getTrackid());
            if(data.getStatue().equals("accepted") || data.getStatue().equals("recived") || data.getStatue().equals("recived2") || data.getStatue().equals("capDenied")) {
                txtOrderTo.setText(data.reStateP() + " - " + data.getmPAddress());
            } else if(data.getStatue().equals("readyD") || data.getStatue().equals("denied")) {
                txtOrderTo.setText(data.reStateD() + " - " + data.getDAddress());
            }
            txtgMoney.setText(data.getGMoney() + " ج");
            txtOwner.setText(data.getOwner());
            txtUsername.setText(data.getDName());
            txtPack.setText(data.getPackType());

            if(!data.getIsPart().equals("false")) {
                String text = "<font color=#FF0000>" + data.getOrignalMoney() + " ج / " +"</font> <font color=#008000>" + data.getGMoney() + " ج" +"</font>";
                txtgMoney.setText(Html.fromHtml(text));
                setReturns(data);
            }

            setDatee(data);
        }

        private void setReturns(Order orderData) {
            String text = "<font color=#FF0000>" + orderData.getItemReturned() + " - " +"</font> <font color=#008000>" + orderData.getPackType() +"</font>";
            txtPack.setText(Html.fromHtml(text));
        }

        public void setDatee(Order data) {
            caculateTime cac = new caculateTime();
            txtDate.setText(cac.setPostDate(data.getReadyDTime()));
        }


        public void setStatue(Order orderData) {
            OrderStatue orderStatue = new OrderStatue();

            switch (orderData.getStatue()) {
                case "accepted":
                    btnDelivered.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.VISIBLE);
                    btnOrderBack.setVisibility(View.GONE);
                    txtgMoney.setVisibility(View.GONE);
                    btnDenied.setVisibility(View.GONE);
                    txtGetStat.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                    txtGetStat.setVisibility(View.VISIBLE);
                    if (!orderData.getpHubName().equals(""))
                        txtOrderTo.setText(orderData.getpHubName());
                    break;
                case "recived":
                    btnDelivered.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    txtgMoney.setVisibility(View.GONE);
                    btnDenied.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.VISIBLE);
                    txtGetStat.setVisibility(View.VISIBLE);
                    txtGetStat.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                    if (!orderData.getpHubName().equals(""))
                        txtOrderTo.setText(orderData.getpHubName());
                    break;
                case "recived2":
                    btnDelivered.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    txtgMoney.setVisibility(View.GONE);
                    btnDenied.setVisibility(View.GONE);
                    txtGetStat.setVisibility(View.VISIBLE);
                    txtGetStat.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    if (!orderData.getpHubName().equals(""))
                        txtOrderTo.setText(orderData.getpHubName());
                    break;
                case "readyD":
                    btnRecived.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    txtgMoney.setVisibility(View.VISIBLE);
                    btnDenied.setVisibility(View.VISIBLE);
                    btnDelivered.setVisibility(View.VISIBLE);
                    txtGetStat.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                    txtGetStat.setVisibility(View.VISIBLE);
                    break;
                case "denied":
                    btnDelivered.setVisibility(View.VISIBLE);
                    btnRecived.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    txtgMoney.setVisibility(View.GONE);
                    btnDenied.setVisibility(View.GONE);
                    txtGetStat.setVisibility(View.VISIBLE);
                    txtGetStat.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                    break;
                case "capDenied":
                    btnDelivered.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.GONE);
                    txtgMoney.setVisibility(View.GONE);
                    btnDenied.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.VISIBLE);
                    txtGetStat.setVisibility(View.VISIBLE);
                    txtGetStat.setBackgroundTintList(ColorStateList.valueOf(Color.MAGENTA));
                    break;
                default:
                    btnDelivered.setVisibility(View.GONE);
                    txtGetStat.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    btnDenied.setVisibility(View.GONE);
                    txtGetStat.setVisibility(View.VISIBLE);
                    txtGetStat.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                    break;
            }

            txtGetStat.setText(orderStatue.shortState(orderData));
        }

        public void setViewer(String accountType, Order data) {
            if (accountType.equals("Supervisor")) {
                btnRecived.setVisibility(View.GONE);
                btnDelivered.setVisibility(View.GONE);
                btnOrderBack.setVisibility(View.GONE);
                btnDenied.setVisibility(View.GONE);
                if(data.getStatue().equals("readyD") || data.getStatue().equals("accepted")) {
                    linSuperVisor.setVisibility(View.VISIBLE);
                    if(data.getStatue().equals("readyD")) {
                        btnCapRecived.setVisibility(View.GONE);
                    }
                }
            } else {
                linSuperVisor.setVisibility(View.GONE);
            }
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
