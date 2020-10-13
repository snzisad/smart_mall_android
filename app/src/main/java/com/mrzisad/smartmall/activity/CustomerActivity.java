package com.mrzisad.smartmall.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.mrzisad.smartmall.R;
import com.mrzisad.smartmall.adapter.ProductListAdapter;
import com.mrzisad.smartmall.model.Product;
import com.mrzisad.smartmall.utils.APILink;
import com.mrzisad.smartmall.utils.DataContainer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public ProductListAdapter adapter;
    private LinearLayout layout_MallList;
    public List<String> mallID;
    public List<String> mallName;
    public List<Product> productList;
    public List<Product> productOrder;
    public ProgressDialog progressDialog;
    Button btnConfirmOrder, btnCancelOrder;
    ListView listView;
    NavigationView nav_view;
    DrawerLayout drawer_layout;


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);
        this.mallName = new ArrayList();
        this.mallID = new ArrayList();
        View findViewById = findViewById(R.id.layout_MallList);
        this.layout_MallList = (LinearLayout) findViewById;
        this.productList = new ArrayList();
        this.productOrder = new ArrayList();
        Context context = this;
        listView = (ListView) findViewById(R.id.lvProductList);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        nav_view = findViewById(R.id.nav_view);
        drawer_layout = findViewById(R.id.drawer_layout);

        adapter = new ProductListAdapter(context, productList, 0, listView, btnConfirmOrder, btnCancelOrder);
        listView.setAdapter(adapter);

        getRecentProductFromServer();
        getMallListFromServer();

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer_layout, (Toolbar) findViewById(R.id.toolbar), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_layout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorLight));
        nav_view.setNavigationItemSelectedListener(this);

        ((Button) findViewById(R.id.btnConfirmOrder)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CustomerActivity.this, BasketShoping.class));
            }
        });

        ((Button) findViewById(R.id.btnCancelOrder)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataContainer.products = null;
                btnCancelOrder.setVisibility(8);
                btnConfirmOrder.setVisibility(8);
                listView.setPadding(0, 0, 0, 0);
            }
        });
    }

    private final void getMallListFromServer() {
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(0, APILink.ShopingMallListAPI, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.cancel();
                try {
                    if (response.getInt(NotificationCompat.CATEGORY_STATUS) == 1) {
                        JSONArray data = response.getJSONArray("response");
                        mallName.clear();
                        mallID.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject chield = data.getJSONObject(i);
                            mallName.add(chield.get("name").toString());
                            mallID.add(chield.get("id").toString());
                        }
                        addDataInLayout();
                        AddNavigationMenuItem(nav_view);
                        return;
                    }
                    Toast.makeText(CustomerActivity.this, "Sorry, please try again", 1).show();
                } catch (Exception e) {
                    Toast.makeText(CustomerActivity.this, ""+e.getLocalizedMessage(), 1).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    /* access modifiers changed from: private */
    public final void addDataInLayout() {
        layout_MallList.removeAllViews();
        for (int i = 0; i < mallName.size(); i++) {
            View v = getLayoutInflater().inflate(R.layout.layout_mall_list, (ViewGroup) null);
            Button btnMallName = (Button) v.findViewById(R.id.btnMallName);
            btnMallName.setText(mallName.get(i));
            final int position = i;
            btnMallName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    APILink.MID = (String) mallID.get(position);
                    startActivity(new Intent(CustomerActivity.this, ShopListActivity.class));
                }
            });
            layout_MallList.addView(v);
        }
        progressDialog.cancel();
    }

    private final void getRecentProductFromServer() {
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(0, APILink.RecentProductAPI, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.cancel();
                Log.e("Response", response.toString());
                try {
                    if (response.getInt(NotificationCompat.CATEGORY_STATUS) == 1) {
                        JSONArray data = response.getJSONArray("response");
                        int length = data.length();
                        productList.clear();
                        for (int i = 0; i < length; i++) {
                            JSONObject chield = data.getJSONObject(i);
                            Product product = new Gson().fromJson(chield.toString(), Product.class);
                            productList.add(product);
                        }
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    Toast.makeText(CustomerActivity.this, "Sorry, please try again", 1).show();
                } catch (Exception e) {
                    Toast.makeText(CustomerActivity.this, ""+e.getLocalizedMessage(), 1).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(CustomerActivity.this, ""+error.getLocalizedMessage(), 1).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public boolean onNavigationItemSelected(MenuItem p0) {
        APILink.MID = mallID.get(p0.getItemId());
        startActivity(new Intent(this, ShopListActivity.class));
        drawer_layout.closeDrawer((int) GravityCompat.START);
        return true;
    }

    public void onBackPressed() {
        if (drawer_layout.isDrawerOpen((int) GravityCompat.START)) {
            drawer_layout.closeDrawer((int) GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.logout) {
            return super.onOptionsItemSelected(item);
        }
        getSharedPreferences("Status", 0).edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        return true;
    }

    /* access modifiers changed from: private */
    public final void AddNavigationMenuItem(NavigationView navView) {
        MenuItem findItem = navView.getMenu().findItem(R.id.navItemShopingMall);
        findItem.getSubMenu().clear();
        List<String> list = this.mallName;
        for (int i = 0; i < mallName.size(); i++) {
            MenuItem findItem2 = navView.getMenu().findItem(R.id.navItemShopingMall);
            SubMenu subMenu = findItem2.getSubMenu();
            subMenu.add(R.id.navItemShopingMall, i, 0, mallName.get(i));
        }
    }
}
