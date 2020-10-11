package com.mrzisad.smartmall.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mrzisad.smartmall.R;
import com.mrzisad.smartmall.utils.APILink;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    public SharedPreferences.Editor editor;
    public ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    public String type;
    TextView textView_warning, forgot_password;
    Button btnLogin, btnCreateAccount;
    EditText edtPhone, edtPassword;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_login);
        super.setTitle("Login");

        String message = getIntent().getStringExtra("Message");

        textView_warning = findViewById(R.id.textView_warning);
        forgot_password = findViewById(R.id.forgot_password);
        btnLogin = findViewById(R.id.btnLogin);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);

        if (message != null) {
            textView_warning.setText(message);
            textView_warning.setVisibility(0);
        }
        this.progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        sharedPreferences = getSharedPreferences("Status", 0);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        this.editor = edit;
        if (sharedPreferences.getBoolean("logged", false)) {
            String type2 = sharedPreferences.getString("type", (String) null);
            if (type2.equals("Customer")){
                APILink.UID = sharedPreferences.getString("id", (String) null);
                startActivity(new Intent(this, CustomerActivity.class));
                finish();
            }
            else if (type2.equals("Shopkeeper")) {
                APILink.UID = sharedPreferences.getString("id", (String) null);
                startActivity(new Intent(this, ShopkeeperActivity.class));
                finish();
            }
            else if (type2.equals("Admin")) {
                APILink.UID = sharedPreferences.getString("id", (String) null);
                startActivity(new Intent(this, AdminActivity.class));
                finish();
            }
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBlank()) {
                    sendDataToServer();
                }
            }
        });
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, PasswordChangeActivity.class));
            }
        });
    }

    /* access modifiers changed from: private */
    public final boolean checkBlank() {
        if (!TextUtils.isEmpty(edtPhone.getText().toString())) {
            if (!TextUtils.isEmpty(edtPassword.getText().toString())) {
                return true;
            }
        }
        Toast.makeText(this, "Please fill up all field", 1).show();
        return false;
    }

    /* access modifiers changed from: private */
    public final void sendDataToServer() {
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, APILink.LoginAPI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                try {
                    if (new JSONObject(response).getInt(NotificationCompat.CATEGORY_STATUS) == 1){
                        APILink.UID = edtPhone.getText().toString();
                        editor.putBoolean("logged", true);
                        editor.putString("type", type);
                        editor.putString("id", edtPhone.getText().toString());
                        editor.apply();

                        if (type.equals("Customer")) {
                            startActivity(new Intent(LoginActivity.this, CustomerActivity.class));
                            finish();
                        } else if (type.equals("Shopkeeper")) {
                            startActivity(new Intent(LoginActivity.this, ShopkeeperActivity.class));
                            finish();
                        } else if (type.equals("Admin")) {
                            startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid ID or Password", 1).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), 1).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                handleError(error);
            }
        }){
            public Map<String, String> getParams() throws AuthFailureError {
                RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
                View button = findViewById(radioGroup.getCheckedRadioButtonId());
                type = ((RadioButton) button).getText().toString();
                HashMap param = new HashMap();
                param.put("phone", edtPhone.getText().toString());
                param.put("password", edtPassword.getText().toString());
                param.put("type", type);
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
    public final void handleError(VolleyError error) {
        if (error instanceof AuthFailureError) {
            Toast.makeText(this, "Auth Fail", 0).show();
        } else if ((error instanceof TimeoutError) || (error instanceof NoConnectionError)) {
            Toast.makeText(this, "Internet not working", 0).show();
        } else if (error instanceof ServerError) {
            Toast.makeText(this, "Server Error: " + error.toString(), 0).show();
        } else if (error instanceof ParseError) {
            Toast.makeText(this, "Failed to load data", 0).show();
        } else if (error instanceof NetworkError) {
            Toast.makeText(this, "Network Error", 0).show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.help) {
            return super.onOptionsItemSelected(item);
        }
        startActivity(new Intent(this, HelpActivity.class));
        return true;
    }
}
