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

    @Override
    public void onCreate() {
        Log.i(Constants.LOG_TAG, "onCreate SERVICE");
        super.onCreate();
        client = new Client();
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

        return START_REDELIVER_INTENT;
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
                    for(int i = 0;i < Config.CONNECTION_ATTEMPS;i++){
                        Log.i(Constants.LOG_TAG, "try connection.");
                        Thread.sleep(Config.CONNECTION_WAIT);
                        if(client.getConnctionStatus().equals(Constants.STATUS_SUCCESS)){

                            break;
                        }
                    }
                    Log.i(Constants.LOG_TAG, "connection status: " + client.getConnctionStatus());
                    Intent resultIntent = new Intent(Constants.BROADCAST_ACTION);
                    resultIntent.putExtra(Constants.PARAM_CONNECTION_RESULT, client.getConnctionStatus());
                    sendBroadcast(resultIntent);




                }if(task.equals(Constants.PARAM_AUTH)){
                    sendMessage(Constants.PARAM_AUTH);
                    Log.i(Constants.LOG_TAG, getMessage());
                    JSONObject user = new JSONObject();

                    user.putOpt(Constants.USERNAME, intent.getStringExtra(Constants.USERNAME));
                    user.putOpt(Constants.PASSWORD, intent.getStringExtra(Constants.PASSWORD));
                    String userJSONData = user.toString();

                    sendMessage(userJSONData);
                    String authStatus = getMessage();
                    Log.i(Constants.LOG_TAG, "TCP MSG " + authStatus);
                    Intent intent = new Intent(Constants.BROADCAST_ACTION);
                    if(authStatus.equals(Constants.SERVER_STATUS_AUTH_SUCCESS)){
                        intent.putExtra(Constants.PARAM_AUTH_RESULT, Constants.STATUS_SUCCESS);
                    }else{
                        intent.putExtra(Constants.PARAM_AUTH_RESULT, Constants.STATUS_FAIL);
                    }
                    sendBroadcast(intent);



                }


                //checkProfile(intent.getStringExtra("username"), intent.getStringExtra("password"));

                TimeUnit.SECONDS.sleep(20);





                stop();

            }catch (Exception e){
                Log.i(Constants.LOG_TAG, "MYRUN Exception " + e.getMessage());
            }
        }

        public void stop(){
            stopSelf(startId);
        }

    }



    public void connection(){
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
