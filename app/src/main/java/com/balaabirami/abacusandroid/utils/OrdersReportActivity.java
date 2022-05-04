package com.balaabirami.abacusandroid.utils;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.model.Order;
import com.balaabirami.abacusandroid.model.StockTransaction;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;
import com.tejpratapsingh.pdfcreator.views.PDFBody;
import com.tejpratapsingh.pdfcreator.views.PDFFooterView;
import com.tejpratapsingh.pdfcreator.views.PDFHeaderView;
import com.tejpratapsingh.pdfcreator.views.PDFTableView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFHorizontalView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFImageView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFLineSeparatorView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFPageBreakView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrdersReportActivity extends PDFCreatorActivity {

    public static int REPORT_TYPE_STUDENTS = 1;
    public static int REPORT_TYPE_TRANSACTIONS = 2;
    public static int REPORT_TYPE_ORDERS = 3;
    String reportFileName = "";
    String reportTitle = "";
    public static List<Order> orders = new ArrayList<Order>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        int reportType = getIntent().getIntExtra("report_type", 0);
        reportType = 3;
        switch (reportType) {
            case 1:
                reportFileName = "alama_student_report_" + UIUtils.getDateWithTime();
                reportTitle = "Students Report - " + UIUtils.getDateWithTime();
                break;
            case 2:
                reportFileName = "alama_transactions_report_" + UIUtils.getDateWithTime();
                reportTitle = "Transactions Report - " + UIUtils.getDateWithTime();
                break;
            case 3:
                reportFileName = "alama_orders_report_" + UIUtils.getDateWithTime();
                reportTitle = "Orders Report - " + UIUtils.getDateWithTime();
                break;
            default:
                break;
        }
        reportFileName = reportFileName.replace(" ", "_");
        reportFileName = reportFileName.replace(":", "");
        reportFileName = reportFileName.replace("-", "");

        createPDF(reportFileName, new PDFUtil.PDFUtilListener() {
            @Override
            public void pdfGenerationSuccess(File savedPDFFile) {
                Toast.makeText(OrdersReportActivity.this, "Report created", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void pdfGenerationFailure(Exception exception) {
                Toast.makeText(OrdersReportActivity.this, "Report not created", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected PDFHeaderView getHeaderView(int pageIndex) {
        PDFHeaderView headerView = new PDFHeaderView(getApplicationContext());
        PDFHorizontalView horizontalView = new PDFHorizontalView(getApplicationContext());
        PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H2);
        SpannableString word = new SpannableString(reportTitle);
        word.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        pdfTextView.setText(word);
        pdfTextView.setLayout(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        pdfTextView.getView().setGravity(Gravity.CENTER_VERTICAL);
        pdfTextView.getView().setTypeface(pdfTextView.getView().getTypeface(), Typeface.BOLD);

        horizontalView.addView(pdfTextView);

        PDFImageView imageView = new PDFImageView(getApplicationContext());
        LinearLayout.LayoutParams imageLayoutParam = new LinearLayout.LayoutParams(60, 60, 0);
        imageView.setImageScale(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(R.drawable.alama_logo);
        imageLayoutParam.setMargins(0, 0, 10, 0);
        imageView.setLayout(imageLayoutParam);

        horizontalView.addView(imageView);

        headerView.addView(horizontalView);

        PDFLineSeparatorView lineSeparatorView1 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        headerView.addView(lineSeparatorView1);

        return headerView;
    }

    @Override
    protected PDFBody getBodyViews() {
        PDFBody pdfBody = new PDFBody();

        PDFTextView pdfCompanyNameView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H3);
        pdfCompanyNameView.setText("Alama International");
        pdfBody.addView(pdfCompanyNameView);
        PDFLineSeparatorView lineSeparatorView1 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        pdfBody.addView(lineSeparatorView1);
        PDFTextView pdfAddressView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        pdfAddressView.setText("A5-2-428/429, 3rd Floor, Hyderbasti,\n" + "R.P.Road, Secunderabad - 500 003\n" + "Telangana, INDIA");
        pdfBody.addView(pdfAddressView);

        PDFLineSeparatorView lineSeparatorView2 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        lineSeparatorView2.setLayout(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 8, 0));
        pdfBody.addView(lineSeparatorView2);

        PDFLineSeparatorView lineSeparatorView3 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        pdfBody.addView(lineSeparatorView3);

        int[] widthPercent = {20, 15, 15, 15, 15, 20}; // Sum should be equal to 100%
        ArrayList<String> textInTable = new ArrayList<>();
        textInTable.add("State");
        textInTable.add("Franchise");
        textInTable.add("Student");
        textInTable.add("Date");
        textInTable.add("Level");
        textInTable.add("Items");

        PDFTextView pdfTableTitleView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        pdfTableTitleView.setText("Official Report");
        pdfBody.addView(pdfTableTitleView);

        final PDFPageBreakView pdfPageBreakView = new PDFPageBreakView(getApplicationContext());
        pdfBody.addView(pdfPageBreakView);

        PDFTableView.PDFTableRowView tableHeader = new PDFTableView.PDFTableRowView(getApplicationContext());
        tableHeader.setBackgroundColor(R.color.dark_gray);

        for (int i = 0; i < textInTable.size(); i++) {
            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
            pdfTextView.setText(textInTable.get(i));
            pdfTextView.setTextColor(Color.WHITE);
            tableHeader.addToRow(pdfTextView);
        }

        PDFTableView.PDFTableRowView tableRowView1 = new PDFTableView.PDFTableRowView(getApplicationContext());
        for (int i = 0; i < textInTable.size(); i++) {
            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
            String value = "";
            if (i == 0) {
                value = orders.get(0).getState();
            } else if (i == 1) {
                value = orders.get(0).getFranchiseName();
            } else if (i == 2) {
                value = orders.get(0).getStudentName();
            } else if (i == 3) {
                value = orders.get(0).getDate();
            } else if (i == 4) {
                value = orders.get(0).getOrderLevel().getName();
            } else if (i == 5) {
                value = orders.get(0).getBooks() == null ? "Nil" : orders.get(0).getBooks().toString();
            }
            if (value == null) {
                value = "";
            }
            pdfTextView.setText(value);
            //tableRowView1.addToRow(pdfTextView);
        }

        PDFTableView tableView = new PDFTableView(getApplicationContext(), tableHeader, tableRowView1);

        for (int i = 0; i < orders.size(); i++) {
            PDFTableView.PDFTableRowView tableRowView = new PDFTableView.PDFTableRowView(getApplicationContext());
            for (int j = 0; j < textInTable.size(); j++) {
                PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
                String value = "";
                if (j == 0) {
                    value = orders.get(i).getDate();
                } else if (j == 1) {
                    value = orders.get(i).getFranchiseName();
                } else if (j == 2) {
                    value = orders.get(i).getStudentName();
                } else if (j == 3) {
                    value = orders.get(i).getDate();
                } else if (j == 4) {
                    value = orders.get(i).getOrderLevel().getName();
                } else if (j == 5) {
                    value = orders.get(i).getBooks() == null ? "Nil" : orders.get(0).getBooks().toString();
                }
                if (value == null) {
                    value = "";
                }
                pdfTextView.setText(value);
                tableRowView.addToRow(pdfTextView);
            }
            tableView.addRow(tableRowView);
        }
        tableView.setColumnWidth(widthPercent);
        pdfBody.addView(tableView);


        PDFLineSeparatorView lineSeparatorView4 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.BLACK);
        pdfBody.addView(lineSeparatorView4);

        PDFTextView pdfIconLicenseView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H3);
        Spanned icon8Link = HtmlCompat.fromHtml("Contact us <a href='https://www.alamainternational.com/index.html'>Alama International</a>", HtmlCompat.FROM_HTML_MODE_LEGACY);
        pdfIconLicenseView.getView().setText(icon8Link);
        pdfBody.addView(pdfIconLicenseView);

        return pdfBody;
    }

    @Override
    protected PDFFooterView getFooterView(int pageIndex) {
        PDFFooterView footerView = new PDFFooterView(getApplicationContext());

        PDFTextView pdfTextViewPage = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        pdfTextViewPage.setText(String.format(Locale.getDefault(), "Page: %d", pageIndex + 1));
        pdfTextViewPage.setLayout(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 0));
        pdfTextViewPage.getView().setGravity(Gravity.CENTER_HORIZONTAL);

        footerView.addView(pdfTextViewPage);

        return footerView;
    }

    @Nullable
    @Override
    protected PDFImageView getWatermarkView(int forPage) {
        PDFImageView pdfImageView = new PDFImageView(getApplicationContext());
        FrameLayout.LayoutParams childLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                200, Gravity.CENTER);
        pdfImageView.setLayout(childLayoutParams);

        pdfImageView.setImageResource(R.drawable.alama_logo);
        pdfImageView.setImageScale(ImageView.ScaleType.FIT_CENTER);
        pdfImageView.getView().setAlpha(0.1F);

        return pdfImageView;
    }

    @Override
    protected void onNextClicked(final File savedPDFFile) {
        /*Uri pdfUri = Uri.fromFile(savedPDFFile);

        Intent intentPdfViewer = new Intent(PdfCreatorExampleActivity.this, PdfViewerExampleActivity.class);
        intentPdfViewer.putExtra(PdfViewerExampleActivity.PDF_FILE_URI, pdfUri);

        startActivity(intentPdfViewer);*/
    }
}