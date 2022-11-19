package com.balaabirami.abacusandroid.utils;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.model.Order;
import com.balaabirami.abacusandroid.model.OrderList;
import com.balaabirami.abacusandroid.model.ProductItem;
import com.balaabirami.abacusandroid.model.StockAdjustment;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class StockAdjustmentReportActivity extends PDFCreatorActivity {

    public static List<StockAdjustment> stockAdjustments;
    String reportFileName = "";
    String reportTitle = "";
    private File reportFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        reportFileName = "alama_stock_adjust_report_" + UIUtils.getDateWithTime();
        reportFileName = reportFileName.replace(" ", "_");
        reportFileName = reportFileName.replace(":", "");
        reportFileName = reportFileName.replace("-", "");

        createPDF(reportFileName, new PDFUtil.PDFUtilListener() {
            @Override
            public void pdfGenerationSuccess(File savedPDFFile) {
                reportFile = savedPDFFile;
                Toast.makeText(StockAdjustmentReportActivity.this, "Report created", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void pdfGenerationFailure(Exception exception) {
                Toast.makeText(StockAdjustmentReportActivity.this, "Report not created", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_share) {
            shareReport();
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareReport() {
        if (reportFile != null) {
            UIUtils.shareFile(StockAdjustmentReportActivity.this, reportFile);
        } else {
            UIUtils.showToast(StockAdjustmentReportActivity.this, "Invalid file!");
        }
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


        HashMap<String, Integer> itemsCountMap = new HashMap<>();
        StringBuilder text = new StringBuilder();
        text.append("\n").append("Items adjusted").append("\n");
        for (StockAdjustment stockAdjustment : stockAdjustments) {
            if (stockAdjustment.getItems() != null) {
                for (StockAdjustment.ItemDetail itemDetail : stockAdjustment.getItems()) {
                    if (itemDetail.getName() != null && itemDetail.getName().getName() != null) {
                        String itemName = itemDetail.getName().getName();
                        if (!itemsCountMap.containsKey(itemName)) {
                            itemsCountMap.put(itemName, itemDetail.getQty());
                        } else {
                            if (itemsCountMap.get(itemName) != null) {
                                itemsCountMap.put(itemName, itemsCountMap.get(itemName) + itemDetail.getQty());
                            }
                        }
                    }
                }
            }
        }
        List<String> sortedItems = new ArrayList<>(itemsCountMap.keySet());
        Collections.sort(sortedItems);
        List<ProductItem> productItems = ItemHelper.getInstance().getItems(getApplicationContext());

        for (ProductItem item : productItems) {
            if (itemsCountMap.get(item.getName()) != null && itemsCountMap.get(item.getName()) != 0) {
                text.append(item.getName()).append(" --> ").append(itemsCountMap.get(item.getName()) == null ? 0 : itemsCountMap.get(item.getName())).append("\n");
            }
        }
        pdfAddressView.setText(text.toString());


        int[] widthPercent = {13, 10, 10, 10, 15, 10, 12, 20}; // Sum should be equal to 100%
        ArrayList<String> textInTable = new ArrayList<>();
        textInTable.add("Ordered date");
        textInTable.add("Franchise");
        textInTable.add("Student");
        textInTable.add("Order type");
        textInTable.add("Requested by");
        textInTable.add("Amount");
        textInTable.add("Payment date");
        textInTable.add("items");

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
                value = stockAdjustments.get(0).getDate();
            } else if (i == 1) {
                value = Objects.requireNonNull(stockAdjustments.get(0).getFranchise()).getName();
            } else if (i == 2) {
                value = Objects.requireNonNull(stockAdjustments.get(0).getStudent()).getName();
            } else if (i == 3) {
                value = Objects.requireNonNull(stockAdjustments.get(0).getOrderType()).toString();
            } else if (i == 4) {
                value = stockAdjustments.get(0).getOrderType() == StockAdjustment.AdjustType.REISSUE ? stockAdjustments.get(0).getRequestedBy() : "Nil";
            } else if (i == 5) {
                if (stockAdjustments.get(0).getPaymentType() == StockAdjustment.PaymentType.CASH) {
                    value = String.valueOf(stockAdjustments.get(0).getAmount());
                } else if (stockAdjustments.get(0).getPaymentType() == StockAdjustment.PaymentType.FREE) {
                    value = "Nil";
                }
            } else if (i == 6) {
                if (stockAdjustments.get(0).getPaymentType() == StockAdjustment.PaymentType.CASH) {
                    value = String.valueOf(stockAdjustments.get(0).getAmountReceivedDate());
                } else if (stockAdjustments.get(0).getPaymentType() == StockAdjustment.PaymentType.FREE) {
                    value = "Nil";
                }
            } else if (i == 7) {
                StringBuilder itemsList = new StringBuilder();
                for (StockAdjustment.ItemDetail item : Objects.requireNonNull(stockAdjustments.get(0).getItems())) {
                    itemsList.append(item.toDisplay()).append("\n");
                }
                if (itemsList.toString().endsWith("\n")) {
                    itemsList = new StringBuilder(itemsList.substring(0, itemsList.length() - 1));
                }
                value = itemsList.toString();
            }
            if (value == null) {
                value = "";
            }
            pdfTextView.setText(value);
        }

        PDFTableView tableView = new PDFTableView(getApplicationContext(), tableHeader, tableRowView1);

        for (int i = 0; i < stockAdjustments.size(); i++) {
            PDFTableView.PDFTableRowView tableRowView = new PDFTableView.PDFTableRowView(getApplicationContext());
            for (int j = 0; j < textInTable.size(); j++) {
                PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
                String value = "";
                if (j == 0) {
                    value = stockAdjustments.get(i).getDate();
                } else if (j == 1) {
                    value = Objects.requireNonNull(stockAdjustments.get(i).getFranchise()).getName();
                } else if (j == 2) {
                    value = Objects.requireNonNull(stockAdjustments.get(0).getStudent()).getName();
                } else if (j == 3) {
                    value = Objects.requireNonNull(stockAdjustments.get(i).getOrderType()).toString();
                } else if (j == 4) {
                    value = stockAdjustments.get(i).getOrderType() == StockAdjustment.AdjustType.REISSUE ? stockAdjustments.get(i).getRequestedBy() : "Nil";
                } else if (j == 5) {
                    if (stockAdjustments.get(0).getPaymentType() == StockAdjustment.PaymentType.CASH) {
                        value = String.valueOf(stockAdjustments.get(i).getAmount());
                    } else if (stockAdjustments.get(i).getPaymentType() == StockAdjustment.PaymentType.FREE) {
                        value = "Nil";
                    }
                } else if (j == 6) {
                    if (stockAdjustments.get(0).getPaymentType() == StockAdjustment.PaymentType.CASH) {
                        value = String.valueOf(stockAdjustments.get(i).getAmountReceivedDate());
                    } else if (stockAdjustments.get(i).getPaymentType() == StockAdjustment.PaymentType.FREE) {
                        value = "Nil";
                    }
                } else if (j == 7) {
                    StringBuilder itemsList = new StringBuilder();
                    for (StockAdjustment.ItemDetail item : Objects.requireNonNull(stockAdjustments.get(i).getItems())) {
                        itemsList.append(item.toDisplay()).append("\n");
                    }
                    if (itemsList.toString().endsWith("\n")) {
                        itemsList = new StringBuilder(itemsList.substring(0, itemsList.length() - 1));
                    }
                    value = itemsList.toString();
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