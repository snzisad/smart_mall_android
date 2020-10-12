package com.mrzisad.smartmall.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.mrzisad.smartmall.R;
import com.mrzisad.smartmall.activity.PaymentActivity;
import com.mrzisad.smartmall.activity.ProductDetailsActivity;
import com.mrzisad.smartmall.activity.UpdateProductActivity;
import com.mrzisad.smartmall.model.Order;
import com.mrzisad.smartmall.model.Product;
import com.mrzisad.smartmall.model.ProductPayment;
import com.mrzisad.smartmall.utils.APILink;
import com.mrzisad.smartmall.utils.DataContainer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductListAdapter extends BaseAdapter {
    Button btnCancelOrder;
    Button btnConfirm;
    Context context;
    ListView lvProductList;
    List<Product> orderList;
    List<Product> productList;
    int type;

    public ProductListAdapter(Context context2, List<Product> product, int type2) {
        this.context = context2;
        this.productList = product;
        this.type = type2;
    }

    public ProductListAdapter(Context context2, List<Product> product, int type2, ListView lvProductList2, Button btnConfirm2, Button btnCancelOrder2) {
        this.context = context2;
        this.productList = product;
        this.type = type2;
        this.lvProductList = lvProductList2;
        this.btnConfirm = btnConfirm2;
        this.btnCancelOrder = btnCancelOrder2;
        this.orderList = new ArrayList();
    }

    public int getCount() {
        return this.productList.size();
    }

    public Object getItem(int position) {
        return this.productList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, final View convertView, ViewGroup parent) {
        View v = View.inflate(this.context, R.layout.layout_product_list, (ViewGroup) null);
        TextView tvName = (TextView) v.findViewById(R.id.tvName);
        TextView tvPrice = (TextView) v.findViewById(R.id.tvPrice);
        TextView tvPID = (TextView) v.findViewById(R.id.tvPID);
        Button btnBuy = (Button) v.findViewById(R.id.btnBuy);
        ConstraintLayout cl_product_info = (ConstraintLayout) v.findViewById(R.id.cl_product_info);
        final Button btnAddtoCart = (Button) v.findViewById(R.id.btnAddtoCart);
        ImageView imgProduct = (ImageView) v.findViewById(R.id.imgProduct);
        final int i = position;
        if (this.btnCancelOrder != null) {
            btnAddtoCart.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    DataContainer.products = orderList;
                    ProductListAdapter.this.orderList.add(productList.get(position));
                    Button button = ProductListAdapter.this.btnConfirm;
                    button.setText("Confirm (" + ProductListAdapter.this.orderList.size() + ")");
                    ProductListAdapter.this.btnConfirm.setVisibility(0);
                    ProductListAdapter.this.btnCancelOrder.setVisibility(0);
                    ProductListAdapter.this.lvProductList.setPadding(0, 0, 0, 80);
                    btnAddtoCart.setBackgroundColor(0);
                    btnAddtoCart.setText("Added");
                    btnAddtoCart.setClickable(false);
                    btnAddtoCart.setTextColor(context.getResources().getColor(R.color.colorAsh));
                }
            });
            this.btnCancelOrder.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ProductListAdapter.this.btnConfirm.setVisibility(8);
                    ProductListAdapter.this.btnCancelOrder.setVisibility(8);
                    ProductListAdapter.this.lvProductList.setPadding(0, 0, 0, 0);
                    ProductListAdapter.this.orderList.clear();
                }
            });
        } else {
            btnAddtoCart.setVisibility(8);
        }
        Glide.with(this.context).load(productImageURL(this.productList.get(position).getPicture())).into(imgProduct);
        final String pid = this.productList.get(position).getId();
        tvPID.setText("ID: " + pid);
        tvName.setText(this.productList.get(position).getName());
        tvPrice.setText(this.productList.get(position).getPrice() + " ৳");
        cl_product_info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                APILink.PID = ProductListAdapter.this.productList.get(i).getId();
                ProductListAdapter.this.context.startActivity(new Intent(ProductListAdapter.this.context, ProductDetailsActivity.class));
            }
        });
        if (this.type == 1) {
            btnBuy.setText("Edit");
            btnBuy.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(ProductListAdapter.this.context, UpdateProductActivity.class);
                    intent.putExtra("PID", pid);
                    ProductListAdapter.this.context.startActivity(intent);
                }
            });
        } else {
            btnBuy.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ProductListAdapter.this.showDialog(convertView, i);
                }
            });
        }
        return v;
    }

    public String productImageURL(String imagelist) {
        String[] images = imagelist.split("\\|ZISAD\\|");
        return APILink.PictureLink + images[0];
    }

    /* access modifiers changed from: private */
    public void showDialog(View v, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        View view = View.inflate(this.context, R.layout.layout_buy_product, (ViewGroup) null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        final Spinner spinnerType = (Spinner) view.findViewById(R.id.spinnerType);
        final Button btnBuy = (Button) view.findViewById(R.id.btnBuy);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (spinnerType.getSelectedItemPosition() == 2) {
                    btnBuy.setText("Order");
                } else {
                    btnBuy.setText("Continue");
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        final EditText editText = (EditText) view.findViewById(R.id.edtQuantity);
        final int i = position;
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.pbBuy);
        final EditText editText2 = (EditText) view.findViewById(R.id.edtSize);
        btnBuy.setOnClickListener(new View.OnClickListener() {
            ProductPayment productPayment = new ProductPayment();

            public void onClick(View v) {
                if (TextUtils.isEmpty(editText.getText().toString())) {
                    editText.setError("Required");
                } else if (ProductListAdapter.this.productList.get(i).getQuantity().intValue() <= 0) {
                    Toast.makeText(ProductListAdapter.this.context, "Sorry, out of stock", 0).show();
                } else if (productList.get(i).getQuantity().intValue() < Integer.parseInt(editText.getText().toString())) {
                    Toast.makeText(context, "Sorry, only " + ProductListAdapter.this.productList.get(i).getQuantity() + " items available", 0).show();
                } else if (spinnerType.getSelectedItemPosition() == 2) {
                    sendDataToServer(progressBar, btnBuy, productList.get(i).getId(), editText.getText().toString(), editText2.getText().toString(), spinnerType.getSelectedItem().toString(), alertDialog);
                } else if (spinnerType.getSelectedItemPosition() == 0 && TextUtils.isEmpty(productList.get(i).shop.getBkash())) {
                    Toast.makeText(context, "Sorry, you cann't buy this product using bKash", 0).show();
                } else if (spinnerType.getSelectedItemPosition() != 1 || !TextUtils.isEmpty(ProductListAdapter.this.productList.get(i).shop.getRocket())) {
                    ProductPayment.type = spinnerType.getSelectedItemPosition();
                    ProductPayment.product = productList.get(i);
                    ProductPayment.quantity = Integer.parseInt(editText.getText().toString());
                    ProductPayment.size = editText2.getText().toString();
                    ProductPayment.position = i;
                    ProductListAdapter.this.context.startActivity(new Intent(ProductListAdapter.this.context, PaymentActivity.class));
                    alertDialog.cancel();
                } else {
                    Toast.makeText(ProductListAdapter.this.context, "Sorry, you cann't buy this product using Rocket", 0).show();
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void sendDataToServer(ProgressBar progressBar, Button button, String pid, String quantity, String size, String payment_type, AlertDialog alertDialog) {
        final ProgressBar progressBar2 = progressBar;
        final Button button2 = button;
        progressBar2.setVisibility(View.VISIBLE);
        button2.setVisibility(View.GONE);
        final RequestQueue requestQueue = Volley.newRequestQueue(this.context);
        final AlertDialog alertDialog2 = alertDialog;
        final RequestQueue requestQueue2 = requestQueue;
        final ProgressBar progressBar3 = progressBar;
        final Button button3 = button;
        final String str = pid;
        final String str2 = quantity;
        final String str3 = size;
        final String str4 = payment_type;
        requestQueue.add(new StringRequest(1, APILink.ProductOrderAPI, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    if (new JSONObject(response).get(NotificationCompat.CATEGORY_STATUS).toString().equals("1")) {
                        Toast.makeText(ProductListAdapter.this.context, "Product ordered Successfully", 0).show();
                        alertDialog2.cancel();
                    } else {
                        Toast.makeText(ProductListAdapter.this.context, "Please try again", 0).show();
                    }
                } catch (Exception e) {
                    Context context = ProductListAdapter.this.context;
                    Toast.makeText(context, "" + e.getLocalizedMessage(), 0).show();
                    e.printStackTrace();
                }
                requestQueue2.stop();
                progressBar3.setVisibility(8);
                button3.setVisibility(0);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                progressBar2.setVisibility(8);
                button2.setVisibility(0);
                Toast.makeText(ProductListAdapter.this.context, "Error. Please Try Again", 0).show();
                requestQueue.stop();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                param.put("user_id", APILink.UID);
                param.put("product_id", str);
                param.put("quantity", str2);
                param.put("size", str3);
                param.put("payment_type", str4);
                return param;
            }

            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("Accept", "application/json");
                return header;
            }
        });
    }
}