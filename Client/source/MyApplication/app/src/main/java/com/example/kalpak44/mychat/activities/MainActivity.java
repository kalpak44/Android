package com.example.kalpak44.mychat.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import com.example.kalpak44.mychat.utils.Client;


public class MainActivity extends Activity {

    private TextView textViewInvalidData;
    private EditText editUsername;
    private EditText editPassword;
    private Button loginButton;
    private Button registration;





    BroadcastReceiver br;
    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        textViewInvalidData = (TextView) findViewById(R.id.textViewInvalidData);
        editUsername = (EditText)findViewById(R.id.username);
        editPassword = (EditText)findViewById(R.id.password1);
        loginButton = (Button)findViewById(R.id.register);


        //for test
        editUsername.setText("admin");
        editPassword.setText("qwerty123");


        startService();
        loadAfterLogInClick();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //textViewInvalidData.setText(Strings.WAIT);
                String username = editUsername.getText().toString();
                String password = editPassword.getText().toString();

                Intent intent = new Intent(getApplicationContext(), MyService.class)
                        .putExtra(Constants.PARAM_TASK, Constants.PARAM_AUTH)
                        .putExtra(Constants.USERNAME, username)
                        .putExtra(Constants.PASSWORD, password);
                // стартуем сервис
                startService(intent);
            }
        });


    }

    private void initApp() {
        /*
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("key", "value");
        ed.commit();

        Log.i(Constants.LOG_TAG, sPref.getString("key", ""));
        */
    }

    public void startService(){
        startService(new Intent(getApplicationContext(), MyService.class));
    }

    public void loadAfterLogInClick(){
        br = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra(Constants.PARAM_RESULT);
                Log.i(Constants.LOG_TAG, "onReceive: status = " + status);
                if(status.equals(Constants.STATUS_SUCCESS)){
                    //textViewInvalidData.setText("");
                    startActivity(new Intent(getApplicationContext(),UserListActivity.class));
                }if(status.equals("FAIL")){
                    Toast.makeText(getApplicationContext(), Strings.AUTH_FAIL, Toast.LENGTH_SHORT).show();
                }
            }
        };

        // создаем фильтр для BroadcastReceiver
        IntentFilter intFilt = new IntentFilter(Constants.BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(br, intFilt);
    }











    public void goToRegister(View view){
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
