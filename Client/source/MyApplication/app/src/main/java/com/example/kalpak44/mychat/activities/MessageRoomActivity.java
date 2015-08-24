package com.example.kalpak44.mychat.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kalpak44.mychat.R;
import com.example.kalpak44.mychat.constants.DefaultConfigs;
import com.example.kalpak44.mychat.constants.Constants;
import com.example.kalpak44.mychat.models.Message;
import com.example.kalpak44.mychat.utils.MyChat;
import com.example.kalpak44.mychat.utils.MyService;
import com.example.kalpak44.mychat.utils.Settings;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class MessageRoomActivity extends Activity {
    private ListView mList;
    private EditText msgTextEdit;
    private Button send_button;
    private DBHelper dbHelper;
    private String receiver, sendler;
    private ArrayList<Message> arrayList = new ArrayList<Message>();
    private ChatArrayAdapter adp;
    private SQLiteDatabase db;
    private MSGTask msgTask;
    private BroadcastReceiver br, br2;
    private Settings settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_room_layout);

        dbHelper = new DBHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();
        initMessageListView();

        Intent i = getIntent();
        if(i.getStringExtra("receiver") !=null){
            Log.i(Constants.LOG_TAG, "RECEIVER - "+i.getStringExtra("receiver"));
            receiver = i.getStringExtra("receiver");
            if(MyService.username != null){
                sendler = MyService.username;
            }
        }
        MyChat app =(MyChat) getApplicationContext();
        settings = app.getSettings();

        send_button = (Button) findViewById(R.id.send_button);
        msgTextEdit = (EditText)findViewById(R.id.msgTextEdit);


        msgTextEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) &&(keyCode == KeyEvent.KEYCODE_ENTER)){
                    return sendMessage();
                }
                return false;
            }
        });

        send_button.setOnClickListener(new View.OnClickListener(

        ) {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        mList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mList.setAdapter(adp);
        adp.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mList.setSelection(adp.getCount() - 1);
            }
        });


        msgTask = (MSGTask) getLastNonConfigurationInstance();
        if(msgTask == null){
            try {
                msgTask = new MSGTask();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                    Log.i(Constants.LOG_TAG, "Async exec on thread pool");
                    msgTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                else {
                    Log.i(Constants.LOG_TAG, "Async exec");
                    msgTask.execute();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }



    }


    public Object onRetainNonConfigurationInstance(){
        return msgTask;
    }




    private void initMessageListView() {
        adp = new ChatArrayAdapter(getApplicationContext(),R.layout.message_room_layout,arrayList);
        mList = (ListView)findViewById(R.id.list);
    }

    private boolean sendMessage() {
        String messageText = msgTextEdit.getText().toString();
        if (!messageText.equals("")){
            //Objects for Data
            ContentValues cv = new ContentValues();





            br = new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {
                    String status = intent.getStringExtra(Constants.PARAM_SENDING_RESULT);

                    if(status != null){
                        Log.i(Constants.LOG_TAG, "onReceive sending: status = " + status);
                        try {
                            if (br != null) {
                                unregisterReceiver(br);
                            }
                        } catch (IllegalArgumentException e) {
                            br = null;
                        }
                    }else{
                        try {
                            if (br != null) {
                                unregisterReceiver(br);
                            }
                        } catch (IllegalArgumentException e) {
                            br = null;
                        }
                    }

                }
            };

            // регистрируем (включаем) BroadcastReceiver
            registerReceiver(br, new IntentFilter(Constants.BROADCAST_ACTION));


            // стартуем сервис
            startService(new Intent(getApplicationContext(), MyService.class)
                    .putExtra(Constants.PARAM_TASK, Constants.PARAM_SENDMSG)
                    .putExtra(Constants.TO, receiver)
                    .putExtra(Constants.MSG_TEXT, messageText));


            //Db connect



            cv.put("msg_text", messageText);
            cv.put("left", 1);
            cv.put("room", receiver);
            cv.put("logined", sendler);

            // db,insert(table,if is null,CV object)
            long rowId = db.insert("messages",null,cv);
            Log.i(Constants.LOG_TAG, "row inserted - " + rowId);

            adp.add(new Message(false, messageText));

            msgTextEdit.setText("");
            //left = !left;
            return true;
        }
        Log.i(Constants.LOG_TAG, "row not inserted");
        return false;

    }




    class MSGTask extends AsyncTask<Void,JSONArray,Void>{


        @Override
        protected void onPreExecute() {
            Log.i(Constants.LOG_TAG, "onPreExecute");

            Cursor c = db.rawQuery("select * from messages where room = ? and logined = ?",new String[]{receiver, sendler});

            if(c.moveToFirst()){

                int leftColIndex = c.getColumnIndex("left");
                int messageTextColIndex = c.getColumnIndex("msg_text");


                do{
                    adp.add(new Message(c.getInt(leftColIndex)!=1?true:false, c.getString(messageTextColIndex)));
                }while(c.moveToNext());



            }else{
                Log.i(Constants.LOG_TAG, "sqlite select msg: 0 rows");
            }

            super.onPreExecute();
        }



        @Override
        protected Void doInBackground(Void... params) {
            while(true){
                try {
                    if(isCancelled()){
                        return null;
                    }


                    br2 = new BroadcastReceiver(){
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            String status = intent.getStringExtra(Constants.PARAM_RECV_MSG_RESULT);

                            if(status != null){
                                Log.i(Constants.LOG_TAG, "onReceive get msg: status = " + status);
                                if(!status.equals(Constants.SERVER_STATUS_NO_MSG)){
                                    //textViewInvalidData.setText("");
                                    try {
                                        publishProgress(new JSONArray(status));
                                    } catch (JSONException e) {
                                        Log.i(Constants.LOG_TAG, "onReceive not parse");
                                    }
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

                    // стартуем сервис
                    startService(new Intent(getApplicationContext(), MyService.class)
                            .putExtra(Constants.PARAM_TASK, Constants.PARAM_GETMSG)
                            .putExtra(Constants.RECEIVER, receiver));
                    Thread.sleep(settings.getMsgListRefreshTime());
                } catch (Exception e) {

                }
            }
        }

        @Override
        protected void onProgressUpdate(JSONArray... array) {
            Log.i(Constants.LOG_TAG, "onProgressUpdate");
            for(int i=0;i<array[0].length();i++){
                try {
                    String message = array[0].getString(i);
                    ContentValues cv = new ContentValues();
                    cv.put("msg_text", message);
                    cv.put("room", receiver);
                    cv.put("left", 0);
                    cv.put("logined",sendler);

                    // db,insert(table,if is null,CV object)
                    long rowId = db.insert("messages",null,cv);
                    Log.i(Constants.LOG_TAG, "row inserted - " + rowId);

                    adp.add(new Message(true, message));
                } catch (JSONException e) {
                    Log.i(Constants.LOG_TAG, "onProgressUpdate parser error");
                }
            }
            super.onProgressUpdate(array);
        }
    }


    private class ChatArrayAdapter extends ArrayAdapter<Message>{
        private TextView msgText;
        private List<Message> MessageList = new ArrayList<Message>();
        private LinearLayout layout;

        public ChatArrayAdapter(Context context, int resource, List objects) {
            super(context, resource, objects);
        }

        public void add(Message message) {
            MessageList.add(message);
            super.add(message);
        }

        @Override
        public int getCount() {
            return this.MessageList.size();
        }

        @Override
        public Message getItem(int position) {
            return this.MessageList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if(v==null){
                LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.item_message_view,parent,false);

            }
            layout = (LinearLayout) v.findViewById(R.id.Message1);
            Message message = getItem(position);
            msgText = (TextView) v.findViewById(R.id.msgText);
            msgText.setText(message.getMessage());
            msgText.setGravity(message.getLeft()? Gravity.LEFT:Gravity.RIGHT);
            //layout.setGravity(message.getLeft()? Gravity.LEFT:Gravity.RIGHT);
            //msgListItemText.setGravity(Gravity.RIGHT);
            //layout.setGravity(message.getLeft()? Gravity.LEFT:Gravity.RIGHT);
            //msgTextEdit.setBackgroundColor(Color.GRAY);
            ((LinearLayout.LayoutParams) layout.getLayoutParams()).gravity = message.getLeft()? Gravity.LEFT:Gravity.RIGHT;
            layout.setBackgroundColor(message.getLeft()?Color.BLUE:Color.GRAY);


            return v;

        }
    }


    class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context){
            //Context, DB name, Cursor, DB version
            super(context, DefaultConfigs.DB_NAME,null,1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(Constants.LOG_TAG, "onCreate DB Helper: init tables");
            db.execSQL("create table messages (" +
                    "id integer primary key autoincrement," +
                    "left integer," +
                    "room text,"+
                    "msg_text text,"+
                    "logined text"+
                            ");"
            );

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }


    @Override
    protected void onPause() {
        Log.i(Constants.LOG_TAG, "onPause");
        msgTask.cancel(true);
        try {
            if (br != null) {
                unregisterReceiver(br);
            }
            if (br2 != null) {
                unregisterReceiver(br2);
            }

        } catch (IllegalArgumentException e) {
            br = null;
            br2 = null;
        }
        super.onPause();
    }
}
