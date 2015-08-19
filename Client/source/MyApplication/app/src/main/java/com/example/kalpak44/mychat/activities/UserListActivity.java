package com.example.kalpak44.mychat.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kalpak44.mychat.R;
import com.example.kalpak44.mychat.constants.Constants;
import com.example.kalpak44.mychat.constants.Strings;
import com.example.kalpak44.mychat.models.User;
import com.example.kalpak44.mychat.utils.MyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kalpak44 on 15-7-29.
 */
public class UserListActivity extends Activity {
    private ListView usersListView;
    private List<User> userList;
    private ArrayAdapter<User> adapter;

    UserUpdater userlistUpdater;
    BroadcastReceiver br1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list_layout);
        usersListView = (ListView) findViewById(R.id.users);



        userlistUpdater = new UserUpdater();
        userlistUpdater.execute();
        //initUserList();
        initUserListView();
        registerClickCallback();


    }


    class UserUpdater extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            userList = new ArrayList<User>();

            super.onPreExecute();
        }

        private void setUserByUsername(String username,User user){
            for(int i=0;i<userList.size();i++){
                User current = userList.get(i);
                if(current.getUsername().equals(username)){
                    userList.set(i,user);
                }
            }
        }


        @Override
        protected Void doInBackground(Void... values) {
            try {


                while(true){

                    br1 = new BroadcastReceiver(){
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            String recv = intent.getStringExtra(Constants.PARAM_USERLIST_RESULT);

                            if(recv != null){
                                Log.i(Constants.LOG_TAG, "onReceive: JSON string = " + recv);



                                try {
                                    JSONArray jsonArray = new JSONArray(recv);
                                    userList.clear();
                                    for(int i=0;i<jsonArray.length();i++){
                                        JSONObject user = (JSONObject)jsonArray.get(i);
                                        Iterator<String> keys = user.keys();
                                        String username = keys.next();

                                        int msgCount = user.optInt(username);
                                        userList.add(new User(username, R.drawable.user_0, msgCount));
                                    }
                                    publishProgress();


                                    try {
                                        if (br1 != null) {
                                            unregisterReceiver(br1);
                                        }
                                    } catch (IllegalArgumentException ex) {
                                        br1 = null;
                                    }

                                } catch (JSONException e) {
                                    try {
                                        if (br1 != null) {
                                            unregisterReceiver(br1);
                                        }
                                    } catch (IllegalArgumentException ex) {
                                        br1 = null;
                                    }
                                }

                            }

                        }
                    };
                    registerReceiver(br1, new IntentFilter(Constants.BROADCAST_ACTION));



                    Intent intent = new Intent(getApplicationContext(), MyService.class)
                            .putExtra(Constants.PARAM_TASK, Constants.PARAM_USERLIST);
                    // стартуем сервис
                    startService(intent);
                    Thread.sleep(10000);
                }









            } catch (InterruptedException e) {
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            adapter.notifyDataSetChanged();
            super.onProgressUpdate(values);
        }
    }


    private void registerClickCallback() {
        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                User userClicked = userList.get(position);
                String message = "Position " + position;
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(getApplicationContext(), MessageRoomActivity.class);
                startActivity(new Intent(getApplicationContext(), MessageRoomActivity.class));
            }
        });
    }

    private void initUserListView() {
        adapter = new MyListAdapter();
        usersListView = (ListView) findViewById(R.id.users);
        usersListView.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<User>{

        public MyListAdapter() {
            super(UserListActivity.this, R.layout.item_users_view, userList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make suhre we have a view to work with (May have been given null)
            View itemView = convertView;
            if(itemView==null){
                itemView = getLayoutInflater().inflate(R.layout.item_users_view, parent, false);
            }
            // find the user to work with
            User currentUser = userList.get(position);

            //fill the view
            //userTextAvatar
            ImageView userImageView = (ImageView)itemView.findViewById(R.id.userImageView);
            userImageView.setImageResource(currentUser.getPict_id());

            //userTextView
            TextView userTextView = (TextView) itemView.findViewById(R.id.userTextView);
            userTextView.setText(currentUser.getUsername());

            ImageView imageViewMsg = (ImageView)itemView.findViewById(R.id.imageViewMsg);
            imageViewMsg.setVisibility(currentUser.getCount_msg()>0?View.VISIBLE:View.INVISIBLE);
            //imageViewMsg.setVisibility(View.INVISIBLE);

            return itemView;
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

    private void logout() {
        Intent intent = new Intent(getApplicationContext(), MyService.class)
                .putExtra(Constants.PARAM_TASK, Constants.PARAM_LOGOUT);
        // стартуем сервис
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        userlistUpdater.cancel(true);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_userlist, menu);
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
        if (id == R.id.action_logout) {
            logout();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            return true;
        }
        if (id == R.id.action_test) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
