package com.armjld.rayashipping;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.armjld.rayashipping.Models.Order;
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;

import java.util.ArrayList;

public class UpdateDialog {
    Context mContext;
    
    public UpdateDialog(Context mContext) {
        this.mContext = mContext;
    }
    
    public void showDialog(Order order) {
        AlertDialog.Builder myUpdate = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View dialogRate = inflater.inflate(R.layout.dialog_update, null);
        myUpdate.setView(dialogRate);
        final AlertDialog dialog = myUpdate.create();
        dialog.setCancelable(false);
        dialog.show();

        OrderStatue orderStatue = new OrderStatue();

        SmartMaterialSpinner spnUpdate = dialog.findViewById(R.id.spnUpdate);
        EditText txtNotes = dialog.findViewById(R.id.txtNotes);
        Button btnSave = dialog.findViewById(R.id.btnSave);
        ImageView btnBack = dialog.findViewById(R.id.btnBack);
        TextView txtState = dialog.findViewById(R.id.txtState);
        ArrayList<String> arrUpdates = new ArrayList<>();

        CheckBox chkNoti = dialog.findViewById(R.id.chkNoti);

        TextView tbTitle = dialog.findViewById(R.id.toolbar_title);
        tbTitle.setText("عمل ابديت");

        arrUpdates.clear();
        arrUpdates.add("الهاتف مغلق");
        arrUpdates.add("الهاتف مشغول");
        arrUpdates.add("العميل لا يرد");
        arrUpdates.add("في انتظار تاكيد العميل");
        arrUpdates.add("لا يرغب في استلام الشحنه");
        arrUpdates.add("قام بتاجيل المعاد");
        arrUpdates.add("العميل غير موجود");
        arrUpdates.add("تم تحديد معاد للتسليم");
        arrUpdates.add("تهرب");

        spnUpdate.setItem(arrUpdates);

        txtState.setText(orderStatue.shortState(order));

        // -- Save The Order
        btnSave.setOnClickListener(v-> {
            String strMsg = txtNotes.getText().toString().trim();

            if(spnUpdate.getSelectedItem().toString().equals("")) {
                Toast.makeText(mContext, "Please choose an update", Toast.LENGTH_SHORT).show();
                return;
            }

            String finalUpate;
            if(!txtNotes.getText().toString().trim().equals("")) {
                finalUpate = spnUpdate.getSelectedItem().toString() + " - " + strMsg;
            } else {
                finalUpate = spnUpdate.getSelectedItem().toString() ;
            }

            sendUpdate(order, finalUpate, chkNoti.isChecked());

            dialog.dismiss();
        });

        btnBack.setOnClickListener(v-> dialog.dismiss());
    }

    private void sendUpdate(Order order, String update, boolean noti) {
        OrdersClass ordersClass = new OrdersClass(mContext);
        ordersClass.setUpdate(order, update, noti);
    }

}
