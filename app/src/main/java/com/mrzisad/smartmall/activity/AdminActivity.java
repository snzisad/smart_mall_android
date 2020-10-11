package com.mrzisad.smartmall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.mrzisad.smartmall.R;

import java.util.HashMap;

public class AdminActivity extends AppCompatActivity {


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        super.setTitle("Admin Panel");

        ((Button) findViewById(R.id.btnAddCategory)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, CategoryAddActivity.class));
            }
        });
        ((Button) findViewById(R.id.btnUserInfo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, UserInformationActivity.class));
            }
        });
        ((Button) findViewById(R.id.btnAddShopingMall)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, AddShopingMallActivity.class));
            }
        });
        ((Button) findViewById(R.id.btnChangePassword)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, PasswordChangeActivity.class));
            }
        });
        ((Button) findViewById(R.id.btnShopkeeperVerification)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, ShopkeeperRequestActivity.class));
            }
        });
        ((Button) findViewById(R.id.btnAddOffer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, NewOfferActivity.class));
            }
        });

        ((Button) findViewById(R.id.btnAddOffer)).setVisibility(View.GONE);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() !=R.id.logout) {
            return super.onOptionsItemSelected(item);
        }
        getSharedPreferences("Status", 0).edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        return true;
    }
}

