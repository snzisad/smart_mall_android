package com.mrzisad.smartmall.activity;

import android.app.ProgressDialog;
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
import com.mrzisad.smartmall.adapter.ShopkeeperRequestAdapter;
import com.mrzisad.smartmall.model.User;
import com.mrzisad.smartmall.utils.APILink;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShopkeeperRequestActivity extends AppCompatActivity {
    public ShopkeeperRequestAdapter adapter;
    public ProgressDialog progressDialog;
    public List<User> userList;
    ListView listView;


    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_shopkeeper_request);
        super.setTitle("Shopkeeper Request");

        this.progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);
        this.userList = new ArrayList();

        listView = (ListView) findViewById(R.id.lvUserLis);

        this.adapter = new ShopkeeperRequestAdapter(ShopkeeperRequestActivity.this, userList);
        listView.setAdapter(adapter);
        getDataFromServer();
    }

    private final void getDataFromServer() {
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(0, APILink.ShopkeeperRequestAPI, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.cancel();

                try {
                    if (response.getInt(NotificationCompat.CATEGORY_STATUS) == 1) {
                        JSONArray data = response.getJSONArray("response");
                        int length = data.length();
                        userList.clear();
                        for (int i = 0; i < length; i++) {
                            JSONObject chield = data.getJSONObject(i);
                            User fromJson = new Gson().fromJson(chield.toString(), User.class);
                            userList.add(fromJson);
                        }
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    Toast.makeText(ShopkeeperRequestActivity.this, "Sorry, please try again", 1).show();
                } catch (Exception e) {
                    Toast.makeText(ShopkeeperRequestActivity.this, e.getLocalizedMessage(), 1).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(ShopkeeperRequestActivity.this, error.getLocalizedMessage(), 1).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}

