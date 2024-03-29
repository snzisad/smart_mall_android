package com.mrzisad.smartmall.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mrzisad.smartmall.R;
import com.mrzisad.smartmall.utils.APILink;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AddShopingMallActivity extends AppCompatActivity {
    public ArrayAdapter<String> adapter;
    public ProgressDialog progressDialog;
    public List<String> shopingmallID;
    public List<String> shopingmallName;


    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_add_shoping_mall);
        setTitle("Shoping Mall List");

        this.progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);

        shopingmallName = new ArrayList();
        shopingmallID = new ArrayList();
        Context context = this;


        adapter = new ArrayAdapter<>(context, 17367043, shopingmallName);
        ListView listView = (ListView) findViewById(R.id.lvMallList);
        listView.setAdapter(adapter);

        ((Button) findViewById(R.id.btnSave)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText) findViewById(R.id.edtMallName);
                if (!TextUtils.isEmpty(editText.getText())) {
                    sendDataToServer(editText.getText().toString());
                    return;
                }
                editText.setError("Required");
            }
        });

        getCategoryListFromServer();
    }

    private final void getCategoryListFromServer() {
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, APILink.ShopingMallListAPI, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.cancel();
                try {
                    if (response.get(NotificationCompat.CATEGORY_STATUS).equals((Object) 1)) {
                        JSONArray data = response.getJSONArray("response");
                        shopingmallName.clear();
                        shopingmallID.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject chield = data.getJSONObject(i);
                            shopingmallName.add(chield.get("name").toString());
                            shopingmallID.add(chield.get("id").toString());
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AddShopingMallActivity.this, "Sorry, please try again", 1).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(AddShopingMallActivity.this, e.getLocalizedMessage(), 1).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(AddShopingMallActivity.this, error.getLocalizedMessage(), 1).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    /* access modifiers changed from: private */
    public final void sendDataToServer(final String name) {
        progressDialog.show();
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, APILink.ShopingMallAddAPI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                try {
                    JSONObject message = new JSONObject(response);
                    if (message.get(NotificationCompat.CATEGORY_STATUS).equals((Object) 1)) {
                        Toast.makeText(AddShopingMallActivity.this, "Shoping Mall added successfully", 1).show();
                        JSONObject response2 = message.getJSONObject("response");
                        shopingmallName.add(response2.get("name").toString());
                        shopingmallID.add(response2.get("id").toString());
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(AddShopingMallActivity.this, "Sorry, Please try again", 1).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(AddShopingMallActivity.this, "Sorry, Please try again", 1).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(AddShopingMallActivity.this, "Shoping Mall already exists", 1).show();
            }
        }) {

            public Map<String, String> getParams() throws AuthFailureError {
                HashMap param = new HashMap();
                param.put("name", name);
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