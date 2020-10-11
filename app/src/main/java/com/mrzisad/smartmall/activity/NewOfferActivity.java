package com.mrzisad.smartmall.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.mrzisad.smartmall.R;
import com.mrzisad.smartmall.utils.APILink;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewOfferActivity extends AppCompatActivity {
    private Bitmap bitmap;
    public boolean imaageSelect;
    public List<String> offerID;
    public List<String> offerImage;
    public ProgressDialog progressDialog;
    TextView textImageLocation;
    Button btnSelectImage, btnSave;
    LinearLayout layoutOffer;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_new_offer);
        super.setTitle("New Offer");

        this.progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);

        this.offerID = new ArrayList();
        this.offerImage = new ArrayList();
        getOfferListFromServer();

        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSave = findViewById(R.id.btnSave);
        textImageLocation = findViewById(R.id.textImageLocation);
        layoutOffer = findViewById(R.id.layoutOffer);

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkGalleryPermission()) {
                    chooseImage();
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imaageSelect) {
                    sendDataToServer();
                } else {
                    Toast.makeText(NewOfferActivity.this, "Please select an image to upload", 0).show();
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public final boolean checkGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") == 0) {
            return true;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_EXTERNAL_STORAGE")) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1000);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1000);
        }
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != 1000 || grantResults.length <= 0 || grantResults[0] != 0) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") == 0) {
            chooseImage();
        } else {
            Toast.makeText(this, "Permission Denied", 0).show();
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1000 && resultCode == -1 && data != null) {
            Uri filpath = data.getData();
            try {
                Bitmap decodeStream = BitmapFactory.decodeStream(getContentResolver().openInputStream(filpath));
                this.bitmap = decodeStream;
                textImageLocation.setText(filpath.toString());
                this.imaageSelect = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: private */
    public final void chooseImage() {
        Intent intent = new Intent("android.intent.action.PICK");
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 1000);
    }

    /* access modifiers changed from: private */
    public final String convertBitmaptoString() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        String encodedImage = Base64.encodeToString(outputStream.toByteArray(), 0);
        return encodedImage;
    }

    /* access modifiers changed from: private */
    public final void sendDataToServer() {
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, APILink.addOfferAPI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                try {
                    if (new JSONObject(response).getInt(NotificationCompat.CATEGORY_STATUS) == 1) {
                        Toast.makeText(NewOfferActivity.this, "Offer Added Successfully", 0).show();
                        startActivity(new Intent(NewOfferActivity.this, NewOfferActivity.class));
                        finish();
                    }
                } catch (Exception e) {
                    Toast.makeText(NewOfferActivity.this, e.getLocalizedMessage(), 0).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(NewOfferActivity.this, "Error. Please Try Again", 0).show();
            }
        }){
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap param = new HashMap();
                param.put("image", convertBitmaptoString());
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
    public final void getOfferListFromServer() {
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(0, APILink.getOfferAPI, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.cancel();

                try {
                    if (response.getInt(NotificationCompat.CATEGORY_STATUS) == 1) {
                        JSONArray data = response.getJSONArray("response");
                        offerID.clear();
                        offerImage.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject chield = data.getJSONObject(i);
                            offerImage.add(chield.get("image").toString());
                            offerID.add(chield.get("id").toString());
                        }
                        addDatainLayout();
                    } else {
                        Toast.makeText(NewOfferActivity.this, "Sorry, please try again", 1).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(NewOfferActivity.this, e.getLocalizedMessage(), 1).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(NewOfferActivity.this, ""+error.getLocalizedMessage(), 1).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    /* access modifiers changed from: private */
    public final void removeOfferFromServer(String id) {
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(0, APILink.removeOfferAPI + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.cancel();

                try {
                    if (response.getInt("response") == 1) {
                        Toast.makeText(NewOfferActivity.this, "Offer removed successfully", 1).show();
                        getOfferListFromServer();
                    } else {
                        Toast.makeText(NewOfferActivity.this, "Sorry, please try again", 1).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(NewOfferActivity.this, e.getLocalizedMessage(), 1).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(NewOfferActivity.this, error.getLocalizedMessage(), 1).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    /* access modifiers changed from: private */
    public final void addDatainLayout() {
        layoutOffer.removeAllViews();
        for (int i = 0; i < offerID.size(); i++) {
            View v = getLayoutInflater().inflate(R.layout.layout_offer, (ViewGroup) null);
            ImageView imgProduct = (ImageView) v.findViewById(R.id.imgProduct);
            ImageView btnRemove = (ImageView) v.findViewById(R.id.btnRemove);
            StringBuilder sb = new StringBuilder();
            sb.append(APILink.PictureLink);
            sb.append(offerImage.get(i));
            Glide.with(NewOfferActivity.this).load(sb.toString()).into(imgProduct);
            final int finalI = i;
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeOfferFromServer(offerID.get(finalI));
                }
            });
            layoutOffer.addView(v);
        }
        progressDialog.cancel();
    }
}

