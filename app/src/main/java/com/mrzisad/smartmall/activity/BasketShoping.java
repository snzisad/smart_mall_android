package com.mrzisad.smartmall.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.google.gson.JsonObject;
import com.mrzisad.smartmall.R;
import com.mrzisad.smartmall.model.Product;
import com.mrzisad.smartmall.model.ProductPayment;
import com.mrzisad.smartmall.utils.APILink;
import com.mrzisad.smartmall.utils.DataContainer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasketShoping extends AppCompatActivity {
    private int position;
    public List<Product> products;
    LinearLayout layoutProductList;
    Button btnCloseShoping, btnBuyAll;
    TextView tvTotalPrice;
    ProgressBar pbBuy;
    boolean isOrdered = false;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_basket_shoping);
        super.setTitle("Basket Shopping");

        products = DataContainer.products;
        btnCloseShoping = findViewById(R.id.btnCloseShoping);
        btnBuyAll = findViewById(R.id.btnBuyAll);
        layoutProductList = findViewById(R.id.layoutProductList);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        btnCloseShoping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BasketShoping.this, CustomerActivity.class));
                finish();
            }
        });

        btnBuyAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPaymentDialog();
            }
        });

        setView();
    }

    private final void setView() {
        layoutProductList.removeAllViews();
        int price = 0;

        for (int i = 0; i < products.size(); i++) {
            View v = getLayoutInflater().inflate(R.layout.layout_shoping_list, (ViewGroup) null);
            TextView tvProductName = (TextView) v.findViewById(R.id.tvProductName);
            TextView tvShopName = (TextView) v.findViewById(R.id.tvShopName);
            TextView tvPrice = (TextView) v.findViewById(R.id.tvPrice);
            Button btnBuy = (Button) v.findViewById(R.id.btnBuy);
            ImageView btnDel = (ImageView) v.findViewById(R.id.btnDel);
            List<? extends Product> list2 = this.products;

            tvProductName.setText(products.get(i).getName());
            Product.Shop shop = (products.get(i)).shop;
            tvShopName.setText(shop.getShopname());
            tvPrice.setText((products.get(i)).getPrice());
            String price2 = (products.get(i)).getPrice();
            price += Integer.parseInt(price2);

            final int finalI = i;
            btnBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    position = finalI;
                    showDialog();
                }
            });
            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeItem(finalI);
                }
            });
            layoutProductList.addView(v);
        }
        tvTotalPrice.setText(" " + price + " taka");
    }

    public final void removeItem(int position2) {
        ArrayList productUpdated = new ArrayList();
        for (int i = 0; i < products.size(); i++) {
            if (i != position2) {
                productUpdated.add(products.get(i));
            }
        }
        DataContainer.products = productUpdated;
        startActivity(new Intent(this, BasketShoping.class));
        finish();
    }

    public final void showPaymentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.layout_payment_method, (ViewGroup) null, false);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        pbBuy = view.findViewById(R.id.pbBuy);
        final Button btnBuy = (Button) view.findViewById(R.id.btnBuy);
        final Spinner spinnerType = (Spinner) view.findViewById(R.id.spinnerType);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinnerType.getSelectedItemPosition() == 2) {
                    btnBuy.setText("Order");
                    return;
                }
                btnBuy.setText("Continue");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (spinnerType.getSelectedItemPosition() == 2) {
                    String obj3 = spinnerType.getSelectedItem().toString();
                    for (int i = 0; i < products.size(); i++) {
                        sendDataToServer(pbBuy, btnBuy, products.get(i).getId(), "1", "", obj3, alertDialog);
                    }
                    return;
                }

                ProductPayment.type = spinnerType.getSelectedItemPosition();
                ProductPayment.product_type = 1;
                ProductPayment.size = "";
                ProductPayment.quantity = 1;
                startActivity(new Intent(BasketShoping.this, PaymentActivity.class));
                alertDialog.cancel();
                return;
            }

        });
    }

    public final void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.layout_buy_product, (ViewGroup) null, false);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final Button btnBuy = (Button) view.findViewById(R.id.btnBuy);
        final Spinner spinnerType = (Spinner) view.findViewById(R.id.spinnerType);
        final EditText edtQuantity = view.findViewById(R.id.edtQuantity);
        final EditText edtSize = view.findViewById(R.id.edtSize);
        pbBuy = view.findViewById(R.id.pbBuy);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinnerType.getSelectedItemPosition() == 2) {
                    btnBuy.setText("Order");
                    return;
                }
                btnBuy.setText("Continue");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(edtQuantity.getText().toString())) {
                    if (products.get(position).getQuantity().intValue() <= 0) {
                        Toast.makeText(BasketShoping.this, "Sorry, out of stock", 0).show();
                        return;
                    }
                    int intValue = products.get(position).getQuantity().intValue();
                    if (intValue < Integer.parseInt(edtQuantity.getText().toString())) {
                        Toast.makeText(BasketShoping.this, "Sorry, only " + intValue + " items available", 0).show();
                        return;
                    }

                    if (spinnerType.getSelectedItemPosition() == 2) {
                        String id = products.get(position).getId();
                        String obj = edtQuantity.getText().toString();
                        String obj2 = edtSize.getText().toString();
                        String obj3 = spinnerType.getSelectedItem().toString();
                        sendDataToServer(pbBuy, btnBuy, id, obj, obj2, obj3, alertDialog);
                        return;
                    }

                    if (spinnerType.getSelectedItemPosition() == 0) {
                        Product.Shop shop = products.get(position).shop;
                        if (TextUtils.isEmpty(shop.getBkash())) {
                            Toast.makeText(BasketShoping.this, "Sorry, you cann't buy this product using bKash", 0).show();
                            return;
                        }
                    }

                    if (spinnerType.getSelectedItemPosition() == 1) {
                        Product.Shop shop = products.get(position).shop;
                        if (TextUtils.isEmpty(shop.getRocket())) {
                            Toast.makeText(BasketShoping.this, "Sorry, you cann't buy this product using Rocket", 0).show();
                            return;
                        }
                    }

                    ProductPayment.type = spinnerType.getSelectedItemPosition();
                    ProductPayment.product = products.get(position);
                    ProductPayment.quantity = Integer.parseInt(edtQuantity.getText().toString());
                    ProductPayment.size = edtSize.getText().toString();
                    ProductPayment.position = position;
                    startActivity(new Intent(BasketShoping.this, PaymentActivity.class));
                    alertDialog.cancel();
                    return;
                }
                edtQuantity.setError("Required");
            }
        });
    }

    /* access modifiers changed from: private */
    public final void sendDataToServer(final ProgressBar progressBar, final Button button, final String pid, final String quantity, final String size, final String payment_type, final AlertDialog alertDialog) {
        ProgressBar progressBar2 = progressBar;
        Button button2 = button;
        progressBar2.setVisibility(0);
        button2.setVisibility(8);
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, APILink.ProductOrderAPI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.get(NotificationCompat.CATEGORY_STATUS).toString().equals("1")) {
                        if(!isOrdered) {
                            Toast.makeText(BasketShoping.this, "Product ordered Successfully", 0).show();
                            if(ProductPayment.product_type == 1){
                                DataContainer.products = null;
                                startActivity(new Intent(BasketShoping.this, CustomerActivity.class));
                            }
                        }
                        isOrdered = true;
                        alertDialog.cancel();
                    }
                    else {
                        Toast.makeText(BasketShoping.this, "Please try again", 0).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(BasketShoping.this, "" + e.getLocalizedMessage(), 0).show();
                    e.printStackTrace();
                }
                progressBar.setVisibility(8);
                button.setVisibility(0);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(8);
                button.setVisibility(0);
                Toast.makeText(BasketShoping.this, "Error: "+error.getLocalizedMessage(), 0).show();
            }
        }){
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap param = new HashMap();
                param.put("user_id", APILink.UID);
                param.put("product_id", pid);
                param.put("quantity", quantity);
                param.put("size", size);
                param.put("payment_type", payment_type);
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
