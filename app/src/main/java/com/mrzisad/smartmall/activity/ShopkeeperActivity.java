package com.mrzisad.smartmall.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class ShopkeeperActivity extends AppCompatActivity {
    public ProductListAdapter adapter;
    public List<Product> productList;
    public ProgressDialog progressDialog;
    ListView listView;
    Button btnUpload, btnUpdate, btnNotification;


    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_shopkeeper);
        super.setTitle("My Products");

        this.progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);

        this.productList = new ArrayList();
        listView = findViewById(R.id.lvProductList);
        btnUpload = findViewById(R.id.btnUpload);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnNotification = findViewById(R.id.btnNotification);

        this.adapter = new ProductListAdapter(ShopkeeperActivity.this, productList, 1);
        listView.setAdapter(adapter);

        getDataFromServer();


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShopkeeperActivity.this, UploadProductActivity.class));
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShopkeeperActivity.this, UpdateProductActivity.class));
            }
        });
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShopkeeperActivity.this, NotificationActivity.class));
            }
        });
    }

    private final void getDataFromServer() {
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(0, APILink.ProductListAPI + APILink.UID, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.cancel();

                try {
                    int status = response.getInt(NotificationCompat.CATEGORY_STATUS);
                    String notification = response.getString("notification");
                    if (status == 1) {
                        btnNotification.setText("Notification(" + notification + ")");
                        JSONArray data = response.getJSONArray("response");
                        int length = data.length();
                        productList.clear();
                        for (int i = 0; i < length; i++) {
                            JSONObject chield = data.getJSONObject(i);
                            Product fromJson = new Gson().fromJson(chield.toString(), Product.class);
                            productList.add(fromJson);
                        }
                        adapter.notifyDataSetChanged();
                        return;
                    }
                    Toast.makeText(ShopkeeperActivity.this, "Sorry, please try again", 1).show();
                } catch (Exception e) {
                    Toast.makeText(ShopkeeperActivity.this, e.getLocalizedMessage(), 1).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(ShopkeeperActivity.this, error.getLocalizedMessage(), 1).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.logout) {
            return super.onOptionsItemSelected(item);
        }
        getSharedPreferences("Status", 0).edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getDataFromServer();
    }
}
