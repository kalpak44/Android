package com.example.kalpak44.mychat.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.kalpak44.mychat.models.User;
import com.example.kalpak44.mychat.utils.MyService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalpak44 on 15-7-29.
 */
public class UserListActivity extends Activity {
    private ListView usersListView;
    private List<User> userList;
    private ArrayAdapter<User> adapter;

    UserUpdater userlistUpdater;

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
                publishProgress();
                Thread.sleep(2000);
                userList.add(new User("User 1", R.drawable.user_0, 0));
                userList.add(new User("User 2", R.drawable.user_0, 1));
                userList.add(new User("User 3", R.drawable.user_0, 0));
                userList.add(new User("User 4", R.drawable.user_0, 0));
                publishProgress();
                Thread.sleep(2000);
                userList.add(new User("User 5", R.drawable.user_0, 0));
                publishProgress();
                Thread.sleep(2000);
                setUserByUsername("User 3", new User("User 3", R.drawable.user_0, 22));
                publishProgress();
                Thread.sleep(2000);
                userList.remove(4);
                publishProgress();
                Thread.sleep(2000);
                userList.add(new User("User 6", R.drawable.user_0, 0));
                userList.add(new User("User 7", R.drawable.user_0, 1));
                publishProgress();



            } catch (InterruptedException e) {
                e.printStackTrace();
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

    private void initUserList(){
        this.userList = new ArrayList<User>();
        userList.add(new User("User 1",R.drawable.user_0,0));
        userList.add(new User("User 2",R.drawable.user_0,1));
        userList.add(new User("User 3", R.drawable.user_0, 0));
        userList.add(new User("User 4", R.drawable.user_0, 8));
        userList.add(new User("User 5", R.drawable.user_0, 6));
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
        logout();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        super.onBackPressed();
    }

    private void logout() {
        Intent intent = new Intent(getApplicationContext(), MyService.class)
                .putExtra(Constants.PARAM_TASK, Constants.PARAM_LOGOUT);
        // стартуем сервис
        startService(intent);
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
        return super.onOptionsItemSelected(item);
    }
}
