package com.mrzisad.smartmall.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.mrzisad.smartmall.R;
import com.mrzisad.smartmall.adapter.ProductListAdapter;
import com.mrzisad.smartmall.model.Product;
import com.mrzisad.smartmall.utils.APILink;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {
    public ProductListAdapter adapter;
    public List<Product> productList;
    public ProgressDialog progressDialog;

    ListView lvProductList;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_product_list);
        super.setTitle("Product List");
        this.progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);

        lvProductList = findViewById(R.id.lvProductList);

        this.productList = new ArrayList();
        this.adapter = new ProductListAdapter(ProductListActivity.this, productList, 0);
        lvProductList.setAdapter(adapter);
        getDataFromServer();
    }

    private final void getDataFromServer() {
        progressDialog.show();
        productList.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(0, APILink.ProductListAPI + APILink.SID, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.cancel();

                try {
                    if (response.getInt(NotificationCompat.CATEGORY_STATUS) == 1) {
                        JSONArray data = response.getJSONArray("response");
                        int length = data.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject chield = data.getJSONObject(i);
                            Product product = new Gson().fromJson(chield.toString(), Product.class);
                            productList.add(product);
                        }
                        adapter.notifyDataSetChanged();
                        return;
                    }
                    Toast.makeText(ProductListActivity.this, "Sorry, please try again", 1).show();
                } catch (Exception e) {
                    Toast.makeText(ProductListActivity.this, e.getLocalizedMessage(), 1).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(ProductListActivity.this, error.getLocalizedMessage(), 1).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}
