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
import com.example.kalpak44.mychat.utils.Client;
import com.example.kalpak44.mychat.utils.MyService;
import com.example.kalpak44.mychat.R;


public class MainActivity extends Activity {

    private TextView textViewInvalidData;
    private EditText editUsername;
    private EditText editPassword;
    private Button loginButton;
    private Button goToRegButton;
    private Menu menu;



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
                Log.i(Constants.LOG_TAG, "onReceive conn: param = " + Constants.PARAM_CONNECT);


                if (conn_status != null) {
                    if (conn_status.equals(Constants.STATUS_SUCCESS)) {
                        Log.i(Constants.LOG_TAG, "enable controls");
                        textViewInvalidData.setText("");
                        loginButton.setEnabled(true);
                        goToRegButton.setEnabled(true);

                        unregisterReceiver(br1);


                        br2 = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                String auth_status = intent.getStringExtra(Constants.PARAM_AUTH_RESULT);

                                if (auth_status != null) {
                                    Log.i(Constants.LOG_TAG, "onReceive auth: status = " + auth_status);
                                    if (auth_status.equals(Constants.SERVER_STATUS_AUTH_SUCCESS)) {
                                        //textViewInvalidData.setText("");
                                        MyService.is_authorized = true;
                                        startActivity(new Intent(getApplicationContext(), UserListActivity.class));
                                    } else {
                                        Toast.makeText(getApplicationContext(), Strings.AUTH_FAIL, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                try {
                                    if (br2 != null) {
                                        unregisterReceiver(br2);
                                    }
                                } catch (IllegalArgumentException e) {
                                    br2 = null;
                                }
                            }
                        };

                        // регистрируем (включаем) BroadcastReceiver
                        registerReceiver(br2, new IntentFilter(Constants.BROADCAST_ACTION));
                        Log.i(Constants.LOG_TAG, "initConnection: param register br2");


                    } else {
                        textViewInvalidData.setText(conn_status);
                        startService( new Intent(getApplicationContext(), MyService.class)
                                .putExtra(Constants.PARAM_TASK, Constants.PARAM_DISCONNECT));
                        MenuItem mi = menu.findItem(R.id.action_disconnect);
                        mi.setTitle("Connect");
                        try {
                            if (br2 != null) {
                                unregisterReceiver(br2);
                            }
                        } catch (IllegalArgumentException e) {
                            br2 = null;
                        }
                    }

                }

                try {
                    if (br1 != null) {
                        unregisterReceiver(br1);
                    }
                } catch (IllegalArgumentException e) {
                    br1 = null;
                }


            }
        };



        // создаем фильтр для BroadcastReceiver
        IntentFilter intFilt = new IntentFilter(Constants.BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(br1, intFilt);
        Log.i(Constants.LOG_TAG, "onPause: param register br1");

    }



    public void goToRegister(View view){
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        return;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        MenuItem mittem = menu.findItem(id);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.i(Constants.LOG_TAG, "menu: settings");
            return true;
        }
        if (id == R.id.action_disconnect) {
            String mittemTitle = mittem.getTitle().toString();
            Log.i(Constants.LOG_TAG, "menu: "+ mittemTitle);
            if(mittemTitle.equals("Disconnect")){
                loginButton.setEnabled(false);
                goToRegButton.setEnabled(false);

                Intent intent = new Intent(getApplicationContext(), MyService.class)
                        .putExtra(Constants.PARAM_TASK, Constants.PARAM_DISCONNECT);
                // стартуем сервис
                startService(intent);



                mittem.setTitle("Connect");


            }if (mittemTitle.equals("Connect")){
                mittem.setTitle("Disconnect");
                Intent intent = getIntent();
                finish();
                startActivity(intent);

            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
