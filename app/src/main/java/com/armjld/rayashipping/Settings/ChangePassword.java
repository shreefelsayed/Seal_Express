package com.armjld.rayashipping.Settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.rayashipping.Login.Login_Options;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Models.UserInFormation;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ChangePassword extends Activity {


    private final String uId = UserInFormation.getUser().getId();
    EditText txtOldPass, txtPass1, txtPass2;
    Button confirm;
    private FirebaseAuth mAuth;
    private ProgressDialog mdialog;
    private DatabaseReference uDatabase;
    private TextInputLayout tlOldPass, tlPass1, tlPass2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, Login_Options.class));
            Toast.makeText(this, "الرجاء تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");

        txtOldPass = findViewById(R.id.txtOldPass);
        txtPass1 = findViewById(R.id.txtPass1);
        txtPass2 = findViewById(R.id.txtPass2);

        tlOldPass = findViewById(R.id.tlOldPass);
        tlPass1 = findViewById(R.id.tlPass1);
        tlPass2 = findViewById(R.id.tlPass2);

        confirm = findViewById(R.id.btnEditInfo);
        mdialog = new ProgressDialog(this);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("تغيير كلمة المرور");

        confirm.setOnClickListener(view -> {
            String strOldPass = txtOldPass.getText().toString();
            String strPass1 = txtPass1.getText().toString();
            String strPass2 = txtPass2.getText().toString();

            if (strPass1.isEmpty()) {
                tlPass1.setError("يجب ادخال كلمة سر");
                txtPass1.requestFocus();
                return;
            }

            if (strPass1.length() < 6) {
                tlPass1.setError("يجب ان تكون كلمة المرور من 6 احرف علي الاقل");
                txtPass1.requestFocus();
                return;
            }

            if (!strPass1.equals(strPass2)) {
                tlPass2.setError("تأكد من تطابق كلمتين المرور");
                txtPass2.requestFocus();
                return;
            }

            if (!strOldPass.equals(UserInFormation.getUser().getMpass())) {
                tlOldPass.setError("اكلمة المرور القديمة خاطئة");
                txtOldPass.requestFocus();
                return;
            }

            if (strOldPass.equals(strPass1)) {
                tlPass1.setError("لا يمكن ان تكون كلمة المرور الجديدة هي نفسها القديمة");
                txtPass1.requestFocus();
                return;
            }

            mdialog.setMessage("جاري تغيير الرقم السري ...");
            mdialog.show();

            AuthCredential credential = EmailAuthProvider.getCredential(UserInFormation.getUser().getEmail(), UserInFormation.getUser().getMpass()); // Current Login Credentials \\
            Objects.requireNonNull(mAuth.getCurrentUser()).reauthenticate(credential).addOnCompleteListener(reAuth -> {
                if (reAuth.isSuccessful()) {
                    mAuth.getCurrentUser().updatePassword(strPass1).addOnCompleteListener(changePass -> {
                        if (changePass.isSuccessful()) {
                            uDatabase.child(uId).child("mpass").setValue(strPass1);
                            UserInFormation.getUser().setMpass(strPass1);
                            mdialog.dismiss();
                            Toast.makeText(this, "تم تغيير الرقم السري", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            mdialog.dismiss();
                            Toast.makeText(ChangePassword.this, "حدث خطأ في تغير الرقم السري", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mdialog.dismiss();
                    Toast.makeText(this, "Couldn't re Authenticate", Toast.LENGTH_SHORT).show();
                }
            });
        });

        textWatcher();
    }

    private void textWatcher() {
        txtOldPass.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tlOldPass.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtPass1.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tlPass1.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtPass2.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tlPass2.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

}
