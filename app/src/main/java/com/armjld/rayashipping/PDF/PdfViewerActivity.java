package com.armjld.rayashipping.PDF;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.armjld.rayashipping.Models.UserData;
import com.armjld.rayashipping.R;
import com.tejpratapsingh.pdfcreator.activity.PDFViewerActivity;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;

import java.io.File;
import java.net.URLConnection;

public class PdfViewerActivity extends PDFViewerActivity {

    public static UserData user;
    public static String title = "Invoice";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Pdf Viewer");
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                    .getColor(R.color.colorTransparentBlack)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_pdf_viewer, menu);
        // return true so that the menu pop up is opened
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuPrintPdf: {
                File fileToPrint = getPdfFile();
                if (fileToPrint == null || !fileToPrint.exists()) {
                    break;
                }

                PrintAttributes.Builder printAttributeBuilder = new PrintAttributes.Builder();
                printAttributeBuilder.setMediaSize(PrintAttributes.MediaSize.ISO_A4);
                printAttributeBuilder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);

                PDFUtil.printPdf(PdfViewerActivity.this, fileToPrint, printAttributeBuilder.build());
                break;
            }

            case R.id.menuSharePdf: {
                File fileToShare = getPdfFile();
                if (fileToShare == null || !fileToShare.exists()) {
                    break;
                }

                Intent intentShareFile = new Intent(Intent.ACTION_SEND);

                Uri apkURI = FileProvider.getUriForFile(
                        getApplicationContext(),
                        getApplicationContext()
                                .getPackageName() + ".provider", fileToShare);
                intentShareFile.setDataAndType(apkURI, URLConnection.guessContentTypeFromName(fileToShare.getName()));
                intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                intentShareFile.putExtra(Intent.EXTRA_STREAM,
                        Uri.parse("file://" + fileToShare.getAbsolutePath()));

                startActivity(Intent.createChooser(intentShareFile, "Share File"));
                break;
            }

            case R.id.mail : {
                String emailTo = "";
                if(user != null) emailTo = user.getEmail();
                File fileToShare = getPdfFile();
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {emailTo});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Seal : " + title);
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Your " + title + " With Seal");
                if (!fileToShare.exists() || !fileToShare.canRead()) {
                    break;
                }

                Uri uri = FileProvider.getUriForFile(PdfViewerActivity.this, "com.armjld.envio.provider", fileToShare);
                emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(emailIntent,"Pick an Email provider"));
            }
        }
        return super.onOptionsItemSelected(item);
    }
}