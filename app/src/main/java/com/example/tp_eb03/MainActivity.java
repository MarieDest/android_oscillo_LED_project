package com.example.tp_eb03;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
private final static int BT_CONNECT_CODE = 1;
    private Intent NTConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                NTConnect = new Intent(this,BTConnectActivity.class);
                startActivityForResult(BTConnect,BT_CONNECT_CODE);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BT_CONNECT_CODE:
                if (resultCode == RESULT_OK) {

                }
                break;
            default:
                
        }
    }
}