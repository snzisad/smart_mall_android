package com.mrzisad.smartmall.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mrzisad.smartmall.R;

public class HelpActivity extends AppCompatActivity {

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_help);
        super.setTitle("Help Desk");
    }
}
