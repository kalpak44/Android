package com.example.kalpak44.mychat.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.kalpak44.mychat.R;
import com.example.kalpak44.mychat.constants.Constants;
import com.example.kalpak44.mychat.utils.MyService;

/**
 * Created by kalpak44 on 15-7-29.
 */
public class RegistrationActivity extends Activity {
    BroadcastReceiver br;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);


/*
        br = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra(Constants.PARAM_STATUS);
                Log.i(Constants.LOG_TAG, "onReceive: status = " + status);
            }
        };

        // создаем фильтр для BroadcastReceiver
        IntentFilter intFilt = new IntentFilter(Constants.BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(br, intFilt);
        */
    }


    public void onClickRegister(View view){
        Intent intent = new Intent(getApplicationContext(), MyService.class)
                .putExtra(Constants.PARAM_TASK, "reg")
                .putExtra("username", "admin")
                .putExtra("password", "admin");
        // стартуем сервис
        startService(intent);


        //Intent intent = new Intent(this, UserListActivity.class);
        //startActivity(intent);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
