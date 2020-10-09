package com.mrzisad.smartmall.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
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

public class CategoryAddActivity extends AppCompatActivity {
    public ArrayAdapter<String> adapter;
    public List<String> categoryID;
    public List<String> categoryName;
    public ProgressDialog progressDialog;
    EditText edtCategoryName;
    ListView listView;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_category_add);
        setTitle("Category List");

        edtCategoryName = findViewById(R.id.edtCategoryName);
        listView = (ListView) findViewById(R.id.lvCategoryList);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);

        this.categoryName = new ArrayList();
        this.categoryID = new ArrayList();

        adapter = new ArrayAdapter<>(this, 17367043, categoryName);
        listView.setAdapter(adapter);
        ((Button) findViewById(R.id.btnSave)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(edtCategoryName.getText())) {
                    sendDataToServer(edtCategoryName.getText().toString());
                    return;
                }
                edtCategoryName.setError("Required");
            }
        });

        getCategoryListFromServer();
    }

    private final void getCategoryListFromServer() {
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, APILink.CategoryListAPI, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.cancel();

                try {
                    if (response.getInt(NotificationCompat.CATEGORY_STATUS) == 1) {
                        JSONArray data = response.getJSONArray("response");

                        categoryName.clear();
                        categoryID.clear();

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject chield = data.getJSONObject(i);
                            categoryName.add(chield.get("name").toString());
                            categoryID.add(chield.get("id").toString());
                        }
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(CategoryAddActivity.this, "Sorry, please try again", 1).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(CategoryAddActivity.this, e.getLocalizedMessage(), 1).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(CategoryAddActivity.this, error.getLocalizedMessage(), 1).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    /* access modifiers changed from: private */
    public final void sendDataToServer(final String name) {
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(APILink.CategoryAddAPI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                try {
                    JSONObject message = new JSONObject(response);
                    if (message.getInt(NotificationCompat.CATEGORY_STATUS) == 1) {
                        Toast.makeText(CategoryAddActivity.this, "Category added successfully", 1).show();
                        JSONObject response2 = message.getJSONObject("response");
                        categoryName.add(response2.get("name").toString());
                        categoryID.add(response2.get("id").toString());
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(CategoryAddActivity.this, "Sorry, Please try again", 1).show();
                    }
                } catch (Exception e) {
                    progressDialog.cancel();
                    Toast.makeText(CategoryAddActivity.this, "Sorry, Please try again", 1).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(CategoryAddActivity.this, "Category already exists", 1).show();
            }
        }){
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
