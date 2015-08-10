package com.example.kalpak44.mychat.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kalpak44.mychat.constants.Constants;
import com.example.kalpak44.mychat.constants.Strings;
import com.example.kalpak44.mychat.utils.MyService;
import com.example.kalpak44.mychat.R;


public class MainActivity extends Activity {

    private TextView textViewInvalidData;
    private EditText editUsername;
    private EditText editPassword;
    private Button loginButton;
    private Button goToRegButton;





    BroadcastReceiver br1,br2;
    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        textViewInvalidData = (TextView) findViewById(R.id.textViewInvalidData);
        editUsername = (EditText)findViewById(R.id.username);
        editPassword = (EditText)findViewById(R.id.password1);
        loginButton = (Button)findViewById(R.id.loginButton);
        goToRegButton = (Button)findViewById(R.id.goToRegButton);

        loginButton.setEnabled(false);
        goToRegButton.setEnabled(false);




        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewInvalidData.setText(Strings.WAIT);
                String username = editUsername.getText().toString();
                String password = editPassword.getText().toString();


                // стартуем сервис
                startService(new Intent(getApplicationContext(), MyService.class)
                        .putExtra(Constants.PARAM_TASK, Constants.PARAM_AUTH)
                        .putExtra(Constants.USERNAME, username)
                        .putExtra(Constants.PASSWORD, password));


            }
        });


        //for test
        editUsername.setText("admin");
        editPassword.setText("qwerty123");


        startService(new Intent(getApplicationContext(), MyService.class));
        initConnection();


    }


    private void initConnection() {
        Intent intent = new Intent(getApplicationContext(), MyService.class)
                .putExtra(Constants.PARAM_TASK, Constants.PARAM_CONNECT);
        // стартуем сервис
        startService(intent);



        br1 = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {

                String conn_status = intent.getStringExtra(Constants.PARAM_CONNECTION_RESULT);
                Log.i(Constants.LOG_TAG, "onReceive1: param = " + Constants.PARAM_CONNECT);


                if (conn_status.equals(Constants.STATUS_SUCCESS)){
                    Log.i(Constants.LOG_TAG, "enable controls");
                    textViewInvalidData.setText("");
                    loginButton.setEnabled(true);
                    goToRegButton.setEnabled(true);

                    unregisterReceiver(br1);




                    br2 = new BroadcastReceiver(){
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            String status = intent.getStringExtra(Constants.PARAM_AUTH_RESULT);
                            Log.i(Constants.LOG_TAG, "onReceive2: status = " + status);

                            if(status.equals(Constants.STATUS_SUCCESS)){
                                //textViewInvalidData.setText("");
                                startActivity(new Intent(getApplicationContext(),UserListActivity.class));
                            }if(status.equals(Constants.STATUS_FAIL)){
                                Toast.makeText(getApplicationContext(), Strings.AUTH_FAIL, Toast.LENGTH_SHORT).show();
                            }
                            unregisterReceiver(br2);
                        }
                    };

                    // регистрируем (включаем) BroadcastReceiver
                    registerReceiver(br2, new IntentFilter(Constants.BROADCAST_ACTION));






                }else {
                    textViewInvalidData.setText(conn_status);
                }
            }
        };



        // создаем фильтр для BroadcastReceiver
        IntentFilter intFilt = new IntentFilter(Constants.BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(br1, intFilt);

    }



    @Override
    protected void onPause() {
        Log.i(Constants.LOG_TAG, "onPause: param unregister BroadcastReceivers");

        try {
            if (br1 != null) {
                unregisterReceiver(br1);
            }
            if (br2 != null) {
                unregisterReceiver(br2);
            }
        } catch (IllegalArgumentException e) {
            br1 = null;
            br2 = null;
        }


        super.onPause();
    }

    public void goToRegister(View view){
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
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
