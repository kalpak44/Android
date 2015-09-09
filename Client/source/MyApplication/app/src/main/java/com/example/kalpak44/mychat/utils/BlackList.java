package com.example.kalpak44.mychat.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.kalpak44.mychat.constants.Constants;
import com.example.kalpak44.mychat.constants.DefaultConfigs;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by kalpak44 on 15-8-31.
 */
public class BlackList {
    private SharedPreferences settings;
    private JSONArray json;

    public BlackList(Context context){
        settings = context.getSharedPreferences(DefaultConfigs.CONFIG_FILE, Context.MODE_PRIVATE);
        this.getJSON();
    }

    public void addToBlackList(String name){
        if(json!=null && !inList(name)){
            json.put(name);
            setJSONArray();
        }else{
            json = new JSONArray();
            json.put(name);
            setJSONArray();
        }
    }

    public void removeFromList(String name){
        JSONArray newArr = new JSONArray();
        for (int i = 0; i < json.length(); i++) {

            try {
                String currient = json.getString(i);
                if(!currient.equals(name)){
                    newArr.put(currient);
                }
            } catch (JSONException e) {

            }
        }
        json = newArr;
        setJSONArray();
    }

    public boolean inList(String name){
        if(json==null)
            return false;
        for (int i = 0; i < json.length(); i++) {
            try {
                String currient = json.getString(i);
                if(currient.equals(name)){
                    return true;
                }
            } catch (JSONException e) {}
        }
        return false;
    }



    private void getJSON() {
        try {
            String favListJsonArray = settings.getString(Constants.BLACK_LIST,"");
            json = new JSONArray(favListJsonArray);
        }catch (JSONException e) {
            json =  null;
        }
    }
    private void setJSONArray() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.BLACK_LIST, json.toString());
        editor.commit();
    }

}
