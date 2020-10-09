package com.mrzisad.smartmall.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import org.json.JSONObject;

public class UserInformationActivity extends AppCompatActivity {
    public ProgressDialog progressDialog;
    Button btnSearch;
    EditText edtUID;
    TextView tvName, tvAddress, tvPhone, tvEmail, tvType, tvShopName, tvShopNumber, tvShopingMall, tvShopCategory;
    LinearLayout layout_ShopDetails, layout_User_Info;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_user_information);
        setTitle("User Information");

        this.progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);

        btnSearch = findViewById(R.id.btnSearch);
        edtUID = findViewById(R.id.edtUID);
        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        tvType = findViewById(R.id.tvType);
        tvShopName = findViewById(R.id.tvShopName);
        tvShopingMall = findViewById(R.id.tvShopingMall);
        tvShopCategory = findViewById(R.id.tvShopCategory);
        layout_ShopDetails = findViewById(R.id.layout_ShopDetails);
        layout_User_Info = findViewById(R.id.layout_User_Info);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(btnSearch.getText())) {
                    getUserInfoFromServer(edtUID.getText().toString());
                    return;
                }
                edtUID.setError("Required");
            }
        });
    }

    /* access modifiers changed from: private */
    public final void getUserInfoFromServer(String id) {
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(0, APILink.UserInfoAPI + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.cancel();

                try {
                    int status = response.getInt(NotificationCompat.CATEGORY_STATUS);
                    JSONObject data = response.getJSONObject("response");
                    if (status == 1 && data.length() > 0) {
                        tvName.setText("Name: " + data.get("name"));
                        tvAddress.setText("Address: " + data.get("address"));
                        tvPhone.setText("Phone: " + data.get("phone"));
                        tvEmail.setText("Email: " + data.get(NotificationCompat.CATEGORY_EMAIL));
                        String type = data.get("type").toString();
                        tvType.setText("User Type: " + type);
                        if (type == "Shopkeeper") {
                            layout_ShopDetails.setVisibility(0);
                            tvShopName.setText("Shop Name: " + data.get("shopname"));
                            tvShopNumber.setText("Shop Number: " + data.get("shopno"));
                            tvShopingMall.setText("Shoping Mall: " + data.getJSONObject("shopingmall").get("name"));
                            tvShopCategory.setText("Shop Category: " + data.getJSONObject("category").get("name"));
                        } else {
                            layout_ShopDetails.setVisibility(8);
                        }
                        layout_User_Info.setVisibility(0);
                    }
                } catch (Exception e) {
                    Toast.makeText(UserInformationActivity.this, "Sorry, no user found", 1).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(UserInformationActivity.this, "Sorry, try again", 1).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}
