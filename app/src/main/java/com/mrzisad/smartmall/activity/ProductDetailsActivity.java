package com.mrzisad.smartmall.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mrzisad.smartmall.R;
import com.mrzisad.smartmall.model.Product;
import com.mrzisad.smartmall.model.ProductPayment;
import com.mrzisad.smartmall.utils.APILink;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {
    private String bkash = "";
    private List<ImageView> imagebtnlist;
    private String[] picturelist;
    public List<Product> productList;
    public ProgressDialog progressDialog;
    private int quantity;
    private String rocket = "";
    Button btn_Buy;
    ImageView img_product1, img_product2, img_product3, img_product_full;
    TextView tv_product_name, tv_product_price, tv_product_quantity, tv_product_size, tv_product_description, tv_shop_description, tv_not_available;
    ConstraintLayout layout_product_details;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_product_details);

        img_product1 = findViewById(R.id.img_product1);
        img_product2 = findViewById(R.id.img_product2);
        img_product3 = findViewById(R.id.img_product3);
        img_product_full = findViewById(R.id.img_product_full);
        tv_product_name = findViewById(R.id.tv_product_name);
        tv_product_price = findViewById(R.id.tv_product_price);
        tv_product_quantity = findViewById(R.id.tv_product_quantity);
        tv_product_size = findViewById(R.id.tv_product_size);
        tv_product_description = findViewById(R.id.tv_product_description);
        tv_shop_description = findViewById(R.id.tv_shop_description);
        layout_product_details = findViewById(R.id.layout_product_details);

        this.imagebtnlist = Arrays.asList(new ImageView[] {img_product1, img_product2, img_product3});

        this.progressDialog = new ProgressDialog(this);
        ProgressDialog progressDialog2 = this.progressDialog;
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);

        this.productList = new ArrayList();
        img_product1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_product_full.setImageDrawable(img_product1.getDrawable());
                loadImage(0);
            }
        });
        img_product2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_product_full.setImageDrawable(img_product2.getDrawable());
                loadImage(1);
            }
        });
        img_product3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_product_full.setImageDrawable(img_product3.getDrawable());
                loadImage(2);
            }
        });
        btn_Buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        getDataFromServer();
    }

    private final void getDataFromServer() {
        progressDialog.show();
        productList.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(0, APILink.ProductDetailsAPI + APILink.PID, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.cancel();

                try {
                    if (response.getInt(NotificationCompat.CATEGORY_STATUS) == 1) {
                        JSONArray data = response.getJSONArray("response");
                        if (response.length() > 0) {
                            JSONObject product = data.getJSONObject(0);
                            JSONObject shop = product.getJSONObject("shop");
                            setTitle(product.get("name").toString());
                            loadImages(product.get("picture").toString());
                            tv_product_name.setText(product.get("name").toString());
                            tv_product_price.setText("Price: " + product.get("price").toString() + " ৳");
                            StringBuilder sb = new StringBuilder();
                            sb.append("Quantity: ");
                            sb.append(product.get("quantity").toString());
                            tv_product_quantity.setText(sb.toString());
                            tv_product_size.setText("Size/Color: " + product.get("size").toString());
                            tv_product_description.setText(product.get("description").toString());
                            tv_shop_description.setText(shop.get("shopname").toString() + "\nShop No: " + shop.get("shopno").toString() + "\nType: " + shop.getJSONObject("category").get("name").toString() + "\n" + shop.getJSONObject("shopingmall").get("name").toString());
                            Product product1 = new Gson().fromJson(product.toString(), Product.class);
                            productList.add(product1);

                            if (product.getInt("quantity") > 0) {
                                btn_Buy.setVisibility(0);
                                tv_not_available.setVisibility(8);
                            } else {
                                btn_Buy.setVisibility(8);
                                tv_not_available.setVisibility(0);
                            }
                            layout_product_details.setVisibility(0);
                        } else {
                            Toast.makeText(ProductDetailsActivity.this, "No product found", 1).show();
                        }
                    } else {
                        Toast.makeText(ProductDetailsActivity.this, "Sorry, please try again", 1).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ProductDetailsActivity.this, e.getLocalizedMessage(), 1).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(ProductDetailsActivity.this, error.getLocalizedMessage(), 1).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0054  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00e0  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void loadImages(java.lang.String r9) {
        /*
            r8 = this;
            r0 = r9
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            java.lang.String r1 = "\\|ZISAD\\|"
            kotlin.text.Regex r2 = new kotlin.text.Regex
            r2.<init>((java.lang.String) r1)
            r1 = 0
            java.util.List r0 = r2.split(r0, r1)
            r2 = r1
            boolean r3 = r0.isEmpty()
            if (r3 != 0) goto L_0x0049
            int r3 = r0.size()
            java.util.ListIterator r3 = r0.listIterator(r3)
            r4 = 0
        L_0x001f:
            boolean r5 = r3.hasPrevious()
            if (r5 == 0) goto L_0x0048
            java.lang.Object r5 = r3.previous()
            java.lang.String r5 = (java.lang.String) r5
            r6 = r5
            java.lang.CharSequence r6 = (java.lang.CharSequence) r6
            int r6 = r6.length()
            r7 = 1
            if (r6 != 0) goto L_0x0037
            r5 = 1
            goto L_0x0038
        L_0x0037:
            r5 = 0
        L_0x0038:
            if (r5 != 0) goto L_0x0047
            r4 = r0
            java.lang.Iterable r4 = (java.lang.Iterable) r4
            int r5 = r3.nextIndex()
            int r5 = r5 + r7
            java.util.List r4 = kotlin.collections.CollectionsKt.take(r4, r5)
            goto L_0x004e
        L_0x0047:
            goto L_0x001f
        L_0x0048:
            goto L_0x004a
        L_0x0049:
        L_0x004a:
            java.util.List r4 = kotlin.collections.CollectionsKt.emptyList()
        L_0x004e:
            java.util.Collection r4 = (java.util.Collection) r4
            r0 = r4
            r2 = r1
            if (r0 == 0) goto L_0x00e0
            r3 = r0
            java.lang.String[] r4 = new java.lang.String[r1]
            java.lang.Object[] r4 = r3.toArray(r4)
            if (r4 == 0) goto L_0x00d8
            r0 = r4
            java.lang.String[] r0 = (java.lang.String[]) r0
            r8.picturelist = r0
            r2 = r8
            android.support.v4.app.FragmentActivity r2 = (android.support.p000v4.app.FragmentActivity) r2
            com.bumptech.glide.RequestManager r2 = com.bumptech.glide.Glide.with((android.support.p000v4.app.FragmentActivity) r2)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "https://mizan.jannathost.com/picture/"
            r3.append(r4)
            r4 = r0[r1]
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            com.bumptech.glide.RequestBuilder r2 = r2.load((java.lang.String) r3)
            int r3 = com.client.snzisad.smartmall.C0611R.C0613id.img_product_full
            android.view.View r3 = r8._$_findCachedViewById(r3)
            android.widget.ImageView r3 = (android.widget.ImageView) r3
            r2.into((android.widget.ImageView) r3)
            int r2 = r0.length
            r3 = 0
        L_0x008d:
            if (r3 >= r2) goto L_0x00d7
            java.util.List<android.widget.ImageView> r4 = r8.imagebtnlist
            if (r4 != 0) goto L_0x0099
            java.lang.String r5 = "imagebtnlist"
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r5)
        L_0x0099:
            java.lang.Object r4 = r4.get(r3)
            android.widget.ImageView r4 = (android.widget.ImageView) r4
            r4.setVisibility(r1)
            r4 = r8
            android.support.v4.app.FragmentActivity r4 = (android.support.p000v4.app.FragmentActivity) r4
            com.bumptech.glide.RequestManager r4 = com.bumptech.glide.Glide.with((android.support.p000v4.app.FragmentActivity) r4)
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "https://mizan.jannathost.com/picture/"
            r5.append(r6)
            r6 = r0[r3]
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            com.bumptech.glide.RequestBuilder r4 = r4.load((java.lang.String) r5)
            java.util.List<android.widget.ImageView> r5 = r8.imagebtnlist
            if (r5 != 0) goto L_0x00ca
            java.lang.String r6 = "imagebtnlist"
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r6)
        L_0x00ca:
            java.lang.Object r5 = r5.get(r3)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r4.into((android.widget.ImageView) r5)
            int r3 = r3 + 1
            goto L_0x008d
        L_0x00d7:
            return
        L_0x00d8:
            kotlin.TypeCastException r1 = new kotlin.TypeCastException
            java.lang.String r4 = "null cannot be cast to non-null type kotlin.Array<T>"
            r1.<init>(r4)
            throw r1
        L_0x00e0:
            kotlin.TypeCastException r1 = new kotlin.TypeCastException
            java.lang.String r3 = "null cannot be cast to non-null type java.util.Collection<T>"
            r1.<init>(r3)
            throw r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.client.snzisad.smartmall.ProductDetailsActivity.loadImages(java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public final void loadImage(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append(APILink.PictureLink);
        String[] strArr = this.picturelist;
        sb.append(strArr[i]);
        Glide.with(this).load(sb.toString()).into(img_product_full);
    }

    /* access modifiers changed from: private */
    public final void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.layout_buy_product, (ViewGroup) null, false);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final Button btnBuy = (Button) view.findViewById(R.id.btnBuy);
        final Spinner spinnerType = (Spinner) view.findViewById(R.id.spinnerType);
        final EditText edtQuantity =  view.findViewById(R.id.edtQuantity);
        final EditText edtSize =  view.findViewById(R.id.edtSize);
        final ProgressBar pbBuy =  view.findViewById(R.id.pbBuy);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinnerType.getSelectedItemPosition() == 2) {
                    btnBuy.setText("Order");
                    return;
                }
                btnBuy.setText("Continue");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(edtQuantity.getText().toString())) {
                    if (productList.get(0).getQuantity().intValue() <= 0) {
                        Toast.makeText(ProductDetailsActivity.this, "Sorry, out of stock", 0).show();
                        return;
                    }
                    int intValue = productList.get(0).getQuantity().intValue();
                    if (Integer.parseInt(edtQuantity.getText().toString()) > intValue) {
                        Toast.makeText(ProductDetailsActivity.this, "Sorry, only " + productList.get(0).getQuantity() + " items available", 0).show();
                        return;
                    }

                    if (spinnerType.getSelectedItemPosition() == 2) {
                        String id = productList.get(0).getId();
                        String obj = edtQuantity.getText().toString();
                        String obj2 = edtSize.getText().toString();
                        String obj3 = spinnerType.getSelectedItem().toString();
                        sendDataToServer(pbBuy, btnBuy, id, obj, obj2, obj3, alertDialog);
                        return;
                    }
                    if (spinnerType.getSelectedItemPosition() == 0) {
                        Product.Shop shop = productList.get(0).shop;
                        if (TextUtils.isEmpty(shop.getBkash())) {
                            Toast.makeText(ProductDetailsActivity.this, "Sorry, you cann't buy this product using bKash", 0).show();
                            return;
                        }
                    }
                    if (spinnerType.getSelectedItemPosition() == 1) {
                        Product.Shop shop2 = productList.get(0).shop;
                        if (TextUtils.isEmpty(shop2.getRocket())) {
                            Toast.makeText(ProductDetailsActivity.this, "Sorry, you cann't buy this product using Rocket", 0).show();
                            return;
                        }
                    }
                    ProductPayment.type = spinnerType.getSelectedItemPosition();
                    ProductPayment.product = productList.get(0);
                    ProductPayment.quantity = Integer.parseInt(edtQuantity.getText().toString());
                    ProductPayment.size = edtSize.getText().toString();
                    ProductPayment.position = 0;
                    startActivity(new Intent(ProductDetailsActivity.this, PaymentActivity.class));
                    alertDialog.cancel();
                    return;
                }
                edtQuantity.setError("Required");
            }
        });
    }

    /* access modifiers changed from: private */
    public final void sendDataToServer(final ProgressBar progressBar, final Button button, final String pid, String quantity2, final String size, final String payment_type, final AlertDialog alertDialog) {
        progressBar.setVisibility(0);
        button.setVisibility(8);
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, APILink.ProductOrderAPI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (new JSONObject(response).get(NotificationCompat.CATEGORY_STATUS).toString() == "1") {
                        Toast.makeText(ProductDetailsActivity.this, "Product ordered Successfully", 0).show();
                        alertDialog.cancel();
                    } else {
                        Toast.makeText(ProductDetailsActivity.this, "Please try again", 0).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ProductDetailsActivity.this, "" + e.getLocalizedMessage(), 0).show();
                    e.printStackTrace();
                }
                progressBar.setVisibility(8);
                button.setVisibility(0);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(8);
                button.setVisibility(0);
                StringBuilder sb = new StringBuilder();
                sb.append("Error: ");
                sb.append(error.getLocalizedMessage());
                Toast.makeText(ProductDetailsActivity.this, sb.toString(), 0).show();
            }
        }){
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap param = new HashMap();
                String str = APILink.UID;
                param.put("user_id", str);
                param.put("product_id", pid);
                param.put("quantity", quantity);
                param.put("size", size);
                param.put("payment_type", payment_type);
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
