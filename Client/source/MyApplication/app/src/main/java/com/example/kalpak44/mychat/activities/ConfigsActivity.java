package com.example.kalpak44.mychat.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kalpak44.mychat.R;
import com.example.kalpak44.mychat.constants.Constants;
import com.example.kalpak44.mychat.constants.DefaultConfigs;
import com.example.kalpak44.mychat.constants.UIstrings;
import com.example.kalpak44.mychat.utils.MyChat;
import com.example.kalpak44.mychat.utils.MyService;
import com.example.kalpak44.mychat.utils.Settings;

public class ConfigsActivity extends Activity {
    private EditText serverIpTextEdit;
    private EditText serverPortEditText;
    private EditText rUserListEditText;
    private EditText rMSGEditText;
    private CheckBox checkBox;
    private Settings settings;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configs_layout);
        serverIpTextEdit = (EditText)findViewById(R.id.serverIpTextEdit);
        serverPortEditText = (EditText)findViewById(R.id.serverPortEditText);
        rUserListEditText = (EditText)findViewById(R.id.rUserListEditText);
        rMSGEditText = (EditText)findViewById(R.id.rMSGEditText);
        checkBox = (CheckBox)findViewById(R.id.checkBox);
        MyChat app =(MyChat) getApplicationContext();
        settings = app.getSettings();
        currientConfig();

    }

    private void currientConfig(){
        serverIpTextEdit.setText(settings.getServerIp());
        serverPortEditText.setText(""+settings.getServerPort());
        rUserListEditText.setText("" + settings.getUserListRefreshTime());
        rMSGEditText.setText("" + settings.getMsgListRefreshTime());

        checkBox.setChecked(settings.getAuthDataStatus());
    }


    public void clearHistory(View view){
        getApplicationContext().deleteDatabase(DefaultConfigs.DB_NAME);
        Toast.makeText(getApplicationContext(), UIstrings.CLEAR_DB, Toast.LENGTH_SHORT).show();
    }

    public void restoreDeafaults(View view){
        serverIpTextEdit.setText(DefaultConfigs.SERVERIP);
        serverPortEditText.setText(""+DefaultConfigs.SERVERPORT);
        rUserListEditText.setText(""+DefaultConfigs.USERLIST_UPDATE);
        rMSGEditText.setText("" + DefaultConfigs.MSG_UPDATE);
        checkBox.setChecked(DefaultConfigs.saveAuthData);
        settings.setAuthDataStatus(DefaultConfigs.saveAuthData);
        settings.setServerIp(DefaultConfigs.SERVERIP);
        settings.setServerPort(DefaultConfigs.SERVERPORT);
        settings.setMsgListRefreshTime(DefaultConfigs.MSG_UPDATE);
        settings.setUserListRefreshTime(DefaultConfigs.USERLIST_UPDATE);

    }

    public void saveConfig(View view){
        try {
            String ip = serverIpTextEdit.getText().toString();
            int port = Integer.parseInt(serverPortEditText.getText().toString());
            int uR = Integer.parseInt(rUserListEditText.getText().toString());
            int mR = Integer.parseInt(rMSGEditText.getText().toString());
            boolean s = checkBox.isChecked();
            if(!s)
                clearAuthData();

            settings.setAuthDataStatus(s);
            settings.setMsgListRefreshTime(mR);
            settings.setUserListRefreshTime(uR);
            settings.setServerPort(port);
            settings.setServerIp(ip);
            startService(new Intent(getApplicationContext(), MyService.class)
                    .putExtra(Constants.PARAM_TASK, Constants.PARAM_DISCONNECT));
            Toast.makeText(getApplicationContext(), "Ok", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), UIstrings.INVALID_CONFIG, Toast.LENGTH_SHORT).show();
        }
    }

    public void clearAuthData(){
        settings.setUsername("");
        settings.setPassword("");
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        super.onBackPressed();
    }
}
