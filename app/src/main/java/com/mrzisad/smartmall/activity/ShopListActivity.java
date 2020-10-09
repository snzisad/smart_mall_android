package com.mrzisad.smartmall.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.mrzisad.smartmall.R;
import com.mrzisad.smartmall.utils.APILink;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShopListActivity extends AppCompatActivity {
    public ArrayAdapter<String> adapter;
    public ProgressDialog progressDialog;
    public List<String> shopID;
    public List<String> shopName;
    ListView listView;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_shop_list);
        setTitle("Shop List");

        this.progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);

        this.shopName = new ArrayList();
        this.shopID = new ArrayList();

        listView = (ListView) findViewById(R.id.lvShopList);

        this.adapter = new ArrayAdapter<>(ShopListActivity.this, 17367043, shopName);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                APILink.SID = shopID.get(i);
                startActivity(new Intent(ShopListActivity.this, ProductListActivity.class));
            }
        });

        getShopListFromServer();
    }

    private final void getShopListFromServer() {
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(0, APILink.ShopListAPI + APILink.MID, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.cancel();

                try {
                    if (response.getInt(NotificationCompat.CATEGORY_STATUS) == 1) {
                        JSONArray data = response.getJSONArray("response");
                        int length = data.length();
                        shopName.clear();
                        shopID.clear();
                        for (int i = 0; i < length; i++) {
                            JSONObject chield = data.getJSONObject(i);
                            shopName.add(chield.get("shopname").toString());
                            shopID.add(chield.get("phone").toString());
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ShopListActivity.this, "Sorry, please try again", 1).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ShopListActivity.this, e.getLocalizedMessage(), 1).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(ShopListActivity.this, error.getLocalizedMessage(), 1).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}

