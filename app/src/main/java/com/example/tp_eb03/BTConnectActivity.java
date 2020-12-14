package com.example.tp_eb03;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class BTConnectActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Toolbar mToolbar;
    private Button mScan;
    private ListView mPairedList;
    private ListView mDiscoveredList;
    private BroadcastReceiver mBroadcastReceiver;
    private ArrayAdapter<String> mPairedAdapter;
    private ArrayAdapter<String> mDiscoveredAdapter;
    private ProgressBar mProgressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_t_connect);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mProgressBar =  findViewById(R.id.progress);
        mPairedList = findViewById(R.id.paired);
        mDiscoveredList = findViewById(R.id.discovered);
        mScan = findViewById(R.id.scan);
        mPairedList.setOnItemClickListener(this);
        mDiscoveredList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onClick(View v) {

    }
}
