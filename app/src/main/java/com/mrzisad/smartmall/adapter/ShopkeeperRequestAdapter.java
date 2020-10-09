package com.mrzisad.smartmall.adapter;


import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mrzisad.smartmall.R;
import com.mrzisad.smartmall.model.User;
import com.mrzisad.smartmall.utils.APILink;

import org.json.JSONObject;

import java.util.List;

public class ShopkeeperRequestAdapter extends BaseAdapter {
    Context context;
    List<User> userList;

    public ShopkeeperRequestAdapter(Context context2, List<User> user) {
        this.context = context2;
        this.userList = user;
    }

    public int getCount() {
        return this.userList.size();
    }

    public Object getItem(int position) {
        return this.userList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(this.context, R.layout.layout_shopkeeper_list, (ViewGroup) null);
        final Button btnAccept = (Button) v.findViewById(R.id.btnAccept);
        User user = this.userList.get(position);
        ((TextView) v.findViewById(R.id.tvName)).setText("Name: " + user.getName());
        ((TextView) v.findViewById(R.id.tvShopName)).setText("Shop Name: " + user.getShopname());
        ((TextView) v.findViewById(R.id.tvShopNumber)).setText("Shop Number: " + user.getShopno());
        ((TextView) v.findViewById(R.id.tvShopingMall)).setText("Shoping Mall: " + user.getShopingmall().getName());
        ((TextView) v.findViewById(R.id.tvShopCategory)).setText("Shop Category: " + user.getCategory().getName());
        final String uid = user.getPhone();
        final ProgressDialog progressDialog = new ProgressDialog(this.context);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShopkeeperRequestAdapter.this.acceptShopkeper(progressDialog, uid, btnAccept);
            }
        });
        return v;
    }

    /* access modifiers changed from: private */
    public void acceptShopkeper(final ProgressDialog progressBar, String uid, final Button btn) {
        progressBar.show();
        final RequestQueue requestQueue = Volley.newRequestQueue(this.context);
        requestQueue.add(new JsonObjectRequest(0, APILink.AcceptShopkeeperAPI + uid, (JSONObject) null, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                try {
                    if (response.get("response").toString().equals("1")) {
                        btn.setText("Accepted");
                        btn.setClickable(false);
                        btn.setBackgroundColor(ShopkeeperRequestAdapter.this.context.getResources().getColor(R.color.colorAsh));
                        Toast.makeText(ShopkeeperRequestAdapter.this.context, "Shopkeeper Accepted", 0).show();
                    } else {
                        Toast.makeText(ShopkeeperRequestAdapter.this.context, "Error Please try again", 0).show();
                    }
                } catch (Exception e) {
                    Context context = ShopkeeperRequestAdapter.this.context;
                    Toast.makeText(context, "" + e.getLocalizedMessage(), 0).show();
                    e.printStackTrace();
                }
                requestQueue.stop();
                progressBar.cancel();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                progressBar.cancel();
                Toast.makeText(ShopkeeperRequestAdapter.this.context, "Error. Please Try Again", 0).show();
                requestQueue.stop();
            }
        }));
    }
}
