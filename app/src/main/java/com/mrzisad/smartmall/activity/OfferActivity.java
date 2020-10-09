package com.mrzisad.smartmall.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.mrzisad.smartmall.R;
import com.mrzisad.smartmall.utils.APILink;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OfferActivity extends AppCompatActivity {
    public ImageView[] imgList;
    public List<String> offerImage;
    public ProgressDialog progressDialog;


    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_offer);
        this.progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);

        this.offerImage = new ArrayList();

        ImageView imageView = (ImageView) findViewById(R.id.imgOffer1);
        ImageView imageView2 = (ImageView) findViewById(R.id.imgOffer2);
        ImageView imageView3 = (ImageView) findViewById(R.id.imgOffer3);
        ImageView imageView4 = (ImageView) findViewById(R.id.imgOffer4);
        ImageView imageView5 = (ImageView) findViewById(R.id.imgOffer5);
        ImageView imgLogin = (ImageView) findViewById(R.id.imgLogin);
        this.imgList = new ImageView[]{imageView, imageView2, imageView3, imageView4, imageView5};

        imgLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OfferActivity.this, LoginActivity.class));
            }
        });

        getOfferListFromServer();
    }

    private final void getOfferListFromServer() {
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(0, APILink.getOfferAPI, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int total;
                progressDialog.cancel();

                try {
                    if (response.getInt(NotificationCompat.CATEGORY_STATUS) == 1) {
                        JSONArray data = response.getJSONArray("response");
                        if (data.length() > 5) {
                            total = 5;
                        } else {
                            total = data.length();
                        }
                        offerImage.clear();
                        for (int i = 0; i < total; i++) {
                            JSONObject chield = data.getJSONObject(i);
                            offerImage.add(chield.get("image").toString());
                            Glide.with(OfferActivity.this).load(APILink.PictureLink + chield.get("image").toString()).into(imgList[i]);
                        }
                    } else {
                        Toast.makeText(OfferActivity.this, "Sorry, please try again", 1).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(OfferActivity.this, e.getLocalizedMessage(), 1).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(OfferActivity.this, error.getLocalizedMessage(), 1).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}

