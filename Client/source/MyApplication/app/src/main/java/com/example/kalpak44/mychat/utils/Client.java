package com.example.kalpak44.mychat.utils;

import android.content.Context;
import android.util.Log;

import com.example.kalpak44.mychat.constants.DefaultConfigs;
import com.example.kalpak44.mychat.constants.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by kalpak44 on 15-8-7.
 */
public class Client implements Runnable {
    private String serverMessage;
    private boolean mRun = false;
    private PrintWriter out;
    private BufferedReader in;
    private String connectionStatus;
    private Settings settings;


    public Client(Context context){
        MyChat app = (MyChat) context;
        settings = app.getSettings();
        connectionStatus = Constants.NOT_CONNECTED;
    }

    public void stopClient() {
        mRun = false;
    }


    /**
     * Sends the message entered by client to the server
     *
     * @param message
     *            text entered by client
     */
    public void sendMessage(String message) {
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }

    public String readMessage(){
        try {
            serverMessage = in.readLine();
        } catch (IOException e) {
            Log.i(Constants.LOG_TAG, "S: Error", e);
        }
        return serverMessage;
    }

    public String getConnctionStatus(){
        return connectionStatus;
    }

    public void run(){

        mRun = true;

        try {



            // here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(settings.getServerIp());
            Log.i(Constants.LOG_TAG, serverAddr.toString());
            Log.i(Constants.LOG_TAG, "Server: Connecting...");

            // create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, settings.getServerPort());

            try {

                // send the message to the server
                out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);

                // receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));

                // update connection status
                connectionStatus = Constants.STATUS_SUCCESS;
                /*
                serverMessage = in.readLine();
                mMessageListener.messageReceived(serverMessage);

                */
                while (mRun) {}


            } catch (Exception e) {
                // update connection status
                connectionStatus = e.getMessage();
            } finally {
                // the socket must be closed. It is not possible to reconnect to
                // this socket
                // after it is closed, which means a new socket instance has to
                // be created.
                socket.close();
            }

        } catch (Exception e) {
            // update connection status
            connectionStatus = e.getMessage();
        }

    }

}
