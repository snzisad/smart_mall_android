package com.mrzisad.smartmall.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mrzisad.smartmall.R;
import com.mrzisad.smartmall.utils.APILink;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PasswordChangeActivity extends AppCompatActivity {
    public int password;
    public ProgressDialog progressDialog;
    Button btnGeneratePassword, btnSavePassword;
    TextView tvPassword;
    EditText edtUID;


    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_password_change);
        setTitle("Change User Password");

        this.progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);

        edtUID = findViewById(R.id.edtUID);
        btnGeneratePassword = (Button) findViewById(R.id.btnGeneratePassword);
        btnSavePassword = (Button) findViewById(R.id.btnSavePassword);
        tvPassword = findViewById(R.id.tvPassword);

        btnGeneratePassword.setVisibility(8);

        btnGeneratePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password = new Random().nextInt(999999) + 111111;
                tvPassword.setText("Password: " + password);
                tvPassword.setVisibility(0);
            }
        });
        btnSavePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtUID.getText())) {
                    edtUID.setError("Required");
                    return;
                }
                password = new Random().nextInt(999999) + 111111;
                sendDataToServer(edtUID.getText().toString(), password);

            }
        });
    }


    public final void sendDataToServer(final String id, final int password) {
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(APILink.PasswordChangeAPI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                try {
                    if (new JSONObject(response).getInt("response") == 1) {
                        Toast.makeText(PasswordChangeActivity.this, "Password was sent to phone", 1).show();
                    } else {
                        Toast.makeText(PasswordChangeActivity.this, "Sorry, no user found", 1).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(PasswordChangeActivity.this, "Sorry, Please try again ", 1).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(PasswordChangeActivity.this, "Sorry, Please try again ", 1).show();
            }
        }){
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap param = new HashMap();
                param.put("uid", id);
                param.put("password", String.valueOf(password));
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

