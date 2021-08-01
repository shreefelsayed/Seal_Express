package com.armjld.rayashipping.SuperCaptins;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.UsersClass;
import com.armjld.rayashipping.Models.UserData;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

public class MyCaptinEdit extends AppCompatActivity {

    ImageView btnBack;
    TextView txtAddBouns, txtCloseAccount, txtMoney,txtComments;
    public static UserData user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_captin_edit);

        if(user == null) finish();

        btnBack = findViewById(R.id.btnBack);
        txtAddBouns = findViewById(R.id.txtAddBouns);
        txtCloseAccount = findViewById(R.id.txtCloseAccount);
        txtMoney = findViewById(R.id.txtMoney);
        txtComments = findViewById(R.id.txtComments);

        btnBack.setOnClickListener(v-> finish());

        // --- Toolbar
        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("بيانات الحساب");

        txtAddBouns.setOnClickListener(v-> {
            MyCaptinAddBouns.user = user;
            startActivity(new Intent(this, MyCaptinAddBouns.class));
        });

        txtComments.setOnClickListener(v-> {
            CaptinsReviews.user = user;
            startActivity(new Intent(this, CaptinsReviews.class));
        });

        txtMoney.setOnClickListener(v-> {
            EditCaptinBouns.user = user;
            startActivity(new Intent(this, EditCaptinBouns.class));
        });

        txtCloseAccount.setOnClickListener(v-> {
            String msg = "هل تريد بإغلاق حساب " + user.getName() + " ؟ سيتم حذف حساب المندوب من المندوبين الخاصين بك";

            if(!user.getPackMoney().equals("0")) {
                msg = "يوجد مبلغ " + user.getPackMoney() + " جنيه مستحقات علي المندوب في حاله اغلاق حسابه الان سيتم تحويلها علي محفظتك، هل انت متاكد من انك تريد اغلاق حساب الكابتن قبل استلام المستحقات ؟";
            }

            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(this).setMessage(msg).setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {

                // --- Remove User
                UsersClass usersClass = new UsersClass(this);
                usersClass.unActiveCaptin(user);

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        });



    }
}