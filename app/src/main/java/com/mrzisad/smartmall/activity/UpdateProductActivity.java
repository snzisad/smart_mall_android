package com.mrzisad.smartmall.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
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

import java.util.HashMap;
import java.util.Map;

public class UpdateProductActivity extends AppCompatActivity {
    public ProgressDialog progressDialog;
    EditText edtPID, edtProductName, edtSize, edtPrice, edtDescription, edtQuantity;
    Button btnSearchProduct, btnSave;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_update_product);
        super.setTitle("Update Product");

        this.progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);

        String pid = getIntent().getStringExtra("PID");

        edtPID = findViewById(R.id.edtPID);
        edtProductName = findViewById(R.id.edtProductName);
        edtSize = findViewById(R.id.edtSize);
        edtPrice = findViewById(R.id.edtPrice);
        edtDescription = findViewById(R.id.edtDescription);
        edtQuantity = findViewById(R.id.edtQuantity);
        btnSearchProduct = findViewById(R.id.btnSearchProduct);
        btnSave = findViewById(R.id.btnSave);

        if (pid != null) {
            edtPID.setText(pid.toString());
            getDataFromServer();
        }

        btnSearchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtPID.getText())) {
                    edtPID.setError("Required");
                } else {
                    getDataFromServer();
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBlank()) {
                    sendDataToServer();
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public final void getDataFromServer() {
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringBuilder sb = new StringBuilder();
        sb.append(APILink.ProductDetailsAPI);
        sb.append(edtPID.getText().toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(0, sb.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.cancel();

                try {
                    if (response.getInt(NotificationCompat.CATEGORY_STATUS) == 1) {
                        JSONArray data = response.getJSONArray("response");
                        if (response.length() > 0) {
                            JSONObject product = data.getJSONObject(0);
                            edtProductName.setText(product.get("name").toString());
                            edtSize.setText(product.get("size").toString());
                            edtPrice.setText(product.get("price").toString());
                            edtDescription.setText(product.get("description").toString());
                            edtQuantity.setText(product.get("quantity").toString());
                        } else {
                            Toast.makeText(UpdateProductActivity.this, "No product found", 1).show();
                        }
                    } else {
                        Toast.makeText(UpdateProductActivity.this, "Sorry, please try again", 1).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(UpdateProductActivity.this, e.getLocalizedMessage(), 1).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(UpdateProductActivity.this, error.getLocalizedMessage(), 1).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    /* access modifiers changed from: private */
    public final void sendDataToServer() {
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(APILink.ProductUpdateAPI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();

                try {
                    if (new JSONObject(response).getInt("response") == 1){
                        Toast.makeText(UpdateProductActivity.this, "Product Updated Successfully", 0).show();
                        finish();
                    } else {
                        Toast.makeText(UpdateProductActivity.this, "You don't have permission to edit this product", 0).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(UpdateProductActivity.this, e.getLocalizedMessage(), 0).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(UpdateProductActivity.this, "Error. Please Try Again", 0).show();
            }
        }){
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap param = new HashMap();
                param.put("name", edtProductName.getText().toString());
                param.put("id", edtPID.getText().toString());
                String str = APILink.UID;
                param.put("shop_id", str);
                param.put("description", edtDescription.getText().toString());
                param.put("price", edtPrice.getText().toString());
                param.put("size", edtSize.getText().toString());
                param.put("quantity", edtQuantity.getText().toString());
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

    /* access modifiers changed from: private */
    public final boolean checkBlank() {
        if (!TextUtils.isEmpty(edtProductName.getText().toString())) {
            if (!TextUtils.isEmpty(edtDescription.getText().toString())) {
                if (!TextUtils.isEmpty(edtQuantity.getText().toString())) {
                    if (!TextUtils.isEmpty(edtPrice.getText().toString())) {
                        return false;
                    }
                }
            }
        }
        Toast.makeText(this, "Please fill up all required field", 0).show();
        return true;
    }
}

