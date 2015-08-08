package com.example.kalpak44.mychat.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
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
import com.example.kalpak44.mychat.models.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageRoomActivity extends Activity {
    private ListView mList;
    private EditText msgTextEdit;
    private Button send_button;
    Intent in;
    private  boolean left = false;

    private ArrayList<Message> arrayList = new ArrayList<Message>();
    private ChatArrayAdapter adp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_room_layout);
        initMessageListView();




        Intent i = getIntent();
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
                mList.setSelection(adp.getCount()-1);
            }
        });

    }

    private void initMessageListView() {
        adp = new ChatArrayAdapter(getApplicationContext(),R.layout.message_room_layout,arrayList);
        mList = (ListView)findViewById(R.id.list);
    }

    private boolean sendMessage() {
        adp.add(new Message(left, msgTextEdit.getText().toString()));
        msgTextEdit.setText("");
        left = !left;
        return true;
    }

















    private class ChatArrayAdapter extends ArrayAdapter<Message>{
        private TextView msgListItemText;
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
            //layout = (LinearLayout) v.findViewById(R.id.Message1);
            Message message = getItem(position);
            msgListItemText = (TextView) v.findViewById(R.id.msgListItemText);
            msgListItemText.setText(message.getMessage() + (message.getLeft() ? " left" : " right"));
            msgListItemText.setGravity(message.getLeft()? Gravity.LEFT:Gravity.RIGHT);
            //msgListItemText.setGravity(Gravity.RIGHT);
            //layout.setGravity(message.getLeft()? Gravity.LEFT:Gravity.RIGHT);
            //msgTextEdit.setBackgroundColor(Color.GRAY);

            return v;

        }
    }












}
