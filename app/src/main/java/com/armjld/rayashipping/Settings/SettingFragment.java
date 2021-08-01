package com.armjld.rayashipping.Settings;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.armjld.rayashipping.CaptinHistory;
import com.armjld.rayashipping.Home;
import com.armjld.rayashipping.Login.LoginManager;
import com.armjld.rayashipping.Models.UserInFormation;
import com.armjld.rayashipping.QRScannerTools;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.SuperCaptins.CaptinWalletInfo;
import com.armjld.rayashipping.SuperVisor.AllOrders;
import com.armjld.rayashipping.SuperVisor.SupperVisorWallet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingFragment extends Fragment {

    private static final int READ_EXTERNAL_STORAGE_CODE = 101;
    public static int CAMERA_CODE = 80;
    
    public Context mContext;

    TextView txtName, txtType;
    TextView txtPassSettings, txtSignOut, txtAbout, txtShare, txtPrivacy, txtWalletMoney, txtRate, txtChangePhone;
    ImageView btnBack;
    TextView txtDeliverCount,txtWhatsapp, txtPhoneCall, txtMakeUpdate, txtDenied;
    CircleImageView imgPPP;
    LinearLayout linWallet;
    DatabaseReference uDatabase;
    FirebaseAuth mAuth;
    String uId;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    int TAKE_IMAGE_CODE = 10001;
    private Bitmap bitmap;
    private ProgressDialog mdialog;

    public SettingFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"QueryPermissionsNeeded", "NonConstantResourceId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        mAuth = FirebaseAuth.getInstance();

        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");

        uId = UserInFormation.getUser().getId();
        mdialog = new ProgressDialog(mContext);

        txtName = view.findViewById(R.id.txtName);
        txtType = view.findViewById(R.id.txtType);
        txtChangePhone = view.findViewById(R.id.txtChangePhone);

        imgPPP = view.findViewById(R.id.imgPPP);
        txtWalletMoney = view.findViewById(R.id.txtWalletMoney);
        txtPassSettings = view.findViewById(R.id.txtPassSettings);
        linWallet = view.findViewById(R.id.linWallet);
        txtSignOut = view.findViewById(R.id.txtSignOut);
        btnBack = view.findViewById(R.id.btnBack);
        txtAbout = view.findViewById(R.id.txtAbout);
        txtShare = view.findViewById(R.id.txtShare);
        txtPrivacy = view.findViewById(R.id.txtPrivacy);
        txtRate = view.findViewById(R.id.txtRate);

        txtDeliverCount = view.findViewById(R.id.txtDeliverCount);
        txtWhatsapp = view.findViewById(R.id.txtWhatsapp);
        txtPhoneCall = view.findViewById(R.id.txtPhoneCall);
        txtMakeUpdate = view.findViewById(R.id.txtMakeUpdate);
        txtDenied = view.findViewById(R.id.txtDenied);

        //Title Bar
        TextView tbTitle = view.findViewById(R.id.toolbar_title);
        tbTitle.setText("الإعدادات");

        setUserData();

        btnBack.setOnClickListener(v -> {
            Home.whichFrag = "Home";
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.container, new AllOrders(), Home.whichFrag).addToBackStack("Home").commit();
            Home.bottomNavigationView.setSelectedItemId(R.id.home);
        });

        txtRate.setOnClickListener(v -> {
            final String appPackageName = requireActivity().getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });

        txtWhatsapp.setOnClickListener(v-> {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_DENIED) {
                QRScannerTools.type = "whatsapp";
                startActivity(new Intent(mContext, QRScannerTools.class));
            } else {
                ActivityCompat.requestPermissions((Activity) mContext, new String[] {Manifest.permission.CAMERA}, CAMERA_CODE);
            }
        });

        txtPhoneCall.setOnClickListener(v-> {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_DENIED) {
                QRScannerTools.type = "phone";
                startActivity(new Intent(mContext, QRScannerTools.class));
            } else {
                ActivityCompat.requestPermissions((Activity) mContext, new String[] {Manifest.permission.CAMERA}, CAMERA_CODE);
            }
        });

        txtMakeUpdate.setOnClickListener(v-> {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_DENIED) {
                QRScannerTools.type = "update";
                startActivity(new Intent(mContext, QRScannerTools.class));
            } else {
                ActivityCompat.requestPermissions((Activity) mContext, new String[] {Manifest.permission.CAMERA}, CAMERA_CODE);
            }
        });

        txtDenied.setOnClickListener(v-> {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_DENIED) {
                QRScannerTools.type = "denied";
                startActivity(new Intent(mContext, QRScannerTools.class));
            } else {
                ActivityCompat.requestPermissions((Activity) mContext, new String[] {Manifest.permission.CAMERA}, CAMERA_CODE);
            }
        });

        txtPassSettings.setOnClickListener(v -> startActivity(new Intent(mContext, ChangePassword.class)));
        txtPrivacy.setOnClickListener(v -> startActivity(new Intent(mContext, Terms.class)));
        txtChangePhone.setOnClickListener(v -> startActivity(new Intent(mContext, ChangePhone.class)));
        txtAbout.setOnClickListener(v -> startActivity(new Intent(mContext, About.class)));

        linWallet.setOnClickListener(v -> {
            if(UserInFormation.getUser().getAccountType().equals("Delivery Worker")) {
                startActivity(new Intent(mContext, CaptinWalletInfo.class));
            } else {
                startActivity(new Intent(mContext, SupperVisorWallet.class));
            }
        });

        txtDeliverCount.setOnClickListener(v-> startActivity(new Intent(mContext, CaptinHistory.class)));

        txtWalletMoney.setText(UserInFormation.getUser().getPackMoney());

        txtShare.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "https://play.google.com/store/apps/details?id=com.armjld.rayashipping";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Play Store Link");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "شارك البرنامج مع اخرون"));
        });

        txtSignOut.setOnClickListener(v -> signOut());

        imgPPP.setOnClickListener(v -> {
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_CODE);
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, TAKE_IMAGE_CODE);
                }
            }
        });


        return view;
    }

    // ------------ Set User Data ----------- //
    @SuppressLint("SetTextI18n")
    private void setUserData() {
        txtName.setText(UserInFormation.getUser().getName());
        Picasso.get().load(Uri.parse(UserInFormation.getUser().getPpURL())).into(imgPPP);
        txtDeliverCount.setText("التسليمات : " + UserInFormation.getUser().getMine().size() + " شحنه");

        if (UserInFormation.getUser().getAccountType().equals("Supervisor")) {
            txtType.setText("مشرف");
            txtDeliverCount.setVisibility(View.GONE);
        } else {
            txtType.setText("مندوب شحن");
            txtDeliverCount.setVisibility(View.VISIBLE);
        }

    }

    private void signOut() {
        LoginManager _lgnMng = new LoginManager();
        _lgnMng.clearInfo(requireActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri photoUri = data.getData();
            try {
                Bitmap source = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), photoUri);
                bitmap = resizeBitmap(source, 500);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Uri uri = null;
            try {
                uri = Uri.parse(getFilePath(requireActivity(), photoUri));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            if (uri != null) {
                bitmap = rotateImage(bitmap, uri);
            }

            mdialog.setMessage("تحديث الصورة الشخصية ..");
            mdialog.show();
            imgPPP.setImageBitmap(bitmap);
            handleUpload(bitmap);
        }
    }

    private Bitmap rotateImage(Bitmap bitmap, Uri uri) {
        ExifInterface exifInterface = null;
        try {
            if (uri == null) {
                return bitmap;
            }
            exifInterface = new ExifInterface(String.valueOf(uri));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (exifInterface != null) {
            int orintation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            if (orintation == 6 || orintation == 3 || orintation == 8) {
                Matrix matrix = new Matrix();
                if (orintation == 6) {
                    matrix.postRotate(90);
                } else if (orintation == 3) {
                    matrix.postRotate(180);
                } else {
                    matrix.postRotate(270);
                }
                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } else {
                return bitmap;
            }
        } else {
            return bitmap;
        }

    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("ppUsers").child(uId + ".jpeg");
        final String did = uId;
        reference.putBytes(baos.toByteArray()).addOnSuccessListener(taskSnapshot -> getDownUrl(did, reference));
    }

    private void getDownUrl(final String uIDd, StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(uri -> {
            uDatabase.child(uIDd).child("ppURL").setValue(uri.toString());
            UserInFormation.getUser().setPpURL(uri.toString());
            Toast.makeText(mContext, "تم تغيير البيانات بنجاح", Toast.LENGTH_SHORT).show();
            mdialog.dismiss();
        });
    }

    // ------------------- CHEECK FOR PERMISSIONS -------------------------------//
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(requireActivity(), permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void openWebURL(String inURL) {
        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(inURL));
        startActivity(browse);
    }

    @SuppressLint({"NewApi", "Recycle"})
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception ignored) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static Bitmap resizeBitmap(Bitmap source, int maxLength) {
        try {
            if (source.getHeight() >= source.getWidth()) {
                if (source.getHeight() <= maxLength) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                int targetWidth = (int) (maxLength * aspectRatio);

                return Bitmap.createScaledBitmap(source, targetWidth, maxLength, false);
            } else {
                if (source.getWidth() <= maxLength) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = ((double) source.getHeight()) / ((double) source.getWidth());
                int targetHeight = (int) (maxLength * aspectRatio);

                return Bitmap.createScaledBitmap(source, maxLength, targetHeight, false);
            }
        } catch (Exception e) {
            return source;
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