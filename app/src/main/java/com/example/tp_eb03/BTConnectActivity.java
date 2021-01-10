package com.example.tp_eb03;

import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Set;

public class BTConnectActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Toolbar mToolbar;
    private Button mScan;
    private ListView mPairedList;
    private ListView mDiscoveredList;
    private BroadcastReceiver mBroadcastReceiver;
    private ArrayAdapter<String> mPairedAdapter;
    private ArrayAdapter<String> mDiscoveredAdapter;
    private ProgressBar mProgressBar;
    private BluetoothAdapter mBluetoothAdapter;
    private int BT_ACTIVATION_REQUEST_CODE = 0;
    private boolean mBroadcastRegistered = false;
    private int RNE2_COD = 0x1F00;

    private enum Action {START,STOP}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_t_connect);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mProgressBar =  findViewById(R.id.progress);
        mPairedList = findViewById(R.id.paired);
        mDiscoveredList = findViewById(R.id.discovered);
        mScan = findViewById(R.id.scan_button);
        mPairedList.setOnItemClickListener(this);
        mDiscoveredList.setOnItemClickListener(this);
        mPairedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mDiscoveredAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        mPairedList.setAdapter(mPairedAdapter);
        mDiscoveredList.setAdapter(mDiscoveredAdapter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if(pairedDevices.size()>0){
            for (BluetoothDevice pairedDevice:pairedDevices){
                mPairedAdapter.add(pairedDevice.getName()+"\n"+pairedDevice.getAddress());
            }
        }else{
            mPairedAdapter.add("pas de périphérique appairé");
        }

        mBroadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        if (mDiscoveredAdapter.getCount() == 0) {
                            mDiscoveredAdapter.add("aucun périphérique trouvé");
                        }
                        mScan.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.INVISIBLE);
                        break;
                    case BluetoothDevice.ACTION_FOUND:
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {//&& (device.getBluetoothClass().getDeviceClass() == RNE2_COD
                            mDiscoveredAdapter.add(device.getName() + "\n" + device.getAddress());

                        }
                        break;
                }
            }
        };
    }

   @Override
    protected void onPause() {
        super.onPause();
        if(mBluetoothAdapter != null){
            mBluetoothAdapter.cancelDiscovery();
        }
   /*     if(mBroadcastReceiver != null){
            unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }*/
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent() ;
        mBluetoothAdapter.cancelDiscovery();
        String info = ((TextView)view).getText().toString();
        if(info.equals("aucun périphérique trouvé")|| info.equals("pas de périphérique appairé")){ // chaine de caractère à vérifier
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        // lecture du nom du device : récupération des 17 derniers caractères
        if (info.length()>17){
            info = info.substring(info.length()-17);
            intent.putExtra("device", info);
            setResult(RESULT_OK,intent);
            finish();
            return;
        }

    }

    @Override
    public void onClick(View view) {
        switch( view.getId()){
            case R.id.scan_button:
                if(!mBluetoothAdapter.isEnabled()){
                    Intent BTActivation;
                    BTActivation =  new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(BTActivation, BT_ACTIVATION_REQUEST_CODE);
                    return;
                }
                toggleBtScan();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    toggleBtScan();
                } else {
                    Toast.makeText(this, "le BT doit être activé", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    private void toggleBtScan(){
        if(mScan.getText().equals("Scanner")){
            btScan(Action.START);
            mProgressBar.setVisibility(View.VISIBLE);
            mScan.setText("Annuler");
        }else {
            btScan(Action.STOP);
            mProgressBar.setVisibility(View.INVISIBLE);
            mScan.setText("Scanner");
        }
    }
    private void btScan(Action startStop){
        if (startStop == Action.START){
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(mBroadcastReceiver,filter);
            filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver,filter);
            mBroadcastRegistered = true;
            mBluetoothAdapter.startDiscovery();
        }else{
            unregisterReceiver(mBroadcastReceiver);
            mBroadcastRegistered = false;
            mBluetoothAdapter.cancelDiscovery();
        }
    }

}
