package com.balaabirami.abacusandroid.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.balaabirami.abacusandroid.R;
import com.balaabirami.abacusandroid.model.Order;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;
import org.w3c.dom.Text;

public class PaymentActivity extends Activity implements PaymentResultListener {
    private static final String TAG = PaymentActivity.class.getSimpleName();
    String amount;
    TextView tvPayment;
    boolean paymentInProgress = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment);
        // amount = getIntent().getStringExtra("amount");
        amount = "1";
        tvPayment = findViewById(R.id.tv_amount);
        tvPayment.setText("Rs. " + amount);
        amount = amount + "00";
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
            options.put("description", "This is a transaction screen to purchase a package or item from Alama International");
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

            co.open(activity, options);
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
        paymentInProgress = false;
        try {
            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Payment Failed: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
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
        if (paymentInProgress) {
            Toast.makeText(this, "Please wait for the Payment to complete", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }
}