package com.example.kalpak44.mychat.models;

/**
 * Created by kalpak44 on 15-7-31.
 */
public class User {
    private String username;
    private int pict_id;
    private int count_msg;
    private String status;
    public User(String username, int pict_id, int count_msg, String status){
        super();
        this.username = username;
        this.pict_id = pict_id;
        this.count_msg = count_msg;
        this.status = status;
    }
    public String getUsername(){
        return this.username;
    }
    public String getStatus(){
        return this.status;
    }
    public int getPict_id(){
        return this.pict_id;
    }
    public int getCount_msg(){
        return this.count_msg;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public void setPict_id(int pict_id){
        this.pict_id = pict_id;
    }
    public void setCount_msg(int count_msg){
        this.count_msg = count_msg;
    }
    public void setStatus(String status){
        this.status = status;
    }

}
