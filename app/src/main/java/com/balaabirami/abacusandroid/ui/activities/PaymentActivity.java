package com.balaabirami.abacusandroid.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.model.Order;
import com.balaabirami.abacusandroid.model.Session;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.room.AbacusDatabase;
import com.balaabirami.abacusandroid.room.OrderDao;
import com.balaabirami.abacusandroid.room.OrderLog;
import com.balaabirami.abacusandroid.room.PendingOrder;
import com.balaabirami.abacusandroid.room.PendingOrderDao;
import com.balaabirami.abacusandroid.utils.UIUtils;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Objects;

public class PaymentActivity extends Activity implements PaymentResultListener {
    private static final String TAG = PaymentActivity.class.getSimpleName();
    String amount, orderId;
    TextView tvPayment;
    boolean paymentInProgress = false;
    OrderDao orderDao;

    PendingOrderDao pendingOrderDao;
    private Order order;
    private Student student;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment);
        orderDao = Objects.requireNonNull(AbacusDatabase.Companion.getAbacusDatabase(getApplicationContext())).orderDao();
        pendingOrderDao = Objects.requireNonNull(AbacusDatabase.Companion.getAbacusDatabase(getApplicationContext())).pendingOrderDao();
        amount = getIntent().getStringExtra("amount");
        orderId = getIntent().getStringExtra("orderId");
        order = getIntent().getExtras().getParcelable("order");
        student = (Student) getIntent().getExtras().getParcelable("student");
        tvPayment = findViewById(R.id.tv_amount);
        tvPayment.setText("Rs. " + amount);
        amount = amount + "00";
        new Thread(() -> {
            orderDao.insert(new OrderLog(orderId, "Opened payment activity"));
        }).start();
        /*
         To ensure faster loading of the Checkout form,
          call this method as early as possible in your checkout flow.
         */
        Checkout.preload(getApplicationContext());

        // Payment button created by you in XML layout
        Button button = findViewById(R.id.btn_pay);

        button.setOnClickListener(v -> startPayment());

        TextView privacyPolicy = findViewById(R.id.txt_privacy_policy);

        privacyPolicy.setOnClickListener(v -> {
            Intent httpIntent = new Intent(Intent.ACTION_VIEW);
            httpIntent.setData(Uri.parse("https://razorpay.com/sample-application/"));
            startActivity(httpIntent);
        });
    }

    public void startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Alama International");
            options.put("description", "Order ID - " + orderId);
            options.put("send_sms_hash", true);
            options.put("allow_rotation", true);
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", amount);
            JSONObject preFill = new JSONObject();
            preFill.put("email", "payment@razorpay.com");
            preFill.put("contact", "9876543210");

            options.put("prefill", preFill);
            if (Student.isValidForEnroll(student)) {
                co.open(activity, options);
            } else {
                UIUtils.showToast(this, "Student data corrupted! Please reopen the app");
            }
            paymentInProgress = true;
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
            paymentInProgress = false;
        }
    }

    /**
     * The name of the function has to be
     * onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            new Thread(() -> {
                orderDao.insert(new OrderLog(orderId, "Payment Success"));
            }).start();
            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
            new Thread(() -> {
                orderDao.insert(new OrderLog(orderId, "Payment Success toast shown"));
            }).start();
            Intent data = new Intent();
            data.putExtra("student", student);
            setResult(Activity.RESULT_OK, data);
            finish();
            new Thread(() -> {
                pendingOrderDao.insert(new PendingOrder(order.getOrderId(), order.getStudentId(), order));
                orderDao.insert(new OrderLog(orderId, "Payment Activity closed"));
            }).start();
        } catch (Exception e) {
            new Thread(() -> {
                orderDao.insert(new OrderLog(orderId, "Payment Failed"));
            }).start();
            Toast.makeText(this, "Payment Failed: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
            new Thread(() -> {
                orderDao.insert(new OrderLog(orderId, "Payment Failure toast shown"));
            }).start();
            setResult(Activity.RESULT_CANCELED);
            finish();
            new Thread(() -> {
                orderDao.insert(new OrderLog(orderId, "Payment Activity closed"));
            }).start();
        }
        paymentInProgress = false;
    }

    /**
     * The name of the function has to be
     * onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
        paymentInProgress = false;
    }

    @Override
    public void onBackPressed() {
        new Thread(() -> {
            Objects.requireNonNull(AbacusDatabase.Companion.getAbacusDatabase(this)).orderDao().
                    insert(new OrderLog("PaymentActivity", "onBackPressed PaymentActivity"));
        }).start();
        if (!UIUtils.API_IN_PROGRESS) {
            super.onBackPressed();
        }
        UIUtils.showToast(this, "Please don't close the screen if the payment in progress. " +
                "You can close the app if you want. ");
    }

    @Override
    protected void onStop() {
        new Thread(() -> {
            Objects.requireNonNull(AbacusDatabase.Companion.getAbacusDatabase(this)).orderDao().
                    insert(new OrderLog("PaymentActivity", "onStop PaymentActivity"));
        }).start();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        new Thread(() -> {
            Objects.requireNonNull(AbacusDatabase.Companion.getAbacusDatabase(this)).orderDao().
                    insert(new OrderLog("PaymentActivity", "onDestroy PaymentActivity"));
        }).start();
        super.onDestroy();
    }
}