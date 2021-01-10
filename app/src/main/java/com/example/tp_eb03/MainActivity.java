package com.example.tp_eb03;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends AppCompatActivity {
    private final static int BT_CONNECT_CODE = 1;
    private final static int PERMISSIONS_REQUEST_CODE= 0;
    private final static String[] BT_DANGEROUS_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    private TextView mstatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyBtRights();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuItem = item.getItemId();
        switch(menuItem){
            case R.id.connect:
                Intent BTConnect = null;
                BTConnect = new Intent(this,BTConnectActivity.class);
                startActivityForResult(BTConnect,BT_CONNECT_CODE);
        }
        return true;
    }

    /**
     * methode de vérification des droits.
     */
    private void verifyBtRights(){
        if(BluetoothAdapter.getDefaultAdapter() ==null){
            Toast.makeText(this,"Cette application nécessite un adaptateur BT",Toast.LENGTH_LONG).show(); // ca affiche un message sur la fenetre principale.
            finish();
            return;
        }
        if (SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED){
            requestPermissions(BT_DANGEROUS_PERMISSIONS,PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSIONS_REQUEST_CODE  ){
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                Toast.makeText(this,"les autorisations sont requises pour utiliser l'application", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BT_CONNECT_CODE:
                if (resultCode == RESULT_OK) {
                    String address= data.getStringExtra("device");
                    mstatus.setText(address);
                }
                break;
            default:
                
        }
    }
}