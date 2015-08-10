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
import android.widget.EditText;
import android.widget.Toast;

import com.example.kalpak44.mychat.R;
import com.example.kalpak44.mychat.constants.Constants;
import com.example.kalpak44.mychat.constants.Strings;
import com.example.kalpak44.mychat.utils.MyService;

/**
 * Created by kalpak44 on 15-7-29.
 */
public class RegistrationActivity extends Activity {
    BroadcastReceiver br3;
    EditText username;
    EditText password1;
    EditText password2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);
        username = (EditText)findViewById(R.id.username);
        password1 = (EditText)findViewById(R.id.password1);
        password2 = (EditText)findViewById(R.id.password2);

        username.setText("user");
        password1.setText("12345");
        password2.setText("12345");




        br3 = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra(Constants.PARAM_REG_RESULT);

                if(status != null){
                    Log.i(Constants.LOG_TAG, "onReceive auth: status = " + status);
                    if(status.equals(Constants.SERVER_STATUS_REG_SUCCESS)){
                        //textViewInvalidData.setText("");
                        unregisterReceiver(br3);
                        startActivity(new Intent(getApplicationContext(),UserListActivity.class));
                    }else{
                        Toast.makeText(getApplicationContext(), Strings.REG_FAIL, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    try {
                        if (br3 != null) {
                            unregisterReceiver(br3);
                        }
                    } catch (IllegalArgumentException e) {
                        br3 = null;
                    }
                }

            }
        };

        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(br3, new IntentFilter(Constants.BROADCAST_ACTION));

    }


    public void onClickRegister(View view){

        String user, pass1, pass2;
        user  = username.getText().toString();
        pass1 = password1.getText().toString();
        pass2 = password2.getText().toString();
        if(pass1.equals(pass2)){
            Intent intent = new Intent(getApplicationContext(), MyService.class)
                    .putExtra(Constants.PARAM_TASK, Constants.PARAM_REG)
                    .putExtra(Constants.USERNAME, user)
                    .putExtra(Constants.PASSWORD, pass1);
            // стартуем сервис
            startService(intent);
        }else{
            Toast.makeText(getApplicationContext(), Strings.PASS_MIS, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onPause() {
        Log.i(Constants.LOG_TAG, "onPause: param unregister BroadcastReceivers");

        try {
            if (br3 != null) {
                Log.i(Constants.LOG_TAG, "onPause: unregister b3");
                unregisterReceiver(br3);
            }
        } catch (IllegalArgumentException e) {
            br3 = null;
        }

        super.onPause();
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
