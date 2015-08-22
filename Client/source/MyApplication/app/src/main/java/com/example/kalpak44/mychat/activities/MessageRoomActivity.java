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
import android.widget.Toast;

import com.example.kalpak44.mychat.R;
import com.example.kalpak44.mychat.constants.Config;
import com.example.kalpak44.mychat.constants.Constants;
import com.example.kalpak44.mychat.constants.Strings;
import com.example.kalpak44.mychat.models.Message;
import com.example.kalpak44.mychat.utils.MyService;

import java.util.ArrayList;
import java.util.List;

public class MessageRoomActivity extends Activity {
    private ListView mList;
    private EditText msgTextEdit;
    private Button send_button;
    private boolean left = false;
    private DBHelper dbHelper;
    private  String receiver;
    private ArrayList<Message> arrayList = new ArrayList<Message>();
    private ChatArrayAdapter adp;
    private SQLiteDatabase db;
    private MSGTask msgTask;
    private BroadcastReceiver br;


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
        }


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
        if(msgTask ==null){
            msgTask = new MSGTask();
            msgTask.execute();
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



            cv.put("message", messageText);
            cv.put("receiver", receiver);
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




    private class MSGTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {

            Cursor c = db.rawQuery("select * from messages where receiver = ? or receiver = ?",new String[]{"I",receiver});

            if(c.moveToFirst()){

                int idColumnIndex = c.getColumnIndex("id");
                int receiverColIndex = c.getColumnIndex("receiver");
                int messageTextColIndex = c.getColumnIndex("message");


                do{
                    adp.add(new Message(false, c.getString(messageTextColIndex)));
                }while(c.moveToNext());



            }else{
                Log.i(Constants.LOG_TAG, "sqlite select msg: 0 rows");
            }

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (!isCancelled()){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
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
            super(context, Config.DB_NAME,null,1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(Constants.LOG_TAG, "onCreate DB Helper: init tables");
            db.execSQL("create table messages (" +
                    "id integer primary key autoincrement," +
                    "receiver text," +
                    "message text"+
                            ");"
            );

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    @Override
    protected void onPause() {
        try {
            if (br != null) {
                unregisterReceiver(br);
            }
        } catch (IllegalArgumentException e) {
            br = null;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        msgTask.cancel(true);
        super.onDestroy();
    }
}
