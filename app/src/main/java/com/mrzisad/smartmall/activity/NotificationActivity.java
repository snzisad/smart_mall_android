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
import com.mrzisad.smartmall.adapter.NotificationAdapter;
import com.mrzisad.smartmall.model.Order;
import com.mrzisad.smartmall.utils.APILink;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    public NotificationAdapter adapter;
    public List<Order> orderList;
    public ProgressDialog progressDialog;
    ListView listView;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_notification);
        super.setTitle("Notifications");

        listView = (ListView) findViewById(R.id.lvNotificationList);

        this.progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);

        this.orderList = new ArrayList();
        this.adapter = new NotificationAdapter(NotificationActivity.this, orderList);
        listView.setAdapter(adapter);
        getDataFromServer();
    }

    private final void getDataFromServer() {
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(0, APILink.NotificationAPI + APILink.UID, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.cancel();

                try {
                    if (response.getInt(NotificationCompat.CATEGORY_STATUS) == 1) {
                        JSONArray data = response.getJSONArray("response");
                        int length = data.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject chield = data.getJSONObject(i);
                            orderList.clear();
                            Order order = new Gson().fromJson(chield.toString(), Order.class);
                            orderList.add(order);
                        }
                        adapter.notifyDataSetChanged();
                        return;
                    }
                    Toast.makeText(NotificationActivity.this, "Sorry, please try again", 1).show();
                } catch (Exception e) {
                    Toast.makeText(NotificationActivity.this, e.getLocalizedMessage(), 1).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(NotificationActivity.this, error.getLocalizedMessage(), 1).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}

