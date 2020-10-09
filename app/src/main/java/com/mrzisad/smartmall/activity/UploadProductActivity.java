package com.mrzisad.smartmall.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mrzisad.smartmall.R;
import com.mrzisad.smartmall.utils.APILink;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class UploadProductActivity extends AppCompatActivity {
    private final Bitmap[] imageBitmap = new Bitmap[3];
    public ImageView imageView;
    public int position = 3;
    public ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    ImageView imgPic1, imgPic2, imgPic3;
    Button btnUpload;
    EditText edtProductName, edtDescription, edtPrice, edtSize, edtQuantity;


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_upload_product);
        super.setTitle("Upload Product");

        this.progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);
        sharedPreferences = getSharedPreferences("Status", 0);

        imgPic1 = findViewById(R.id.imgPic1);
        imgPic2 = findViewById(R.id.imgPic2);
        imgPic3 = findViewById(R.id.imgPic3);
        btnUpload = findViewById(R.id.btnUpload);
        edtProductName = findViewById(R.id.edtProductName);
        edtDescription = findViewById(R.id.edtDescription);
        edtPrice = findViewById(R.id.edtPrice);
        edtSize = findViewById(R.id.edtSize);
        edtQuantity = findViewById(R.id.edtQuantity);

        imgPic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView = imgPic1;
                position = 0;
                if (checkGalleryPermission()) {
                    chooseImage();
                }
            }
        });
        imgPic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView = imgPic2;
                position = 1;
                if (checkGalleryPermission()) {
                    chooseImage();
                }
            }
        });
        imgPic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView = imgPic3;
                position = 2;
                if (checkGalleryPermission()) {
                    chooseImage();
                }
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkBlank()) {
                    sendDataToServer();
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
                this.imageBitmap[this.position] = BitmapFactory.decodeStream(getContentResolver().openInputStream(filpath));
                imageView.setImageBitmap(this.imageBitmap[this.position]);
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
        String imagesString = "";
        for (int i = 0; i <= 2; i++) {
            Bitmap bitmap = this.imageBitmap[i];
            if (bitmap != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                imagesString = imagesString + Base64.encodeToString(outputStream.toByteArray(), 0) + "|ZISAD|";
            }
        }
        return imagesString;
    }

    /* access modifiers changed from: private */
    public final void sendDataToServer() {
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(APILink.ProductUploadAPI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                try {
                    if (new JSONObject(response).getInt(NotificationCompat.CATEGORY_STATUS) == 1) {
                        Toast.makeText(UploadProductActivity.this, "Product Uploaded Successfully", 0).show();
                        startActivity(new Intent(UploadProductActivity.this, UploadProductActivity.class));
                        finish();
                    }
                } catch (Exception e) {
                    Toast.makeText(UploadProductActivity.this, e.getLocalizedMessage(), 0).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(UploadProductActivity.this, "Error. Please Try Again", 0).show();
            }
        }){
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap param = new HashMap();
                param.put("name", edtProductName.getText().toString());
                String str = APILink.UID;
                param.put("shop_id", str);
                param.put("description", edtDescription.getText().toString());
                param.put("price", edtPrice.getText().toString());
                param.put("size", edtSize.getText().toString());
                param.put("quantity", edtQuantity.getText().toString());
                param.put("images", convertBitmaptoString());
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

    public final boolean checkBlank() {
        if (!TextUtils.isEmpty(edtProductName.getText().toString())) {
            if (!TextUtils.isEmpty(edtDescription.getText().toString())) {
                if (!TextUtils.isEmpty(edtQuantity.getText().toString())) {
                    if (!TextUtils.isEmpty(edtPrice.getText().toString())) {
                        if (imgPic1.getDrawable() != null) {
                            return false;
                        }
                        Toast.makeText(this, "Please select at least one image", 0).show();
                        return true;
                    }
                }
            }
        }
        Toast.makeText(this, "Please fill up all required field", 0).show();
        return true;
    }
}
