package com.example.kalpak44.mychat.utils;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;


import com.example.kalpak44.mychat.constants.Config;
import com.example.kalpak44.mychat.constants.Constants;
import com.example.kalpak44.mychat.constants.Strings;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;



/**
 * Created by kalpak44 on 15-8-7.
 */
public class MyService extends Service {
    Client client;
    public static boolean is_authorized = false;

    @Override
    public void onCreate() {
        Log.i(Constants.LOG_TAG, "onCreate SERVICE");
        super.onCreate();
        connection();
        Log.i(Constants.LOG_TAG, "onCreate connected to server");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String task = intent.getStringExtra(Constants.PARAM_TASK);
        if(task != null){
            Log.i(Constants.LOG_TAG, "onStartCommand task "+task);
            MyRun mr = new MyRun(startId, task, intent);
            new Thread(mr).start();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(Constants.LOG_TAG, "onDestroy SERVICE");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public class MyRun implements Runnable{
        int startId;
        String task;
        Intent intent;
        MyRun(int startId, String task, Intent intent){
            this.startId = startId;
            this.task = task;
            this.intent = intent;
        }

        @Override
        public void run() {
            try{


                Log.i(Constants.LOG_TAG,"myRun");
                if(task.equals(Constants.PARAM_CONNECT)){
                    Thread.sleep(Config.CONNECTION_WAIT);
                    Log.i(Constants.LOG_TAG, "connection status: " + client.getConnctionStatus());
                    Intent resultIntent = new Intent(Constants.BROADCAST_ACTION);
                    resultIntent.putExtra(Constants.PARAM_CONNECTION_RESULT, client.getConnctionStatus());
                    sendBroadcast(resultIntent);

                }if(task.equals(Constants.PARAM_AUTH)){
                    Log.i(Constants.LOG_TAG,getMessage());
                    Log.i(Constants.LOG_TAG, task);
                    sendMessage(task);
                    Log.i(Constants.LOG_TAG, getMessage());
                    JSONObject user = new JSONObject();

                    user.putOpt(Constants.USERNAME, intent.getStringExtra(Constants.USERNAME));
                    user.putOpt(Constants.PASSWORD, intent.getStringExtra(Constants.PASSWORD));
                    sendMessage(user.toString());

                    String authStatus = getMessage();
                    Log.i(Constants.LOG_TAG, "TCP MSG " + authStatus);
                    Intent resultIntent = new Intent(Constants.BROADCAST_ACTION);
                    resultIntent.putExtra(Constants.PARAM_AUTH_RESULT, authStatus);
                    sendBroadcast(resultIntent);
                }if(task.equals(Constants.PARAM_REG)){
                    Log.i(Constants.LOG_TAG,getMessage());
                    Log.i(Constants.LOG_TAG, task);
                    sendMessage(task);
                    JSONObject user = new JSONObject();
                    user.putOpt(Constants.USERNAME, intent.getStringExtra(Constants.USERNAME));
                    user.putOpt(Constants.PASSWORD, intent.getStringExtra(Constants.PASSWORD));
                    user.putOpt("avatar", "");

                    Log.i(Constants.LOG_TAG, getMessage());
                    sendMessage(user.toString());
                    String regStatus = getMessage();
                    Log.i(Constants.LOG_TAG, regStatus);
                    Intent resultIntent = new Intent(Constants.BROADCAST_ACTION);
                    resultIntent.putExtra(Constants.PARAM_REG_RESULT, regStatus);
                    sendBroadcast(resultIntent);
                }if (task.equals(Constants.PARAM_DISCONNECT)){
                    stop();
                }if (task.equals(Constants.PARAM_LOGOUT)){
                    sendMessage(Constants.PARAM_LOGOUT);
                    Log.i(Constants.LOG_TAG, "TCP MSG " + getMessage());
                }if(task.equals(Constants.PARAM_USERLIST)){
                    Log.i(Constants.LOG_TAG,getMessage());
                    sendMessage(Constants.PARAM_USERLIST);
                    String recv = getMessage();
                    Log.i(Constants.LOG_TAG, "TCP MSG " + recv);
                    Intent resultIntent = new Intent(Constants.BROADCAST_ACTION);
                    resultIntent.putExtra(Constants.PARAM_USERLIST_RESULT, recv);
                    sendBroadcast(resultIntent);
                }if(task.equals(Constants.PARAM_SENDMSG)){
                    Log.i(Constants.LOG_TAG,getMessage());
                    sendMessage(Constants.PARAM_SENDMSG);
                    Log.i(Constants.LOG_TAG,getMessage());
                    JSONObject msg = new JSONObject();
                    msg.putOpt(Constants.TO, intent.getStringExtra(Constants.TO));
                    msg.putOpt(Constants.MSG_TEXT, intent.getStringExtra(Constants.MSG_TEXT));
                    sendMessage(msg.toString());

                    String recv = getMessage();
                    Log.i(Constants.LOG_TAG, "TCP MSG " + recv);
                    Intent resultIntent = new Intent(Constants.BROADCAST_ACTION);
                    resultIntent.putExtra(Constants.PARAM_SENDING_RESULT, recv);
                    sendBroadcast(resultIntent);

                }if(task.equals(Constants.PARAM_GETMSG)){
                    Log.i(Constants.LOG_TAG, getMessage());
                    Log.i(Constants.LOG_TAG, "TCP SEND: "+Constants.PARAM_GETMSG);
                    sendMessage(Constants.PARAM_GETMSG);
                    Log.i(Constants.LOG_TAG, getMessage());
                    String receiver = intent.getStringExtra(Constants.RECEIVER);
                    sendMessage(new JSONObject().put(Constants.FROM,receiver).toString());


                    String recv = getMessage();
                    Log.i(Constants.LOG_TAG, "TCP MSG " + recv);
                    Intent resultIntent = new Intent(Constants.BROADCAST_ACTION);
                    resultIntent.putExtra(Constants.PARAM_RECV_MSG_RESULT, recv);
                    sendBroadcast(resultIntent);

                }


                //checkProfile(intent.getStringExtra("username"), intent.getStringExtra("password"));









            }catch (Exception e){
                Log.i(Constants.LOG_TAG, "MYRUN Exception " + e.getMessage());
            }
        }

        public void stop(){
            stopSelf(startId);
        }

    }



    public void connection(){
        client = new Client();
        new Thread(client).start();
    }

    public void sendMessage(String message){
        if (client != null) {
            client.sendMessage(message);
            Log.i(Constants.LOG_TAG, "TCP SEND: " + message);

        }else{
            Log.i(Constants.LOG_TAG, "TCP NOT SEND MSG");
        }
    }

    public String getMessage(){
        String result = null;
        if (client != null) {
            result = client.readMessage();

        }else{
            Log.i(Constants.LOG_TAG, "TCP NOT GET MSG");
        }
        return result;
    }


}
