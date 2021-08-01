package com.armjld.rayashipping.PDF;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.Models.UserData;
import com.armjld.rayashipping.R;
import com.tejpratapsingh.pdfcreator.activity.PDFCreatorActivity;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;
import com.tejpratapsingh.pdfcreator.views.PDFBody;
import com.tejpratapsingh.pdfcreator.views.PDFHeaderView;
import com.tejpratapsingh.pdfcreator.views.PDFTableView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFHorizontalView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFImageView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFLineSeparatorView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFTextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class PrintCaptinReport extends PDFCreatorActivity {

    public static ArrayList<Order> reportList;

    PDFTableView.PDFTableRowView tableHeader;
    PDFTableView.PDFTableRowView tableRow;
    PDFTableView tableView;

    ArrayList<String> ordersList = new ArrayList<>();


    public static UserData user;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createPDF(user.getName() + " Report", new PDFUtil.PDFUtilListener() {
            @Override
            public void pdfGenerationSuccess(File savedPDFFile) { }

            @Override
            public void pdfGenerationFailure(Exception exception) { }
        });
    }

    @Override
    protected PDFHeaderView getHeaderView(int page) {
        PDFHeaderView headerView = new PDFHeaderView(getApplicationContext());

        // --- Date
        PDFTextView DateView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        DateView.getView().setGravity(Gravity.LEFT);
        DateView.getView().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        DateView.setText(datee);
        headerView.addView(DateView);

        // --- Create Horizontal View
        PDFHorizontalView horizontalView = new PDFHorizontalView(getApplicationContext());

        // -- Text
        PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.HEADER);
        pdfTextView.setText("تقرير المندوب");
        LinearLayout.LayoutParams prams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        prams.setMargins(60,0,0,0);
        pdfTextView.setLayout(prams);
        pdfTextView.getView().setGravity(Gravity.CENTER);
        pdfTextView.getView().setTypeface(pdfTextView.getView().getTypeface(), Typeface.BOLD);
        horizontalView.addView(pdfTextView);

        // -- Image
        PDFImageView imageView = new PDFImageView(getApplicationContext());
        LinearLayout.LayoutParams imageLayoutParam = new LinearLayout.LayoutParams(60, 60, 0);
        imageView.setImageScale(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(R.drawable.ic_logo);
        imageLayoutParam.setMargins(0, 0, 10, 0);
        imageView.setLayout(imageLayoutParam);
        horizontalView.addView(imageView);

        // --- Add View
        headerView.addView(horizontalView);

        return headerView;
    }

    @Override
    protected PDFBody getBodyViews() {
        PDFBody pdfBody = new PDFBody();

        // --- Text View
        PDFTextView pdfCompanyNameView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H3);
        pdfCompanyNameView.getView().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        pdfCompanyNameView.setText("للمستخدم : " + user.getName());
        pdfBody.addView(pdfCompanyNameView);

        PDFTextView pdfTableTitleView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        pdfTableTitleView.getView().setGravity(Gravity.CENTER);
        pdfTableTitleView.getView().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        pdfTableTitleView.setText("تفاصيل التقرير لعدد : " + reportList.size() + " شحنه");
        pdfBody.addView(pdfTableTitleView);

        // --- Setting Header
        tableHeader = new PDFTableView.PDFTableRowView(getApplicationContext());
        tableHeader.getView().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        setHeader("تاريخ التسليم");
        setHeader("التحصيل");
        setHeader("المدينه");
        setHeader("المستلم");
        setHeader("رقم الشحنه");

        tableRow = new PDFTableView.PDFTableRowView(getApplicationContext());
        tableView = new PDFTableView(getApplicationContext(), tableHeader, tableRow);

        setData();

        pdfBody.addView(tableView);


        // --- Text View
        PDFTextView txtTotal = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        pdfCompanyNameView.getView().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        LinearLayout.LayoutParams ly2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        ly2.setMargins(0,20,0,0);
        txtTotal.setLayout(ly2);
        txtTotal.setText("اجمالي عدد الشحنات : " + reportList.size() + " شحنه.");
        pdfBody.addView(txtTotal);

        return pdfBody;
    }

    private void setData() {
        ordersList.clear();
        Collections.sort(reportList, (lhs, rhs) -> lhs.getDate().compareTo(rhs.getDate()));

        for(int i = 0; i < reportList.size(); i++) {
            ordersList.clear();
            Order orderData = reportList.get(i);

            if(orderData.getDilverTime().length() > 10) {
                ordersList.add(orderData.getDilverTime().substring(5,10));
            } else {
                ordersList.add(orderData.getDilverTime());
            }
            ordersList.add(orderData.getGMoney() + " جنيه");
            ordersList.add(orderData.getmDRegion());
            ordersList.add(orderData.getDName());
            ordersList.add(orderData.getTrackid());
            tableView.addRow(addRow(ordersList));

            PDFLineSeparatorView lineSeparatorView3 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.BLACK);
            tableView.addSeparatorRow(lineSeparatorView3);
        }
    }

    @Override
    protected void onNextClicked(File savedPDFFile) {
        Uri pdfUri = Uri.fromFile(savedPDFFile);

        Intent intentPdfViewer = new Intent(PrintCaptinReport.this, PdfViewerActivity.class);
        intentPdfViewer.putExtra(PdfViewerActivity.PDF_FILE_URI, pdfUri);
        PdfViewerActivity.user = user;
        PdfViewerActivity.title = "Report";
        startActivity(intentPdfViewer);
    }

    private PDFTableView.PDFTableRowView addRow(ArrayList<String> orderinfo) {
        PDFTableView.PDFTableRowView tableRowView = new PDFTableView.PDFTableRowView(getApplicationContext());

        for(int i=0; i < orderinfo.size(); i++) {
            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextView.setText(orderinfo.get(i));
            pdfTextView.getView().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tableRowView.addToRow(pdfTextView);
        }

        return tableRowView;
    }

    private void setHeader(String title) {
        PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        pdfTextView.setText(title);
        pdfTextView.getView().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tableHeader.addToRow(pdfTextView);
    }
}