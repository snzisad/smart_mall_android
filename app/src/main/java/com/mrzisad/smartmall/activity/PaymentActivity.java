package com.mrzisad.smartmall.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mrzisad.smartmall.R;
import com.mrzisad.smartmall.model.Product;
import com.mrzisad.smartmall.model.ProductPayment;
import com.mrzisad.smartmall.utils.APILink;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {
    private String account;
    private final Integer[] procedure = {Integer.valueOf(R.drawable.bkash_payment), Integer.valueOf(R.drawable.rocket_payment)};
    private ProductPayment productPayment;
    public ProgressDialog progressDialog;
    public String type;
    ImageView imgProcedure;
    TextView textPaymentInfo;
    Button btnOrder;
    EditText edtTrnx;


    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_payment);
        super.setTitle("Payment");

        progressDialog = new ProgressDialog(PaymentActivity.this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);

        imgProcedure = findViewById(R.id.imgProcedure);
        textPaymentInfo = findViewById(R.id.textPaymentInfo);
        btnOrder = findViewById(R.id.btnOrder);
        edtTrnx = findViewById(R.id.edtTrnx);

        if (ProductPayment.type == 0) {
            Product.Shop shop = ProductPayment.product.shop;
            String bkash = shop.getBkash();
            this.account = bkash;
            this.type = "bKash";
        } else {
            Product.Shop shop2 = ProductPayment.product.shop;
            String rocket = shop2.getRocket();
            this.account = rocket;
            this.type = "Rocket";
        }

        imgProcedure.setImageDrawable(getResources().getDrawable(this.procedure[ProductPayment.type].intValue()));
        Product product = ProductPayment.product;
        String price = product.getPrice();
        int amount = Integer.parseInt(price) * ProductPayment.quantity;

        StringBuilder sb = new StringBuilder();
        sb.append("Referance No: ");
        sb.append(product.getId());
        sb.append("\nAmount: ");
        sb.append(amount);
        sb.append(" taka");
        sb.append("\nPayment Account: ");
        String str = this.account;
        sb.append(str);
        textPaymentInfo.setText(sb.toString());

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtTrnx.getText())) {
                    edtTrnx.setError("Required");
                } else {
                    sendDataToServer();
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public final void sendDataToServer() {
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, APILink.ProductOrderAPI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                try {
                    if (new JSONObject(response).get(NotificationCompat.CATEGORY_STATUS).toString().equals("1")) {
                        Toast.makeText(PaymentActivity.this, "Product ordered Successfully", 0).show();
                        finish();
                    }
                    else {
                        Toast.makeText(PaymentActivity.this, "Please try again", 0).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(PaymentActivity.this, "" + e.getLocalizedMessage(), 0).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                try{
                    String message = new String(error.networkResponse.data, "UTF-8");
                    Toast.makeText(PaymentActivity.this, ""+message, 0).show();
                }
                catch (Exception e){
                    Toast.makeText(PaymentActivity.this, "Error. Please Try Again", 0).show();
                }
            }
        }){
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap param = new HashMap();
                String str = APILink.UID;
                param.put("user_id", str);
                Product product = ProductPayment.product;
                String id = product.getId();
                param.put("product_id", id);
                param.put("quantity", String.valueOf(ProductPayment.quantity));
                param.put("size", ProductPayment.size);
                param.put("payment_type", type);
                param.put("trxnid", edtTrnx.getText().toString());
                return param;
            }


            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap header = new HashMap();
                header.put("Accept", "application/json");
                return header;
            }
        };

        requestQueue.add(stringRequest);
    }
}
