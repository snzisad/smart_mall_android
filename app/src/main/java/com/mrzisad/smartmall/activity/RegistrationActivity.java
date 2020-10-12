package com.mrzisad.smartmall.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    public ArrayAdapter<String> categoryAdapter;
    public List<String> categoryid, categoryname, shopingmallid, shopingmallname;
    private SharedPreferences.Editor editor;
    public ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    public ArrayAdapter<String> shopingMallAdapter;
    public String type;
    Spinner spinnerShopingMall, spinnerShopingCategory;
    RadioButton rbShopkeeper, rbCustomer;
    RadioGroup radioGroupUserType;
    LinearLayout linearLayout_ShopInfo;
    Button btnRegister, btnLogin;
    EditText edtName, edtAddress, edtPhone, edtEmail, edtShopName, edtShopNumber, edtBkash, edtRocket;


    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_registration);
        super.setTitle("Registration");

        this.progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        categoryid = new ArrayList<>();
        categoryname = new ArrayList<>();
        shopingmallid = new ArrayList<>();
        shopingmallname = new ArrayList<>();

        spinnerShopingMall = findViewById(R.id.spinnerShopingMall);
        spinnerShopingCategory = findViewById(R.id.spinnerShopingCategory);
        radioGroupUserType = findViewById(R.id.radioGroupUserType);
        rbShopkeeper = findViewById(R.id.rbShopkeeper);
        rbCustomer = findViewById(R.id.rbCustomer);
        linearLayout_ShopInfo = findViewById(R.id.linearLayout_ShopInfo);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        edtName = findViewById(R.id.edtName);
        edtAddress = findViewById(R.id.edtAddress);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtShopName = findViewById(R.id.edtShopName);
        edtShopNumber = findViewById(R.id.edtShopNumber);
        edtBkash = findViewById(R.id.edtBkash);
        edtRocket = findViewById(R.id.edtRocket);


        this.shopingMallAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, this.shopingmallname);
        spinnerShopingMall.setAdapter(shopingMallAdapter);

        this.categoryAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, this.categoryname);
        spinnerShopingCategory.setAdapter(categoryAdapter);

        getDataFromServer();

        rbShopkeeper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                linearLayout_ShopInfo.setVisibility(8);
            }
        });
        rbCustomer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                linearLayout_ShopInfo.setVisibility(0);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkBlank()) {
                    sendDataToServer();
                }
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    /* access modifiers changed from: private */
    public final boolean checkBlank() {
        if (!TextUtils.isEmpty(edtName.getText().toString())) {
            if (!TextUtils.isEmpty(edtAddress.getText().toString())) {
                if (!TextUtils.isEmpty(edtPhone.getText().toString())) {
                    if (!TextUtils.isEmpty(edtEmail.getText().toString())) {
                        if (radioGroupUserType.getCheckedRadioButtonId() != R.id.rbShopkeeper) {
                            return false;
                        }
                        if (!TextUtils.isEmpty(edtShopName.getText().toString())) {
                            return false;
                        }
                        Toast.makeText(this, "Please fill up all field", 1).show();
                        return true;
                    }
                }
            }
        }
        Toast.makeText(this, "Please fill up all field", 1).show();
        return true;
    }

    private final void getDataFromServer() {
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest shopingMallRequest = new JsonObjectRequest(0, APILink.ShopingMallListAPI, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.cancel();

                try {
                    if (response.getInt(NotificationCompat.CATEGORY_STATUS) == 1) {
                        JSONArray data = response.getJSONArray("response");
                        int length = data.length();
                        shopingmallname.clear();
                        shopingmallid.clear();

                        for (int i = 0; i < length; i++) {
                            JSONObject chield = data.getJSONObject(i);
                            String string = chield.getString("name");
                            shopingmallname.add(string);
                            String string2 = chield.getString("id");
                            shopingmallid.add(string2);
                        }
                        shopingMallAdapter.notifyDataSetChanged();
                        return;
                    }
                    Toast.makeText(RegistrationActivity.this, "Sorry, please try again", 1).show();
                } catch (Exception e) {
                    Toast.makeText(RegistrationActivity.this, e.getLocalizedMessage(), 1).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(RegistrationActivity.this, "Network Error. Please check internet connection", 1).show();
            }
        });

        JsonObjectRequest CategoryRequest = new JsonObjectRequest(0, APILink.CategoryListAPI, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.cancel();

                try {
                    if (response.getInt(NotificationCompat.CATEGORY_STATUS) == 1) {
                        JSONArray data = response.getJSONArray("response");
                        int length = data.length();
                        categoryname.clear();
                        categoryid.clear();

                        for (int i = 0; i < length; i++) {
                            JSONObject chield = data.getJSONObject(i);
                            String string = chield.getString("name");
                            categoryname.add(string);
                            String string2 = chield.getString("id");
                            categoryid.add(string2);
                        }
                        categoryAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Sorry, please try again", 1).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(RegistrationActivity.this, e.getLocalizedMessage(), 1).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(RegistrationActivity.this, error.getLocalizedMessage(), 1).show();
            }
        });

        requestQueue.add(shopingMallRequest);
        requestQueue.add(CategoryRequest);
    }

    /* access modifiers changed from: private */
    public final void sendDataToServer() {
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, APILink.RegistrationAPI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                try {
                    JSONObject message = new JSONObject(response);
                    if (message.getInt(NotificationCompat.CATEGORY_STATUS) == 1) {
                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                        intent.putExtra("Message", message.get("message").toString());
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Something went wrong. Please try again", 1).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(RegistrationActivity.this, e.getLocalizedMessage(), 1).show();
                    e.printStackTrace();
                }
                progressDialog.cancel();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();

                try {
                    String message = new String(error.networkResponse.data,"UTF-8");
                    Toast.makeText(RegistrationActivity.this, ""+message, Toast.LENGTH_LONG).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }){
            public Map<String, String> getParams() throws AuthFailureError {
                View radioButton = findViewById(radioGroupUserType.getCheckedRadioButtonId());
                type = ((RadioButton) radioButton).getText().toString();
                HashMap param = new HashMap();
                param.put("name", edtName.getText().toString());
                param.put(NotificationCompat.CATEGORY_EMAIL, edtEmail.getText().toString());
                param.put("address", edtAddress.getText().toString());
                param.put("phone", edtPhone.getText().toString());
                param.put("type", type);

                if (type.equals("Shopkeeper")) {
                    param.put("shopname", edtShopName.getText().toString());
                    param.put("shopno", edtShopNumber.getText().toString());
                    param.put("bkash", edtBkash.getText().toString());
                    param.put("rocket", edtRocket.getText().toString());
                    param.put("shopingmall", shopingmallid.get(spinnerShopingMall.getSelectedItemPosition()));
                    param.put("category", categoryid.get(spinnerShopingCategory.getSelectedItemPosition()));
                }
                return param;
            }

            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap header = new HashMap();
                header.put("Accept", "application/json");
                return header;
            }
        };

        DefaultRetryPolicy mRetryPolicy = new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(mRetryPolicy);
        requestQueue.add(stringRequest);
    }
}
