package com.example.kalpak44.mychat.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.kalpak44.mychat.constants.Constants;
import com.example.kalpak44.mychat.constants.DefaultConfigs;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by kalpak44 on 15-8-24.
 */
public class Settings {
    private SharedPreferences settings;
    public Settings(Context context){
        settings = context.getSharedPreferences(DefaultConfigs.CONFIG_FILE, Context.MODE_PRIVATE);
    }

    public void setUsername(String username) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.USERNAME, username);
        editor.commit();
    }

    public void setPassword(String password) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.PASSWORD, password);
        editor.commit();
    }

    public void setAuthDataStatus(boolean s) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(Constants.SAVE_AUTH_DATA, s);
        editor.commit();
    }

    public void setServerIp(String ip) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.SERVER_IP, ip);
        editor.commit();
    }

    public void setServerPort(int port) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.SERVER_PORT, port);
        editor.commit();
    }
    public void setUserListRefreshTime(int ms) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.U_REFRESH, ms);
        editor.commit();
    }

    public void setMsgListRefreshTime(int ms) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.MSG_REFRESH, ms);
        editor.commit();
    }





    public String getUsername() {
        return settings.getString(Constants.USERNAME,"");
    }
    public String getPassword() {
        return settings.getString(Constants.PASSWORD,"");
    }

    public boolean getAuthDataStatus() {
        return settings.getBoolean(Constants.SAVE_AUTH_DATA, DefaultConfigs.saveAuthData);
    }

    public String getServerIp() {
        return settings.getString(Constants.SERVER_IP, DefaultConfigs.SERVERIP);
    }



    public int getServerPort() {
        return settings.getInt(Constants.SERVER_PORT, DefaultConfigs.SERVERPORT);
    }
    public int getUserListRefreshTime() {
        return settings.getInt(Constants.U_REFRESH, DefaultConfigs.USERLIST_UPDATE);
    }
    public int getMsgListRefreshTime() {
        return settings.getInt(Constants.MSG_REFRESH, DefaultConfigs.MSG_UPDATE);
    }






    public boolean hasSettings() {
        // We just check if a username has been set
        return (!settings.getString(Constants.SERVER_IP, "").equals(""));
    }





}
